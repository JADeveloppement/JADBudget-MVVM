package fr.jadeveloppement.budgetsjad.functions.interfaces;

import org.json.JSONObject;

import java.util.ArrayList;

import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;

public interface BudgetRequestsInterface {

    void tokenOk();
    void tokenNonOk();

    void loginOk();
    void loginNonOk();

    void datasSaved();

    void datasImported(JSONObject response);
    void datasImportedV2(ArrayList<Transaction> transactions);

    void previewDatas(Enums.DataToRequest type);

    void requestsFinished();
}
