package fr.jadeveloppement.budgetsjad.functions.api;

import static java.util.Objects.isNull;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.interfaces.ApiErrorHandler;
import fr.jadeveloppement.budgetsjad.functions.interfaces.BudgetRequestsInterface;

public class DefaultApiErrorHandler implements ApiErrorHandler {

    private final Context context;
    private final BudgetRequestsInterface callback;
    private final Functions functions;
    private final String TAG = "JADBudget";

    public DefaultApiErrorHandler(@NonNull Context c, @Nullable BudgetRequestsInterface call){
        this.context = c.getApplicationContext();
        this.callback = call;
        this.functions = new Functions(c.getApplicationContext());
    }

    @Override
    public void handleError(VolleyError error, Enums.ErrorRequest type){
        NetworkResponse networkResponse = error.networkResponse;
        if (!isNull(networkResponse)){
            int statuscode = networkResponse.statusCode;
            switch(statuscode){
                case 400:
                case 401:
                    Log.d(TAG, "DefaultApiErrorHandler > handleError: error "+statuscode);
                    break;
                case 500:
                    functions.makeToast("Erreur serveur");
                    break;
                default:
                    Log.d(TAG, "DefaultApiErrorHandler > handleError : statusCode : " + statuscode);
                    break;
            }

            switch(type){
                case TOKEN_NON_OK:
                case DELETE_TRANSACTION_ERROR:
                case ADD_TRANSACTION_ERROR:
                    callback.tokenNonOk();
                    break;
                case LOGIN_NON_OK:
                case EXPORT_ERROR:
                case IMPORT_ERROR:
                case ALL_DATA_ERROR:
                    callback.loginNonOk();
                    break;
            }
        }
    }
}
