package fr.jadeveloppement.budgetsjad.functions.interfaces;

public interface BudgetRequestsInterface {
    void loginOk(String token);
    void datasSaved();
    void datasImported(String response);
}
