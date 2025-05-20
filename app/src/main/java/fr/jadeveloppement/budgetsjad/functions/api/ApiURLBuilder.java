package fr.jadeveloppement.budgetsjad.functions.api;

import android.util.Log;

import androidx.annotation.NonNull;

import fr.jadeveloppement.budgetsjad.functions.Variables;

public class ApiURLBuilder {
    private final String baseUrl;
    private String login;
    private String password;

    public ApiURLBuilder(){
        this.baseUrl = Variables.baseURL;
    }

    public ApiURLBuilder setLogin(@NonNull String l){
        this.login = l;
        return this;
    }

    public ApiURLBuilder setPassword(@NonNull String p){
        this.password = p;
        return this;
    }

    private final String TAG = "JADBudget";

    public String build(@NonNull String endpoint, @NonNull String... params){
        String URL = baseUrl + endpoint;
        Log.d(TAG, "ApiURLBuilder > build\nURL : " + URL);
        for(int i = 0; i < params.length; i+= 2){
            URL = URL.replace(params[i], params[i+1]);
            Log.d(TAG, "ApiURLBuilder > build\nURL : " + URL);
        }
        return URL;
    }
}
