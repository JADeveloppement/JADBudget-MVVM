package fr.jadeveloppement.budgetsjad.functions;

import static java.util.Objects.isNull;
import static fr.jadeveloppement.budgetsjad.functions.Variables.URL_EXPORTDATA;
import static fr.jadeveloppement.budgetsjad.functions.Variables.URL_LOGIN;
import static fr.jadeveloppement.budgetsjad.functions.Variables.URL_RETRIEVEDATA;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.Arrays;

import fr.jadeveloppement.budgetsjad.functions.interfaces.BudgetRequestsInterface;

public class BudgetRequests {

    private final String TAG = "JADBudget";

    private final Context context;
    private BudgetRequestsInterface callback;
    private Functions functions;
    private String login, password;
    private RequestQueue requestQueue;
    private String token;

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
    }

    /**
     * Test if login credentials are correct.
     * To call just after the declaration of BudgetRequests(Context, Login, Password, Callback)
     */
    public void handleLogin(){
        if (login.isBlank() || password.isBlank()) {
            functions.makeToast("Veuillez renseigner tous les champs.");
            callback.loginNonOk();
            return;
        }

        String URL = URL_LOGIN + "login=" + login + "&password=" + password;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        if (response.getString("logged").equals("1")){
                            this.token = response.getString("token");
                            callback.loginOk(token);
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
                    NetworkResponse networkResponse = error.networkResponse;
                    if (!isNull(networkResponse)){
                        int statuscode = networkResponse.statusCode;
                        switch(statuscode){
                            case 400:
                            case 500:
                                functions.makeToast("Erreur serveur.");
                                break;
                            case 401:
                                functions.makeToast("Mauvais identifiants");
                                break;
                            default:
                                Log.d(TAG, "BudgetRequests > handleLogin: statusCode : " + statuscode);
                                break;
                        }
                    }
                    callback.loginNonOk();
                }
        );

        putToQueue(jsonObjectRequest);

    }

    private void putToQueue(JsonObjectRequest jsonObjectRequest) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Save data into database, linked to user credentials
     * @param t : token sent by the handleLogin
     * @param d : datas to send to be parsed by API
     */
    public void makeSaveDatas(@NonNull String t, @NonNull String d){
        if (t.isBlank() || d.isBlank()){
            functions.makeToast("Les données n'ont pas permis de vous identifier.");
            return;
        }

        String URL = URL_EXPORTDATA + "login=" + login + "&password=" + password + "&token=" + t + "&datas=" + d;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    callback.datasSaved();
                },
                error -> {
                    Functions.handleExceptions("BudgetRequests > makeSaveDatas : ", error);
                    callback.loginNonOk();
                    NetworkResponse networkResponse = error.networkResponse;
                    if (!isNull(networkResponse)){
                        int statuscode = networkResponse.statusCode;
                        switch(statuscode){
                            case 400:
                            case 500:
                                functions.makeToast("Erreur serveur ("+statuscode+")");
                                break;
                            case 401:
                                functions.makeToast("Erreur lors de l'identification de l'utilisateur.");
                                break;
                            default:
                                Log.d(TAG, "BudgetRequests > handleLogin: statusCode : " + statuscode);
                                break;
                        }
                    }
                }
        );

        putToQueue(jsonObjectRequest);

    }

    /**
     * Import datas from database, linked to the user credentials
     * @param t : token sent by the handleLogin
     */
    public void makeImportDatas(@NonNull String t, @NonNull Enums.DataToRequest d){
        if (t.isBlank()){
            functions.makeToast("Les données n'ont pas permis de vous identifier.");
            return;
        }

        String URL = URL_RETRIEVEDATA + "login=" + login + "&password=" + password + "&token=" + t + "&type="+ d;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        callback.datasImported(response);
                    } catch(Exception e){
                        Functions.handleExceptions("BudgetRequests > makeImportDatas : ", e);
                    }
                },
                error -> {
                    Functions.handleExceptions("BudgetRequests > makeImportDatas : ", error);
                    callback.loginNonOk();
                    NetworkResponse networkResponse = error.networkResponse;
                    if (!isNull(networkResponse)){
                        int statusCode = networkResponse.statusCode;
                        switch(statusCode){
                            case 400:
                            case 500:
                                functions.makeToast("Erreur serveur (" + statusCode + ")");
                                break;
                            case 401:
                                functions.makeToast("Erreur lors de l'identification (" + statusCode + ")");
                                break;
                            default:
                                Log.d(TAG, "BudgetRequests > handleLogin: statusCode : " + statusCode);
                                break;
                        }
                    }
                }
        );

        putToQueue(jsonObjectRequest);

    }
}
