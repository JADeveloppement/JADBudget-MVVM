package fr.jadeveloppement.budgetsjad.functions;

import java.text.DecimalFormat;

public class Variables {
    public static String strTypeInvoice = "invoice";
    public static String strTypeIncome = "income";
    public static String strTypeExpense = "expense";
    public static String settingPeriod = "period_id";
    public static String settingAccount = "account_id";

    public static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public static String URL_LOGIN = "https://jadeveloppement.fr/api/login?";
    public static String URL_RETRIEVEDATA = "https://jadeveloppement.fr/api/retrieveDatas?";
    public static String URL_EXPORTDATA = "https://jadeveloppement.fr/api/exportDatas?";
}
