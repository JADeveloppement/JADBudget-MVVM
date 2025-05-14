package fr.jadeveloppement.budgetsjad.functions;

public interface BudgetRequestsInterface {
    void loginOk(String token);
    void datasSaved();
    void datasImported(String response);
}
