package fr.jadeveloppement.budgetsjad.functions.interfaces;

import org.json.JSONObject;

import fr.jadeveloppement.budgetsjad.functions.Enums;

public interface BudgetRequestsInterface {

    void tokenOk();
    void tokenNonOk();

    void loginOk();
    void loginNonOk();

    void datasSaved();
    void datasImported(JSONObject response);

    void previewDatas(Enums.DataToRequest type);

    void requestsFinished();
}
