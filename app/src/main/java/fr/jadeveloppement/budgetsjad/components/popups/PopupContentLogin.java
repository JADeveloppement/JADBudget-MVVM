package fr.jadeveloppement.budgetsjad.components.popups;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private TextView popupContentLoginTitleTv;
    private Button manageloginConnectBtn;

    private LinearLayout popupContentLoginClose;

    public PopupContentLogin(@NonNull Context c){
        super(c);
        this.context = c.getApplicationContext();
        this.viewParent = MainActivity.getViewRoot();
        this.popupLayout = LayoutInflater.from(context).inflate(R.layout.popup_managelogin_layout, (ViewGroup) viewParent, false);

        initPopup();
    }

    private void initPopup(){
        popupContentLoginClose = popupLayout.findViewById(R.id.popupContentLoginClose);
        LinearLayout manageloginLoadingScreen = popupLayout.findViewById(R.id.manageloginLoadingScreen);
        EditText manageloginLogin = popupLayout.findViewById(R.id.manageloginLogin);
        EditText manageloginPassword = popupLayout.findViewById(R.id.manageloginPassword);
        manageloginConnectBtn = popupLayout.findViewById(R.id.manageloginConnectBtn);
        popupContentLoginTitleTv = popupLayout.findViewById(R.id.popupContentLoginTitleTv);

        manageloginConnectBtn.setOnClickListener(v -> {
            String login = manageloginLogin.getText().toString();
            String password = manageloginPassword.getText().toString();

            if (login.isBlank() || password.isBlank()) Toast.makeText(context, "Veuillez renseigner tous les champs.", Toast.LENGTH_LONG).show();
            else {
                manageloginLoadingScreen.setVisibility(View.VISIBLE);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://jadeveloppement.fr/login?login="+login+"&password="+password, null,
                        response -> {
                            manageloginLoadingScreen.setVisibility(View.GONE);
                            try {
                                String logged = response.getString("logged");
                                if (logged.contains("1")){
                                    Log.d(TAG, "makeRequest: ok");
                                    String token = response.getString("token");
                                    (new Functions(context)).makeToast("Connecté");
                                } else {
                                    (new Functions(context)).makeToast("Mauvais identifiants");
                                    Log.d(TAG, "makeRequest: ok but bad logins");
                                }
                            } catch (JSONException e) {
                                (new Functions(context)).makeToast("Une erreur est survenue (-1).");
                                Log.d(TAG, "BudgetRequests > makeRequest > onResponse: " + e);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                manageloginLoadingScreen.setVisibility(View.GONE);
                                (new Functions(context)).makeToast("Une erreur est survenue (-2).");
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
