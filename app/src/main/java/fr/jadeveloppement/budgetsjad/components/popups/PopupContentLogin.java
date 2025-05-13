package fr.jadeveloppement.budgetsjad.components.popups;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.functions.Functions;

public class PopupContentLogin extends LinearLayout {

    private final String TAG = "JADABudget";

    private final Context context;
    private final View viewParent;
    private final View popupLayout;
    private final int typeOfAction;
    private TextView popupContentLoginTitleTv;
    private Button manageloginConnectBtn;
    private Functions functions;

    private LinearLayout popupContentLoginClose, popupContentLoginLoadingScreen;

    public PopupContentLogin(@NonNull Context c){
        super(c);
        this.context = c.getApplicationContext();
        this.viewParent = MainActivity.getViewRoot();
        this.popupLayout = LayoutInflater.from(context).inflate(R.layout.popup_managelogin_layout, (ViewGroup) viewParent, false);
        this.functions = new Functions(context);
        this.typeOfAction = 0;

        initPopup();
    }

    public PopupContentLogin(@NonNull Context c, int t){
        super(c);
        this.context = c.getApplicationContext();
        this.viewParent = MainActivity.getViewRoot();
        this.popupLayout = LayoutInflater.from(context).inflate(R.layout.popup_managelogin_layout, (ViewGroup) viewParent, false);
        this.functions = new Functions(context);
        this.typeOfAction = t;

        initPopup();
    }

    private EditText manageloginLogin, manageloginPassword;
    private CheckBox popupContentLoginInvoiceCb, popupContentLoginIncomeCb, popupContentLoginExpenseCb, popupContentLoginModelInvoiceCb, popupContentLoginModelIncomeCb;
    private void initPopup(){
        popupContentLoginClose = popupLayout.findViewById(R.id.popupContentLoginClose);
        manageloginLogin = popupLayout.findViewById(R.id.manageloginLogin);
        manageloginPassword = popupLayout.findViewById(R.id.manageloginPassword);
        manageloginConnectBtn = popupLayout.findViewById(R.id.manageloginConnectBtn);
        popupContentLoginTitleTv = popupLayout.findViewById(R.id.popupContentLoginTitleTv);
        popupContentLoginLoadingScreen = popupLayout.findViewById(R.id.popupContentLoginLoadingScreen);

        popupContentLoginInvoiceCb = popupLayout.findViewById(R.id.popupContentLoginInvoiceCb);
        popupContentLoginIncomeCb = popupLayout.findViewById(R.id.popupContentLoginIncomeCb);
        popupContentLoginExpenseCb = popupLayout.findViewById(R.id.popupContentLoginExpenseCb);
        popupContentLoginModelInvoiceCb = popupLayout.findViewById(R.id.popupContentLoginModelInvoiceCb);
        popupContentLoginModelIncomeCb = popupLayout.findViewById(R.id.popupContentLoginModelIncomeCb);

        manageloginConnectBtn.setOnClickListener(v -> {
            String login = manageloginLogin.getText().toString();
            String password = manageloginPassword.getText().toString();

            if (login.isBlank() || password.isBlank()) functions.makeToast("Veuillez renseigner tous les champs.");
            else if (!popupContentLoginInvoiceCb.isChecked() &&
                    !popupContentLoginIncomeCb.isChecked() &&
                    !popupContentLoginExpenseCb.isChecked() &&
                    !popupContentLoginModelInvoiceCb.isChecked() &&
                    !popupContentLoginModelIncomeCb.isChecked()) functions.makeToast("Veuillez cocher au moins un élément.");
            else {
                popupContentLoginLoadingScreen.setVisibility(View.VISIBLE);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://jadeveloppement.fr/api/login?login="+login+"&password="+password+"&type="+typeOfAction, null,
                        response -> {
                            popupContentLoginLoadingScreen.setVisibility(View.GONE);
                            try {
                                String logged = response.getString("logged");
                                if (logged.contains("1")){
                                    String token = response.getString("token");
                                    String datasBody = "&datas=";
                                    if (popupContentLoginInvoiceCb.isChecked())
                                        datasBody += "<n>"+functions.convertListToDatas(functions.getAllInvoicesTransaction());
                                    if (popupContentLoginIncomeCb.isChecked())
                                        datasBody += "<n>"+functions.convertListToDatas(functions.getAllIncomesTransaction());
                                    if (popupContentLoginExpenseCb.isChecked())
                                        datasBody += "<n>"+functions.convertListToDatas(functions.getAllExpensesTransaction());
                                    if (popupContentLoginModelIncomeCb.isChecked())
                                        datasBody += "<n>"+functions.convertListToDatas(functions.getModelIncomeTransaction());
                                    if (popupContentLoginModelInvoiceCb.isChecked())
                                        datasBody += "<n>"+functions.convertListToDatas(functions.getModelInvoiceTransaction());

                                    String loginBody = "login="+login+"&password="+password+"&token="+token;

                                    JsonObjectRequest modelRequest = new JsonObjectRequest(Request.Method.GET, "https://jadeveloppement.fr/api/updateModelInvoice?"+loginBody+datasBody, null,
                                            modelResponse -> {
                                                popupContentLoginLoadingScreen.setVisibility(View.GONE);
                                                Log.d(TAG, "makeRequest: " + modelResponse);
                                            },
                                            error -> {
                                                popupContentLoginLoadingScreen.setVisibility(View.GONE);
                                                functions.makeToast("Une erreur est survenue (-2).");
                                                Log.d(TAG, "BudgetRequests > makeRequest > onErrorResponse: " + error.toString());
                                            });

                                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                                    requestQueue.add(modelRequest);
                                } else {
                                    functions.makeToast("Mauvais identifiants");
                                    Log.d(TAG, "makeRequest: ok but bad logins");
                                }
                            } catch (JSONException e) {
                                functions.makeToast("Une erreur est survenue (-1).");
                                Log.d(TAG, "BudgetRequests > makeRequest > onResponse: " + e);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                popupContentLoginLoadingScreen.setVisibility(View.GONE);
                                functions.makeToast("Une erreur est survenue (-2).");
                                Log.d(TAG, "BudgetRequests > makeRequest > onErrorResponse: " + error.toString());
                            }
                        });

                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(jsonObjectRequest);
            }
        });
    }

    public void setPopupTitle(String title){
        popupContentLoginTitleTv.setText(title);
    }

    public void setPopupBtnText(String text){
        manageloginConnectBtn.setText(text);
    }

    public LinearLayout getBtnClose(){
        return popupContentLoginClose;
    }

    public LinearLayout getLayout(){
        return (LinearLayout) popupLayout;
    }
}
