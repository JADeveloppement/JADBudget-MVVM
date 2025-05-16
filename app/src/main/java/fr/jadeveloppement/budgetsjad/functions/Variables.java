package fr.jadeveloppement.budgetsjad.functions;

import java.text.DecimalFormat;

public class Variables {
    public static final String strTypeInvoice = "invoice";
    public static final String strTypeIncome = "income";
    public static final String strTypeExpense = "expense";
    public static final String settingPeriod = "period_id";
    public static final String settingAccount = "account_id";

    public static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public static final String loginUrlField = "%LOGIN%";
    public static final String passwordUrlField = "%PASSWORD%";
    public static final String tokenUrlField = "%TOKEN%";
    public static final String datasUrlField = "%DATAS%";
    public static final String typeUrlField = "%TYPE%";

    public static final String URL_LOGIN = "https://jadeveloppement.fr/api/login?login="+ loginUrlField +"&password="+passwordUrlField;
    public static final String URL_RETRIEVEDATA = "https://jadeveloppement.fr/api/retrieveDatas?login="+ loginUrlField +"&password="+passwordUrlField+"&token="+tokenUrlField+"&type="+typeUrlField;
    public static final String URL_EXPORTDATA = "https://jadeveloppement.fr/api/exportDatas?login="+ loginUrlField +"&password="+passwordUrlField+"&token="+tokenUrlField+"&datas="+datasUrlField;
    public static final String URL_CHECKTOKEN = "https://jadeveloppement.fr/api/checkToken?login="+loginUrlField+"&token="+tokenUrlField;
    public static final String settingsToken = "token_user";
    public static final String settingUsername = "username";
    public static final String settingPassword = "password";
}
