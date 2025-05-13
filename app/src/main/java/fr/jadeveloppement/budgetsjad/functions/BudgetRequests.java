package fr.jadeveloppement.budgetsjad.functions;

import static java.util.Objects.isNull;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicReference;

public class BudgetRequests {

    private final String TAG = "JADBudget";

    private final Context context;
    private String login, password;

    public BudgetRequests(@NonNull Context c){
        this.context = c.getApplicationContext();
    }

    public BudgetRequests(@NonNull Context c, String l, String p){
        this.context = c;
        this.login = l;
        this.password = p;
    }

    public String makeRequest(){
        AtomicReference<String> result = new AtomicReference<>("");
        if (login.isBlank() || password.isBlank()) makeToast("Veuillez remplir tous les champs svp");
        else {
//            JSONObject requestBody = new JSONObject();
//            try {
//                requestBody.put("login", "test");
//                requestBody.put("password", "123456");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://jadeveloppement.fr/login?login="+login+"&password="+password, null,
                    response -> {
                        try {
                            String logged = response.getString("logged");
                            if (logged.contains("1")){
                                Log.d(TAG, "makeRequest: ok");
                                String token = response.getString("token");
                                result.set("1");
                            } else {
                                Log.d(TAG, "makeRequest: ok but bad logins");
                                result.set("0");
                            }
                        } catch (JSONException e) {
                            result.set("-1");
                            makeToast("Une erreur est survenue (-1).");
                            Log.d(TAG, "BudgetRequests > makeRequest > onResponse: " + e);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            result.set("-2");
                            makeToast("Une erreur est survenue (-2).");
                            Log.d(TAG, "BudgetRequests > makeRequest > onErrorResponse: " + error.toString());
                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);
        }

        return result.get();
    }

    private void makeToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
