package fr.jadeveloppement.budgetsjad.functions;

import static java.util.Objects.isNull;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;
import fr.jadeveloppement.budgetsjad.sqlite.SQLiteFunctions;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.CategoryTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ExpensesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.IncomesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.InvoicesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleIncomes;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleInvoices;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class Functions {
    private static final String TAG = "BudgetsJAD";
    private final Context context;
    private final SQLiteFunctions sqliteFunctions;

    /**
     * Allow to use basics functions. List of function available :
     *  -
     * @param c : context
     */
    public Functions(Context c){
        this.context = c.getApplicationContext();
        this.sqliteFunctions = new SQLiteFunctions(context);
    }

    /**
     * Get today's date
     * @return : date in YYYY-MM-DD format
     */
    public static String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int monthOfYear = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);

        String day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
        String month = monthOfYear < 10 ? "0" + monthOfYear : String.valueOf(monthOfYear);

        return year + "-" + month + "-" + day;
    }

    /**
     * Convert a date format to another
     * @param date : date in YYYY-MM-DD format
     * @return : date in DD/MM/YYYY format, or, if it's not in the good format, return the date given
     * in parameter
     */
    public static String convertStdDateToLocale(String date){
        if (date.contains("-")){
            String[] dateSplitted = date.split("-");
            return dateSplitted[2] + "/" + dateSplitted[1] + "/" + dateSplitted[0];
        } else return date;
    }

    /**
     * Convert a date format to another
     * @param date : date in DD/MM/YYYY format
     * @return : date in YYYY-MM-DD format, or, if it's not in the good format, return the date given
     * in parameter
     */
    public static String convertLocaleDateToStd(String date){
        if (date.contains("/")){
            String[] dateSplitted = date.split("/");
            return dateSplitted[2] + "-" + dateSplitted[1] + "-" + dateSplitted[0];
        } else return date;
    }

    /**
     * convert string type (INVOICE, INCOME, EXPENSE, MODELINVOICE, MODELINCOME) to Enums.TransactionType
     * @param strType : string to convert
     * @return : corresponding Enums.TransactionType, Enums.TransactionType.UNDEFINED if not recongnized
     */
    public static Enums.TransactionType convertStrtypeToTransactionType(String strType) {
        Enums.TransactionType type = Enums.TransactionType.UNDEFINED;

        if (strType.equalsIgnoreCase(String.valueOf(Enums.TransactionType.INVOICE))) type = Enums.TransactionType.INVOICE;
        else if (strType.equalsIgnoreCase(String.valueOf(Enums.TransactionType.INCOME))) type = Enums.TransactionType.INCOME;
        else if (strType.equalsIgnoreCase(String.valueOf(Enums.TransactionType.EXPENSE))) type = Enums.TransactionType.EXPENSE;
        else if (strType.equalsIgnoreCase(String.valueOf(Enums.TransactionType.MODELINVOICE))) type = Enums.TransactionType.MODELINVOICE;
        else if (strType.equalsIgnoreCase(String.valueOf(Enums.TransactionType.MODELINCOME))) type = Enums.TransactionType.MODELINCOME;

        return type;
    }

    /**
     * Display a snackBar to MainActivity rootview (warning : can be in background instead of foreground)
     * @param message : message to display
     */
    public static void makeSnakebar(String message) {
        Snackbar snackbar = Snackbar.make(MainActivity.getViewRoot(), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    /**
     * Generic function to handle potential catch case or exception
     * @param s : Message to display in log
     * @param e : error to display with the log message
     */
    public static void handleExceptions(String s, Exception e) {
        Log.d(TAG, "handleExceptions: " + s + "\nException : \n"+e);
    }

    /**
     *
     * @return layoutParams that's WRAP_CONTENT / WRAP_CONTENT
     */
    public LinearLayout.LayoutParams defaultLayoutParams(){
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    /**
     *
     * @return layoutParams that's MATCH_PARENT / WRAP_CONTENT
     */
    public LinearLayout.LayoutParams matchParentWidthLayoutParams(){
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    /**
     * Convert dp unit in pixel unit
     * @param dp : unit in dp to convert
     * @return : unit in pixel converted
     */
    public int getDpInPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * Display a Toast message
     * @param message
     */
    public void makeToast(String message) {
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * Convert a list of Transaction to a String of datas we can send to the JADeveloppement API
     * @param listOfTransaction : list of Transaction to convert to datas parsable by the JADeveloppement API
     * @return : return a String of the list of transaction to use to export datas
     *      separator of rows : <n>
     *      separator of cols : <l>
     */
    public String convertListToDatas(List<Transaction> listOfTransaction) {
        List<String> result = new ArrayList<>();
        if (isNull(listOfTransaction)) result = Collections.emptyList();
        else {
            for (Transaction t : listOfTransaction) {
                result.add(t.getId() + "<l>"
                        + sanitizeDatas(t.getLabel()) + "<l>"
                        + t.getAmount() + "<l>"
                        + t.getDate() + "<l>"
                        + t.getAccount() + "<l>"
                        + t.getPaid() + "<l>"
                        + t.getType() + "<l>"
                        + t.getCategory());
            }
        }
        return TextUtils.join("<n>", result);
    }

    private String sanitizeDatas(String label) {
        String regex = "[&=;%+#/?<>\\\\\\[\\]{}|^`\\s'\"]";
        return label.replaceAll(regex, "");
    }

    // ACCOUNTS MANAGEMENT
    public List<AccountsTable> getAllAccounts(){
        return sqliteFunctions.getAllAccounts();
    }

    public AccountsTable getAccountById(long id) {
        return sqliteFunctions.getAccountById(id);
    }

    public Long insertAccount(AccountsTable a){
        return sqliteFunctions.insertAccount(a);
    }

    public void updateAccount(AccountsTable a) {
        sqliteFunctions.updateAccount(a);
    }

    public void deleteAccount(AccountsTable a){
        sqliteFunctions.deleteAccount(a);
        SettingsTable settingAccount = getSettingByLabel(Variables.settingAccount);
        if (String.valueOf(a.account_id).equalsIgnoreCase(settingAccount.value)){
            settingAccount.value = String.valueOf(getAllAccounts().get(0).account_id);
            updateSettings(settingAccount);
        }
    }
    //

    // CATEGORY MANAGEMENT
    public List<CategoryTable> getAllCategories(){
        return sqliteFunctions.getAllCategories();
    }

    public CategoryTable getCategoryById(long id) {
        return sqliteFunctions.getCategoryById(id);
    }

    public CategoryTable getCategoryByLabel(@NonNull String label) {
        return sqliteFunctions.getCategoryByLabel(label);
    }

    public Long insertCategory(CategoryTable categoryTable){
        CategoryTable catLabel = getCategoryByLabel(categoryTable.label);
        if (isNull(catLabel)) return sqliteFunctions.insertCategory(categoryTable);
        else {
            makeToast("Une catégorie similaire existe déjà.");
            return null;
        }
    }

    public void updateCategory(CategoryTable categoryTable) {
        sqliteFunctions.updateCategory(categoryTable);
    }

    public void deleteCategory(CategoryTable categoryTable){
        sqliteFunctions.deleteCategory(categoryTable);
    }
    //

    // PERIODS MANAGEMENT
    public List<PeriodsTable> getAllPeriods() {
        return sqliteFunctions.getAllPeriods();
    }

    public PeriodsTable getPeriodById(long id) {
        return sqliteFunctions.getPeriodById(id);
    }

    public PeriodsTable getPeriodByLabel(String period) {
        return sqliteFunctions.getPeriodByLabel(period);
    }

    public void insertPeriod(PeriodsTable periodsTable) {
        sqliteFunctions.insertPeriod(periodsTable);
    }

    public void deletePeriod(@NonNull PeriodsTable p) {
        sqliteFunctions.deletePeriod(p);
    }
    //

    // SETTINGS MANAGEMENT

    /**
     * Check if a Period is saved into SettingsTable. Allows to prevent crash because no period is preselected
     */
    public void checkSettingPeriod(){
        sqliteFunctions.checkSettingsPeriod();
    }

    public SettingsTable getSettingByLabel(String settingPeriod) {
        return sqliteFunctions.getSettingByLabel(settingPeriod);
    }

    public SettingsTable getSettingById(long id) {
        return sqliteFunctions.getSettingById(id);
    }

    public Long insertSettings(SettingsTable settingsTable) {
        return sqliteFunctions.insertSettings(settingsTable);
    }

    public void updateSettings(SettingsTable settingsTable) {
        sqliteFunctions.updateSettings(settingsTable);
    }
    //

    // INVOICE MANAGEMENT
    public List<InvoicesTable> getAllInvoices() {
        return sqliteFunctions.getAllInvoices();
    }

    public List<Transaction> getAllInvoicesTransaction() {
        List<InvoicesTable> listOfInvoices = sqliteFunctions.getAllInvoices();
        List<Transaction> listOfTransactions = new ArrayList<>();
        for (InvoicesTable i : listOfInvoices){
            listOfTransactions.add(new Transaction(i.label, i.amount, i.date, String.valueOf(i.account_id), i.paid, String.valueOf(i.category_id), Enums.TransactionType.INVOICE, String.valueOf(i.invoice_id)));
        }
        return listOfTransactions;
    }

    public InvoicesTable getInvoiceById(long id) {
        return sqliteFunctions.getInvoiceById(id);
    }

    public Long insertInvoice(InvoicesTable invoice){
        return sqliteFunctions.insertInvoice(invoice);
    }

    public void updateInvoice(InvoicesTable i) {
        sqliteFunctions.updateInvoice(i);
    }

    public void deleteInvoice(InvoicesTable i) {
        sqliteFunctions.deleteInvoice(i);
    }
    //

    // INCOME MANAGEMENT
    public List<IncomesTable> getAllIncomes() {
        return sqliteFunctions.getAllIncomes();
    }

    public List<Transaction> getAllIncomesTransaction() {
        List<IncomesTable> listOfIncomes = sqliteFunctions.getAllIncomes();
        List<Transaction> listOfTransactions = new ArrayList<>();
        for (IncomesTable i : listOfIncomes){
            listOfTransactions.add(new Transaction(i.label, i.amount, i.date, String.valueOf(i.account_id), i.paid, String.valueOf(i.category_id), Enums.TransactionType.INCOME, String.valueOf(i.income_id)));
        }
        return listOfTransactions;
    }

    public IncomesTable getIncomeById(long id) {
        return sqliteFunctions.getIncomeById(id);
    }

    public Long insertIncome(IncomesTable income) {
        return sqliteFunctions.insertIncome(income);
    }

    public void updateIncome(IncomesTable i) {
        sqliteFunctions.updateIncome(i);
    }

    public void deleteIncome(IncomesTable i) {
        sqliteFunctions.deleteIncome(i);
    }
    //

    // EXPENSE MANAGEMENT
    public List<ExpensesTable> getAllExpenses() {
        return sqliteFunctions.getAlLExpenses();
    }

    public List<Transaction> getAllExpensesTransaction() {
        List<ExpensesTable> listOfExpenses = sqliteFunctions.getAlLExpenses();
        List<Transaction> listOfTransactions = new ArrayList<>();
        for (ExpensesTable i : listOfExpenses){
            listOfTransactions.add(new Transaction(i.label, i.amount, i.date, String.valueOf(i.account_id), "0", String.valueOf(i.category_id), Enums.TransactionType.EXPENSE, String.valueOf(i.expense_id)));
        }
        return listOfTransactions;
    }

    public ExpensesTable getExpenseById(long id){
        return sqliteFunctions.getExpenseById(id);
    }

    public Long insertExpense(ExpensesTable expense) {
        return sqliteFunctions.insertExpense(expense);
    }

    public void updateExpense(ExpensesTable e) {
        sqliteFunctions.updateExpense(e);
    }

    public void deleteExpense(ExpensesTable e) {
        sqliteFunctions.deleteExpense(e);
    }
    //

    // MODELINVOICE MANAGEMENT
    public List<Transaction> getModelInvoiceTransaction() {
        List<ModeleInvoices> listOfModelInvoice = sqliteFunctions.getAllModelInvoice();
        List<Transaction> listOfTransactions = listOfModelInvoice.isEmpty() ? Collections.emptyList() : new ArrayList<>();
        for (ModeleInvoices i : listOfModelInvoice){
            listOfTransactions.add(new Transaction(i.label, i.amount, i.date, "", "0", String.valueOf(i.category_id), Enums.TransactionType.MODELINVOICE, String.valueOf(i.modeleinvoice_id)));
        }
        return listOfTransactions;
    }

    public ModeleInvoices getModeleInvoiceById(long id) {
        return sqliteFunctions.getModeleInvoiceById(id);
    }

    public void insertModelInvoice(ModeleInvoices newModelInvoice) {
        sqliteFunctions.insertModelInvoice(newModelInvoice);
    }

    public void updateModeleInvoice(ModeleInvoices modeleInvoices) {
        sqliteFunctions.updateModeleInvoice(modeleInvoices);
    }

    public void deleteModelInvoice(ModeleInvoices modeleInvoices) {
        sqliteFunctions.deleteModelInvoice(modeleInvoices);
    }

    //MODELINCOME MANAGEMENT
    public List<Transaction> getModelIncomeTransaction() {
        List<ModeleIncomes> listOfModelIncomes = sqliteFunctions.getAllModelIncome();
        List<Transaction> listOfTransactions = listOfModelIncomes.isEmpty() ? Collections.emptyList() : new ArrayList<>();
        for (ModeleIncomes i : listOfModelIncomes){
            listOfTransactions.add(new Transaction(i.label, i.amount, i.date, "", "0", String.valueOf(i.category_id), Enums.TransactionType.MODELINCOME, String.valueOf(i.modeleincome_id)));
        }
        return listOfTransactions;
    }

    public ModeleIncomes getModeleIncomeById(long id) {
        return sqliteFunctions.getModeleIncomeById(id);
    }

    public void insertModelIncome(ModeleIncomes newModelInvoice) {
        sqliteFunctions.insertModelIncome(newModelInvoice);
    }

    public void updateModeleIncome(ModeleIncomes modeleIncomes) {
        sqliteFunctions.updateModeleIncome(modeleIncomes);
    }

    public void deleteModelIncome(ModeleIncomes modeleIncomes) {
        sqliteFunctions.deleteModelIncome(modeleIncomes);
    }

    public void emptyUserInfos() {
        SettingsTable settingUser = getSettingByLabel(Variables.settingUsername);
        SettingsTable settingPassword = getSettingByLabel(Variables.settingPassword);
        SettingsTable settingToken = getSettingByLabel(Variables.settingsToken);

        settingUser.value = "";
        settingPassword.value = "";
        settingToken.value = "";

        updateSettings(settingUser);
        updateSettings(settingPassword);
        updateSettings(settingToken);
    }
    //

    // Transaction Management

    /**
     * Convert an Expense/Invoice/Income/ModelInvoice/ModelIncome to a Transaction
     * @param o : object to convert
     * @return : object converted to a Transaction
     * @throws Exception : if unable to convert object to Transaction because object is neither of type ExpensesTable/IncomesTable/InvoicesTable/ModelInvoicesTable/ModelIncomesTable
     */
    public Transaction convertObjectToTransaction(Object o) throws Exception {
        Transaction convertedObject = null;
        if (o instanceof InvoicesTable){
            InvoicesTable i = (InvoicesTable) o;
            convertedObject = new Transaction(
                    i.label,
                    i.amount,
                    i.date,
                    String.valueOf(i.account_id),
                    i.paid,
                    String.valueOf(i.category_id),
                    Enums.TransactionType.INVOICE
            );
        } else if (o instanceof IncomesTable){
            IncomesTable i = (IncomesTable) o;
            convertedObject = new Transaction(
                    i.label,
                    i.amount,
                    i.date,
                    String.valueOf(i.account_id),
                    i.paid,
                    String.valueOf(i.category_id),
                    Enums.TransactionType.INCOME
            );
        } else if (o instanceof ExpensesTable){
            ExpensesTable i = (ExpensesTable) o;
            convertedObject = new Transaction(
                    i.label,
                    i.amount,
                    i.date,
                    String.valueOf(i.account_id),
                    "0",
                    String.valueOf(i.category_id),
                    Enums.TransactionType.EXPENSE
            );
        } else if (o instanceof ModeleInvoices){
            ModeleInvoices i = (ModeleInvoices) o;
            convertedObject = new Transaction(
                    i.label,
                    i.amount,
                    i.date,
                    "",
                    "0",
                    String.valueOf(i.category_id),
                    Enums.TransactionType.MODELINVOICE
            );
        } else if (o instanceof ModeleIncomes){
            ModeleIncomes i = (ModeleIncomes) o;
            convertedObject = new Transaction(
                    i.label,
                    i.amount,
                    i.date,
                    "",
                    "0",
                    String.valueOf(i.category_id),
                    Enums.TransactionType.MODELINCOME
            );
        }

        if (isNull(convertedObject)) throw new Exception("No type match object");

        return convertedObject;
    }

    /**
     * Function to get a List of transaction of a given type
     * @param type : type to retrieve
     * @return : List of transaction of a given type
     */
    public List<Transaction> getAllTransactionByType(@NonNull Enums.TransactionType type){
        List<Transaction> result = new ArrayList<>();
        List<?> objects = Collections.emptyList();

        if (type == Enums.TransactionType.EXPENSE) objects = getAllExpenses();
        else if (type == Enums.TransactionType.INVOICE) objects = getAllInvoices();
        else if (type == Enums.TransactionType.INCOME) objects = getAllIncomes();

        try {
            for (Object o : objects){
                Log.d(TAG, "ICI : getAllTransactionByType: " + convertObjectToTransaction(o).getLabel());
                result.add(convertObjectToTransaction(o));
            }
        } catch(Exception e){
            makeToast("Une erreur est survenue");
            Log.d(TAG, "Functions > getAllTransactionByType: " + e.getMessage());
        }

        return result;
    }
    //
}
