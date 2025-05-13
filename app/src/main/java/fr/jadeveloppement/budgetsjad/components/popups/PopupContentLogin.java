package fr.jadeveloppement.budgetsjad.components.popups;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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

public class PopupContentLogin extends LinearLayout {

    private final String TAG = "JADABudget";

    private final Context context;
    private final View viewParent;
    private final View popupLayout;

    private LinearLayout popupContentLoginClose, popupContentLoginContentContainer;

    public PopupContentLogin(@NonNull Context c){
        super(c);
        this.context = c.getApplicationContext();
        this.viewParent = MainActivity.getViewRoot();
        this.popupLayout = LayoutInflater.from(context).inflate(R.layout.popup_managelogin_layout, (ViewGroup) viewParent, false);

        initPopup();
    }

    private void initPopup(){
        popupContentLoginClose = popupLayout.findViewById(R.id.popupContentLoginClose);
        popupContentLoginContentContainer = popupLayout.findViewById(R.id.popupContentLoginContentContainer);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", "test");
            requestBody.put("password", "123456");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://jadeveloppement.fr/csrf_token", requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            Log.d(TAG, "onResponse: " + token + " / ");
                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: " + e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: erreur " + error.toString());
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    public LinearLayout getBtnClose(){
        return popupContentLoginClose;
    }

    public LinearLayout getLayout(){
        return (LinearLayout) popupLayout;
    }
}
