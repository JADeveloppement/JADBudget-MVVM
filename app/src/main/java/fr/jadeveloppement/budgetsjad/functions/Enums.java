package fr.jadeveloppement.budgetsjad.functions;

public class Enums {

    public enum ErrorRequest{
        LOGIN_NON_OK, EXPORT_ERROR, IMPORT_ERROR, ALL_DATA_ERROR, DELETE_TRANSACTION_ERROR, ADD_TRANSACTION_ERROR, TOKEN_NON_OK
    };

    /**
     * Enum used for BudgetRequests class inside HomeFragment to differentiate an API call to retrieve or export datas
     */
    public enum TagRequest {
        LOGIN, CHECK_TOKEN, EXPORT_DATA, DISPLAY_DATA, LOGOUT, LOGIN_NON_OK, TOKEN_NON_OK, DELETE_TRANSACTION, ADD_TRANSACTION, IMPORT_DATA
    }

    /**
     * Enum used for BudgetRequests class inside HomeFragment to list datas to export/import
     */
    public enum DataToRequest{
        INVOICE, INCOME, EXPENSE, MODELINVOICE, ALL_DATAS, MODELINCOME
    }

    /**
     *  Allows to differentiate between the differents transaction we can save inside local/remote database
     */
    public enum TransactionType {
        INVOICE, INCOME, EXPENSE, MODELINCOME, MODELINVOICE, UNDEFINED
    }
}
