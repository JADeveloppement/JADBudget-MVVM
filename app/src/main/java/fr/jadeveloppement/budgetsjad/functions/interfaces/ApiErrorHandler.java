package fr.jadeveloppement.budgetsjad.functions.interfaces;

import com.android.volley.VolleyError;

import fr.jadeveloppement.budgetsjad.functions.Enums;

public interface ApiErrorHandler {
    void handleError(VolleyError error, Enums.ErrorRequest type);
}
