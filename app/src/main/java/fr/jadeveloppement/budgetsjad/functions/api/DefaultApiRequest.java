package fr.jadeveloppement.budgetsjad.functions.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import fr.jadeveloppement.budgetsjad.functions.interfaces.ApiRequest;

public class DefaultApiRequest implements ApiRequest {

    private final Context context;

    public DefaultApiRequest(@NonNull Context c){
        this.context = c.getApplicationContext();
    }

    @Override
    public void makeRequest(JsonObjectRequest jsonObjectRequest) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }
}
