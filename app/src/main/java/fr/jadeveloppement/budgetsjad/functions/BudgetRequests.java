package fr.jadeveloppement.budgetsjad.functions;

import static java.util.Objects.isNull;
import static fr.jadeveloppement.budgetsjad.functions.Variables.URL_ADD_TRANSACTION;
import static fr.jadeveloppement.budgetsjad.functions.Variables.URL_CHECKTOKEN;
import static fr.jadeveloppement.budgetsjad.functions.Variables.URL_DELETEDATA;
import static fr.jadeveloppement.budgetsjad.functions.Variables.URL_EXPORTDATA;
import static fr.jadeveloppement.budgetsjad.functions.Variables.URL_LOGIN;
import static fr.jadeveloppement.budgetsjad.functions.Variables.URL_RETRIEVEDATA;
import static fr.jadeveloppement.budgetsjad.functions.Variables.amountUrlField;
import static fr.jadeveloppement.budgetsjad.functions.Variables.datasUrlField;
import static fr.jadeveloppement.budgetsjad.functions.Variables.labelUrlField;
import static fr.jadeveloppement.budgetsjad.functions.Variables.loginUrlField;
import static fr.jadeveloppement.budgetsjad.functions.Variables.passwordUrlField;
import static fr.jadeveloppement.budgetsjad.functions.Variables.tokenUrlField;
import static fr.jadeveloppement.budgetsjad.functions.Variables.transactionIdField;
import static fr.jadeveloppement.budgetsjad.functions.Variables.typeUrlField;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import fr.jadeveloppement.budgetsjad.functions.api.ApiURLBuilder;
import fr.jadeveloppement.budgetsjad.functions.api.DefaultApiErrorHandler;
import fr.jadeveloppement.budgetsjad.functions.interfaces.BudgetRequestsInterface;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class BudgetRequests {

    private final String TAG = "JADBudget";

    private final Context context;
    private BudgetRequestsInterface callback;
    private Functions functions;
    private String login, password;
    private String token;
    private DefaultApiErrorHandler defaultApiErrorHandler;
    private ApiURLBuilder apiURLBuilder;

    /**
     * Do not use this constructor
     * The best way is to use BudgetRequests(Context, Login, Password, Interface callback)
     * @param c : context
     */
    public BudgetRequests(@NonNull Context c){
        this.context = c.getApplicationContext();
    }

    /**
     * To start the call to the JADeveloppement API
     *
     * @param c : context
     * @param l : login
     * @param p : password
     * @param call : interface when requests are done that will be called
     */
    public BudgetRequests(@NonNull Context c, @NonNull String l, @NonNull String p, @NonNull BudgetRequestsInterface call){
        this.context = c.getApplicationContext();
        this.login = l;
        this.password = p;
        this.functions = new Functions(context);
        this.callback = call;
        this.defaultApiErrorHandler = new DefaultApiErrorHandler(context, callback);
        this.apiURLBuilder = new ApiURLBuilder();
    }

    /**
     * Handler if API send an error : 400/401/500
     * @param error : volleyError catched by App
     * @param type : type of request done when catching the exception
     */
    public void volleyErrorHandler(@NonNull VolleyError error, @NonNull Enums.ErrorRequest type){
        NetworkResponse networkResponse = error.networkResponse;
        if (!isNull(networkResponse)){
            int statuscode = networkResponse.statusCode;
            switch(statuscode){
                case 400:
                case 401:
                    functions.makeToast("Session expirée ou mauvais identifiants.");
                    break;
                case 500:
                    functions.makeToast("Erreur serveur ("+statuscode+")");
                    break;
                default:
                    Log.d(TAG, "BudgetRequests > checkToken: statusCode : " + statuscode);
                    break;
            }
        }

        if (type == Enums.ErrorRequest.TOKEN_NON_OK) callback.tokenNonOk();
        else if (type == Enums.ErrorRequest.LOGIN_NON_OK) callback.loginNonOk();
        else if (type == Enums.ErrorRequest.EXPORT_ERROR) callback.loginNonOk();
        else if (type == Enums.ErrorRequest.IMPORT_ERROR) callback.loginNonOk();
        else if (type == Enums.ErrorRequest.ALL_DATA_ERROR) callback.loginNonOk();
        else if (type == Enums.ErrorRequest.DELETE_TRANSACTION_ERROR) callback.tokenNonOk();
        else if (type == Enums.ErrorRequest.ADD_TRANSACTION_ERROR) callback.tokenNonOk();
    }

    /**
     * Check validity of token
     */
    public void checkToken(@NonNull String login, @NonNull String token) {
        String URL = apiURLBuilder.build(URL_CHECKTOKEN, loginUrlField, login, tokenUrlField, token);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    callback.tokenOk();
                },
                error -> {
                    Functions.handleExceptions("BudgetRequests > checkToken : ", error);
                    defaultApiErrorHandler.handleError(error, Enums.ErrorRequest.TOKEN_NON_OK);
                }
        );

        putToQueue(jsonObjectRequest);
    }

    /**
     * Test if login credentials are correct.
     * To call just after the declaration of BudgetRequests(Context, Login, Password, Callback)
     */
    public void handleLogin(){
        String URL = apiURLBuilder.build(URL_LOGIN, loginUrlField, login, passwordUrlField, password);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        if (response.getString("logged").equals("1")){
                            this.token = response.getString("token");
                            SettingsTable settingUser = functions.getSettingByLabel(Variables.settingUsername);
                            settingUser.value = login;
                            functions.updateSettings(settingUser);

                            SettingsTable settingPassword = functions.getSettingByLabel(Variables.settingPassword);
                            settingPassword.value = password;
                            functions.updateSettings(settingPassword);

                            SettingsTable settingToken = functions.getSettingByLabel(Variables.settingsToken);
                            settingToken.value = token;
                            functions.updateSettings(settingToken);

                            callback.loginOk();
                        }
                        else functions.makeToast("Mauvais identifiant.");
                    } catch(JSONException e){
                        functions.makeToast("Une erreur serveur est survenue.");
                        callback.loginNonOk();
                        Functions.handleExceptions("BudgetRequests > try/catch > handleLogin : ", e);
                    }
                },
                error -> {
                    Functions.handleExceptions("BudgetRequests > handleLogin : ", error);
                    defaultApiErrorHandler.handleError(error, Enums.ErrorRequest.LOGIN_NON_OK);
                }
        );

        putToQueue(jsonObjectRequest);

    }

    /**
     * Save data into database, linked to user credentials
     * @param t : token sent by the handleLogin
     * @param datas : datas to send to be parsed by API
     */
    public void makeExportDatas(@NonNull String t, @NonNull String datas){
        String URL = apiURLBuilder.build(URL_EXPORTDATA, loginUrlField, login, passwordUrlField, password, tokenUrlField, t, datasUrlField, datas);

        Log.d(TAG, "BudgetRequests > makeSaveDatas: URL > " + URL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    callback.datasSaved();
                },
                error -> {
                    Functions.handleExceptions("BudgetRequests > makeSaveDatas : ", error);
                    volleyErrorHandler(error, Enums.ErrorRequest.EXPORT_ERROR);
                }
        );

        putToQueue(jsonObjectRequest);

    }

    /**
     * Allow to import datas given their datatype
     * @param dataToRequest : datatype to request
     * @param t : token
     * @param totalRequests : total of datatype to retrieve
     * @param indexRequest : index of current datatype being retrieved
     */
    private void importDataType(@NonNull Enums.DataToRequest dataToRequest, String t, int totalRequests, int indexRequest){
        String URL = apiURLBuilder.build(URL_RETRIEVEDATA, loginUrlField, login, passwordUrlField, password, tokenUrlField, t, typeUrlField, String.valueOf(dataToRequest));

        Log.d(TAG, "makeImportDatas: URL > " + URL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        callback.datasImported(response);
                        if (indexRequest == totalRequests-1) {
                            callback.requestsFinished();
                        }
                    } catch(Exception e){
                        Functions.handleExceptions("BudgetRequests > makeImportDatas : ", e);
                    }
                },
                error -> {
                    Functions.handleExceptions("BudgetRequests > makeImportDatas : ", error);
                    volleyErrorHandler(error, Enums.ErrorRequest.IMPORT_ERROR);
                }
        );

        putToQueue(jsonObjectRequest);
    }

    /**
     * Import datas from database, linked to the user credentials
     * @param t : token sent by the handleLogin
     */
    public void makeImportDatas(@NonNull String t, @NonNull List<Enums.DataToRequest> datas){
        int index = 0;
        for (Enums.DataToRequest d : datas){
            importDataType(d, t, datas.size(), index);
            index++;
        }
    }

    /**
     * Retrieve all rows of transaction table for user connected from distant DB
     * @param token : token of the user connected
     */
    public void retrieveAllTransactions(String token) {
        String URL = apiURLBuilder.build(URL_RETRIEVEDATA, loginUrlField, login, passwordUrlField, password, tokenUrlField, token, typeUrlField, String.valueOf(Enums.DataToRequest.ALL_DATAS));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
                try {
                    String datasResponse = response.getString("datas");
                    ArrayList<Transaction> listOfTransaction = null;
                    if (datasResponse.contains("<n>") && datasResponse.contains("<l>")){
                        listOfTransaction = new ArrayList<>();
                        for (String line : datasResponse.split("<n>")){
                            String[] cols = line.split("<l>");
                            listOfTransaction.add(new Transaction(
                                    cols[1],
                                    cols[2],
                                    cols[3],
                                    cols[4],
                                    cols[5],
                                    "",
                                    Functions.convertStrtypeToTransactionType(cols[6]),
                                    cols[7]
                            ));
                        }
                    }

                    callback.allDataRetrieved(listOfTransaction);

                } catch(Exception e){
                    Functions.handleExceptions("BudgetRequests > retrieveAllTransactions : ", e);
                }
            },
            error -> {
                volleyErrorHandler(error, Enums.ErrorRequest.ALL_DATA_ERROR);
            }
        );

        putToQueue(jsonObjectRequest);
    }

    /**
     * Delete a transaction for a user in distant DB
     * @param id : distant transaction_id to delete
     */
    public void deleteTransaction(String id) {
        if (isNull(token) || token.isBlank()) {
            Functions.makeSnakebar("Une erreur est survenue : token vide");
            Log.d(TAG, "BudgetRequests > deleteTransaction: empty token");
        }

        String URL = apiURLBuilder.build(URL_DELETEDATA, loginUrlField, login, passwordUrlField, password, tokenUrlField, token, transactionIdField, id);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            try {
                callback.requestsFinished();
            } catch(Exception e){
                Functions.handleExceptions("BudgetRequests > deleteTransaction : ", e);
            }
        },
                error -> {
                    volleyErrorHandler(error, Enums.ErrorRequest.DELETE_TRANSACTION_ERROR);
                }
        );

        putToQueue(jsonObjectRequest);
    }

    /**
     * Add transaction to distant DB
     * @param label : label of transaction
     * @param amount : amount of transaction
     * @param type : type of transaction
     */
    public void addTransaction(String label, String amount, String type) {
        if (type.isBlank()) type = String.valueOf(Enums.TransactionType.UNDEFINED);
        if (amount.isBlank()) amount = "0";
        if (label.isBlank()) label = "pas de label";

        String URL = apiURLBuilder.build(URL_ADD_TRANSACTION, loginUrlField, login, passwordUrlField, password, tokenUrlField, token, labelUrlField, label, amountUrlField, amount, typeUrlField, type);
        Log.d(TAG, "addTransaction: " + URL);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            try {
                callback.requestsFinished();
            } catch(Exception e){
                Functions.handleExceptions("BudgetRequests > addTransaction : ", e);
            }
        },
                error -> {
                    volleyErrorHandler(error, Enums.ErrorRequest.ADD_TRANSACTION_ERROR);
                }
        );

        putToQueue(jsonObjectRequest);
    }

    /**
     * Put request to queue
     * @param jsonObjectRequest : jsonobject to send to API
     */
    private void putToQueue(@NonNull JsonObjectRequest jsonObjectRequest) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }
}
