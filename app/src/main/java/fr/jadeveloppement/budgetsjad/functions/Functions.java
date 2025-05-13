package fr.jadeveloppement.budgetsjad.functions;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import fr.jadeveloppement.budgetsjad.models.classes.Transaction;
import fr.jadeveloppement.budgetsjad.sqlite.SQLiteFunctions;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;
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

    public Functions(Context c){
        this.context = c.getApplicationContext();
        this.sqliteFunctions = new SQLiteFunctions(context);
    }

    public static String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int monthOfYear = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);

        String day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
        String month = monthOfYear < 10 ? "0" + monthOfYear : String.valueOf(monthOfYear);

        return year + "-" + month + "-" + day;
    }

    public static String convertStdDateToLocale(String date){
        if (date.contains("-")){
            String[] dateSplitted = date.split("-");
            return dateSplitted[2] + "/" + dateSplitted[1] + "/" + dateSplitted[0];
        } else return date;
    }

    public static String convertLocaleDateToStd(String date){
        if (date.contains("/")){
            String[] dateSplitted = date.split("/");
            return dateSplitted[2] + "-" + dateSplitted[1] + "-" + dateSplitted[0];
        } else return date;
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

    public List<AccountsTable> getAllAccounts(){
        return sqliteFunctions.getAllAccounts();
    }

    public Long insertAccount(AccountsTable a){
        return sqliteFunctions.insertAccount(a);
    }

    public void deleteAccount(AccountsTable a){
        sqliteFunctions.deleteAccount(a);
        SettingsTable settingAccount = getSettingByLabel(Variables.settingAccount);
        if (String.valueOf(a.account_id).equalsIgnoreCase(settingAccount.value)){
            settingAccount.value = String.valueOf(getAllAccounts().get(0).account_id);
            updateSettings(settingAccount);
        }
    }

    public void updateAccount(AccountsTable a) {
        sqliteFunctions.updateAccount(a);
    }

    public int getDpInPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public List<PeriodsTable> getAllPeriods() {
        return sqliteFunctions.getAllPeriods();
    }

    public PeriodsTable getPeriodById(long id) {
        return sqliteFunctions.getPeriodById(id);
    }

    public void insertPeriod(PeriodsTable periodsTable) {
        sqliteFunctions.insertPeriod(periodsTable);
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

    public PeriodsTable getPeriodByLabel(String period) {
        return sqliteFunctions.getPeriodByLabel(period);
    }

    public void updateSettings(SettingsTable settingsPeriod) {
        sqliteFunctions.updateSettings(settingsPeriod);
    }

    public Long insertInvoice(InvoicesTable invoice){
        return sqliteFunctions.insertInvoice(invoice);
    }

    public Long insertIncome(IncomesTable income) {
        return sqliteFunctions.insertIncome(income);
    }

    public Long insertExpense(ExpensesTable expense) {
        return sqliteFunctions.insertExpense(expense);
    }

    public List<IncomesTable> getAllIncomes() {
        return sqliteFunctions.getAllIncomes();
    }

    public List<InvoicesTable> getAllInvoices() {
        return sqliteFunctions.getAllInvoices();
    }

    public List<ExpensesTable> getAllExpenses() {
        return sqliteFunctions.getAlLExpenses();
    }

    public AccountsTable getAccountById(long id) {
        return sqliteFunctions.getAccountById(id);
    }

    public List<Transaction> getAllInvoicesTransaction() {
        List<InvoicesTable> listOfInvoices = sqliteFunctions.getAllInvoices();
        List<Transaction> listOfTransactions = new ArrayList<>();
        for (InvoicesTable i : listOfInvoices){
            listOfTransactions.add(new Transaction(i.label, i.amount, i.date, String.valueOf(i.account_id), i.paid, Transaction.TransactionType.INVOICE, String.valueOf(i.invoice_id)));
        }
        return listOfTransactions;
    }

    public List<Transaction> getAllIncomesTransaction() {
        List<IncomesTable> listOfIncomes = sqliteFunctions.getAllIncomes();
        List<Transaction> listOfTransactions = new ArrayList<>();
        for (IncomesTable i : listOfIncomes){
            listOfTransactions.add(new Transaction(i.label, i.amount, i.date, String.valueOf(i.account_id), i.paid, Transaction.TransactionType.INCOME, String.valueOf(i.income_id)));
        }
        return listOfTransactions;
    }

    public List<Transaction> getAllExpensesTransaction() {
        List<ExpensesTable> listOfExpenses = sqliteFunctions.getAlLExpenses();
        List<Transaction> listOfTransactions = new ArrayList<>();
        for (ExpensesTable i : listOfExpenses){
            listOfTransactions.add(new Transaction(i.label, i.amount, i.date, String.valueOf(i.account_id), "0", Transaction.TransactionType.EXPENSE, String.valueOf(i.expense_id)));
        }
        return listOfTransactions;
    }

    public List<Transaction> getModelInvoiceTransaction() {
        List<ModeleInvoices> listOfModelInvoice = sqliteFunctions.getAllModelInvoice();
        List<Transaction> listOfTransactions = listOfModelInvoice.isEmpty() ? Collections.emptyList() : new ArrayList<>();
        for (ModeleInvoices i : listOfModelInvoice){
            listOfTransactions.add(new Transaction(i.label, i.amount, i.date, "", "0", Transaction.TransactionType.MODELINVOICE, String.valueOf(i.modeleinvoice_id)));
        }
        Log.d(TAG, "getModelInvoiceTransaction: list of Transaction : " + listOfTransactions.size());
        Log.d(TAG, "getModelInvoiceTransaction: list of ModelInvoice : " + listOfModelInvoice.size());
        return listOfTransactions;
    }

    public List<Transaction> getModelIncomeTransaction() {
        List<ModeleIncomes> listOfModelIncomes = sqliteFunctions.getAllModelIncome();
        List<Transaction> listOfTransactions = listOfModelIncomes.isEmpty() ? Collections.emptyList() : new ArrayList<>();
        for (ModeleIncomes i : listOfModelIncomes){
            listOfTransactions.add(new Transaction(i.label, i.amount, i.date, "", "0", Transaction.TransactionType.MODELINCOME, String.valueOf(i.modeleincome_id)));
        }
        return listOfTransactions;
    }

    public IncomesTable getIncomeById(long id) {
        return sqliteFunctions.getIncomeById(id);
    }

    public InvoicesTable getInvoiceById(long id) {
        return sqliteFunctions.getInvoiceById(id);
    }

    public ExpensesTable getExpenseById(long id){
        return sqliteFunctions.getExpenseById(id);
    }

    public void deleteIncome(IncomesTable i) {
        sqliteFunctions.deleteIncome(i);
    }

    public void deleteInvoice(InvoicesTable i) {
        sqliteFunctions.deleteInvoice(i);
    }

    public void deleteExpense(ExpensesTable e) {
        sqliteFunctions.deleteExpense(e);
    }

    public void updateInvoice(InvoicesTable i) {
        sqliteFunctions.updateInvoice(i);
    }

    public void updateIncome(IncomesTable i) {
        sqliteFunctions.updateIncome(i);
    }

    public void updateExpense(ExpensesTable e) {
        sqliteFunctions.updateExpense(e);
    }

    public static void handleExceptions(String s, Exception e) {
        Log.d(TAG, "handleExceptions: " + s + "\nException : \n"+e);
    }

    public void deletePeriod(@NonNull PeriodsTable p) {
        sqliteFunctions.deletePeriod(p);
    }

    public void checkSettingPeriod(){
        sqliteFunctions.checkSettingsPeriod();
    }

    public ModeleIncomes getModeleIncomeById(long id) {
        return sqliteFunctions.getModeleIncomeById(id);
    }

    public void insertModelIncome(ModeleIncomes newModelInvoice) {
        sqliteFunctions.insertModelIncome(newModelInvoice);
    }

    public void deleteModelIncome(ModeleIncomes modeleIncomes) {
        sqliteFunctions.deleteModelIncome(modeleIncomes);
    }

    public ModeleInvoices getModeleInvoiceById(long id) {
        return sqliteFunctions.getModeleInvoiceById(id);
    }

    public void insertModelInvoice(ModeleInvoices newModelInvoice) {
        sqliteFunctions.insertModelInvoice(newModelInvoice);
    }

    public void deleteModelInvoice(ModeleInvoices modeleInvoices) {
        sqliteFunctions.deleteModelInvoice(modeleInvoices);
    }

    public void makeToast(String message) {
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public String convertListToDatas(List<Transaction> modelInvoiceTransaction) {
        List<String> result = new ArrayList<>();
        for (Transaction t : modelInvoiceTransaction) {
            result.add(t.getId() + "<l>"
                    + t.getLabel() + "<l>"
                    + t.getAmount() + "<l>"
                    + t.getDate() + "<l>"
                    + t.getAccount() + "<l>"
                    + t.getPaid() + "<l>"
                    + t.getType());
        }

        return TextUtils.join("<n>", result);
    }

    public void updateModeleIncome(ModeleIncomes modeleIncomes) {
        sqliteFunctions.updateModeleIncome(modeleIncomes);
    }

    public void updateModeleInvoice(ModeleInvoices modeleInvoices) {
        sqliteFunctions.updateModeleInvoice(modeleInvoices);
    }
}
