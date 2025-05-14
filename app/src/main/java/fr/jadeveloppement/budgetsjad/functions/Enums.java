package fr.jadeveloppement.budgetsjad.functions;

public class Enums {

    /**
     * Enum used for BudgetRequests class inside HomeFragment to differentiate an API call to retrieve or export datas
     */
    public enum TagRequest {
        EXPORT_DATA, IMPORT_DATA
    }

    /**
     * Enum used for BudgetRequests class inside HomeFragment to list datas to export/import
     */
    public enum DataToRequest{
        INVOICE, INCOME, EXPENSE, MODELINVOICE, MODELINCOME
    }

    /**
     *  Allows to differentiate between the differents transaction we can save inside local/remote database
     */
    public enum TransactionType {
        INVOICE, INCOME, EXPENSE, MODELINCOME, MODELINVOICE, UNDEFINED
    }
}
