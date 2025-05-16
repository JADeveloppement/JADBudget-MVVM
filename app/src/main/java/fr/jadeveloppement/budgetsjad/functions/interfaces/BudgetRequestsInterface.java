package fr.jadeveloppement.budgetsjad.functions.interfaces;

import org.json.JSONObject;

public interface BudgetRequestsInterface {
    void loginOk(String token);
    void loginNonOk();

    void datasSaved();
    void datasImported(JSONObject response);

    void requestsFinished();

    void tokenOk();

    void tokenNonOk();
}
