package fr.jadeveloppement.budgetsjad.sqlite;

import static java.lang.Long.parseLong;
import static java.util.Objects.isNull;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.sqlite.helper.SQLiteAccountsFunctions;
import fr.jadeveloppement.budgetsjad.sqlite.helper.SQLiteExpensesFunctions;
import fr.jadeveloppement.budgetsjad.sqlite.helper.SQLiteIncomesFunctions;
import fr.jadeveloppement.budgetsjad.sqlite.helper.SQLiteInvoicesFunctions;
import fr.jadeveloppement.budgetsjad.sqlite.helper.SQLiteModeleIncomeFunctions;
import fr.jadeveloppement.budgetsjad.sqlite.helper.SQLiteModeleInvoiceFunctions;
import fr.jadeveloppement.budgetsjad.sqlite.helper.SQLitePeriodsFunctions;
import fr.jadeveloppement.budgetsjad.sqlite.helper.SQLiteSettingsFunctions;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ExpensesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.IncomesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.InvoicesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleIncomes;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleInvoices;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class SQLiteFunctions {
    private final String TAG = "BudgetJAD";
    private final Context context;
    private final SQLiteAccountsFunctions sqLiteAccountsFunctions;
    private final SQLitePeriodsFunctions sqlitePeriodsFunctions;
    private final SQLiteSettingsFunctions sqLiteSettingsFunctions;
    private final SQLiteInvoicesFunctions sqLiteInvoicesFunctions;
    private final SQLiteIncomesFunctions sqLiteIncomesFunctions;
    private final SQLiteExpensesFunctions sqLiteExpensesFunctions;
    private final SQLiteModeleInvoiceFunctions sqLiteModeleInvoiceFunctions;
    private final SQLiteModeleIncomeFunctions sqLiteModeleIncomeFunctions;


    public SQLiteFunctions(@NonNull Context c){
        this.context = c.getApplicationContext();
        this.sqLiteAccountsFunctions = new SQLiteAccountsFunctions(context);
        this.sqlitePeriodsFunctions = new SQLitePeriodsFunctions(context);
        this.sqLiteSettingsFunctions = new SQLiteSettingsFunctions(context);
        this.sqLiteInvoicesFunctions = new SQLiteInvoicesFunctions(context);
        this.sqLiteIncomesFunctions = new SQLiteIncomesFunctions(context);
        this.sqLiteExpensesFunctions = new SQLiteExpensesFunctions(context);
        this.sqLiteModeleInvoiceFunctions = new SQLiteModeleInvoiceFunctions(context);
        this.sqLiteModeleIncomeFunctions = new SQLiteModeleIncomeFunctions(context);
    }
    public List<AccountsTable> getAllAccounts() {
        return sqLiteAccountsFunctions.getAllAccounts();
    }

    public Long insertAccount(AccountsTable a) {
        return sqLiteAccountsFunctions.insertAccount(a);
    }

    public void deleteAccount(AccountsTable a) {
        sqLiteAccountsFunctions.deleteAccount(a);
    }

    public void updateAccount(AccountsTable a) {
        sqLiteAccountsFunctions.updateAccount(a);
    }

    public List<PeriodsTable> getAllPeriods() {
        return sqlitePeriodsFunctions.getAllPeriods();
    }

    public void insertPeriod(PeriodsTable periodsTable) {
        sqlitePeriodsFunctions.insertPeriod(periodsTable);
    }

    public SettingsTable getSettingByLabel(String settingPeriod) {
        return sqLiteSettingsFunctions.getSettingByLabel(settingPeriod);
    }

    public Long insertSettings(SettingsTable settingsTable){
        return sqLiteSettingsFunctions.insertSetting(settingsTable);
    }

    public PeriodsTable getPeriodById(long id) {
        return sqlitePeriodsFunctions.getPeriodById(id);
    }

    public PeriodsTable getPeriodByLabel(String period) {
        return sqlitePeriodsFunctions.getPeriodByLabel(period);
    }

    public void updateSettings(SettingsTable settingsPeriod) {
        sqLiteSettingsFunctions.updateSettings(settingsPeriod);
    }

    public Long insertInvoice(InvoicesTable invoice) {
        return sqLiteInvoicesFunctions.insertInvoice(invoice);
    }

    public Long insertIncome(IncomesTable income) {
        return sqLiteIncomesFunctions.insertIncome(income);
    }

    public Long insertExpense(ExpensesTable expense) {
        return sqLiteExpensesFunctions.insertExpense(expense);
    }

    public List<IncomesTable> getAllIncomes() {
        return sqLiteIncomesFunctions.getAllIncomes();
    }

    public List<InvoicesTable> getAllInvoices() {
        return sqLiteInvoicesFunctions.getAllInvoices();
    }

    public List<ExpensesTable> getAlLExpenses() {
        return sqLiteExpensesFunctions.getAllExpenses();
    }

    public List<ModeleInvoices> getAllModelInvoice() {
        return sqLiteModeleInvoiceFunctions.getAllModelInvoice();
    }

    public List<ModeleIncomes> getAllModelIncome() {
        return sqLiteModeleIncomeFunctions.getAllModelIncome();
    }

    public AccountsTable getAccountById(long id) {
        return sqLiteAccountsFunctions.getAccountById(id);
    }

    public SettingsTable getSettingById(long id) {
        return sqLiteSettingsFunctions.getSettingById(id);
    }

    public IncomesTable getIncomeById(long id) {
        return sqLiteIncomesFunctions.getIncomeById(id);
    }

    public InvoicesTable getInvoiceById(long id) {
        return sqLiteInvoicesFunctions.getInvoiceById(id);
    }

    public ExpensesTable getExpenseById(long id) {
        return sqLiteExpensesFunctions.getExpenseById(id);
    }

    public void deleteIncome(IncomesTable i) {
        sqLiteIncomesFunctions.deleteIncome(i);
    }

    public void deleteExpense(ExpensesTable e) {
        sqLiteExpensesFunctions.deleteExpense(e);
    }

    public void deleteInvoice(InvoicesTable i) {
        sqLiteInvoicesFunctions.deleteInvoice(i);
    }

    public void updateInvoice(InvoicesTable i) {
        sqLiteInvoicesFunctions.updateInvoice(i);
    }

    public void updateIncome(IncomesTable i) {
        sqLiteIncomesFunctions.updateIncome(i);
    }

    public void updateExpense(ExpensesTable e) {
        sqLiteExpensesFunctions.updateExpense(e);
    }

    public void checkSettingsPeriod(){
        PeriodsTable periodsTable = getPeriodById(parseLong(getSettingByLabel(Variables.settingPeriod).value));
        if (isNull(periodsTable)){
            Log.d(TAG, "SQLiteFunctions > checkSettingsPeriod : Period saved don't exists. ");
            List<PeriodsTable> listOfPeriods = sqlitePeriodsFunctions.getAllPeriods();
            if (listOfPeriods.isEmpty()){
                PeriodsTable periodsTable1 = new PeriodsTable();
                periodsTable1.label = Functions.getTodayDate();
                insertPeriod(periodsTable1);
            }
            SettingsTable settingsTable = sqLiteSettingsFunctions.getSettingByLabel(Variables.settingPeriod);
            settingsTable.value = String.valueOf(getAllPeriods().get(0).period_id);
            updateSettings(settingsTable);
        }
    }

    public void deletePeriod(PeriodsTable p) {
        List<IncomesTable> incomesTables = getIncomesFromPeriod(p.label);
        List<ExpensesTable> expensesTables = getExpensesFromPeriod(p.label);
        List<InvoicesTable> invoicesTables = getInvoicesFromPeriod(p.label);

        for (InvoicesTable i : invoicesTables)
            deleteInvoice(i);
        for (IncomesTable i : incomesTables)
            deleteIncome(i);
        for (ExpensesTable e : expensesTables)
            deleteExpense(e);

        sqlitePeriodsFunctions.deletePeriod(p);

        checkSettingsPeriod();
    }

    private List<InvoicesTable> getInvoicesFromPeriod(String period) {
        return sqLiteInvoicesFunctions.getInvoicesFromPeriod(period);
    }

    private List<ExpensesTable> getExpensesFromPeriod(String period) {
        return sqLiteExpensesFunctions.getExpensesFromPeriod(period);
    }

    private List<IncomesTable> getIncomesFromPeriod(String period) {
        return sqLiteIncomesFunctions.getIncomesFromPeriod(period);
    }

    // MODEL INVOICE
    //OK
    public void insertModelInvoice(ModeleInvoices newModelInvoice) {
        sqLiteModeleInvoiceFunctions.insertModelInvoice(newModelInvoice);
    }

    // OK
    public void deleteModelInvoice(ModeleInvoices modeleInvoices) {
        sqLiteModeleInvoiceFunctions.deleteModelInvoice(modeleInvoices);
    }

    // OK
    public ModeleInvoices getModeleInvoiceById(long id) {
        return sqLiteModeleInvoiceFunctions.getModeleInvoiceById(id);
    }
    //

    // MODEL INCOME
    public void insertModelIncome(ModeleIncomes newModelIncome) {
        sqLiteModeleIncomeFunctions.insertModelIncome(newModelIncome);
    }

    public void deleteModelIncome(ModeleIncomes modelIncome) {
        sqLiteModeleIncomeFunctions.deleteModelIncome(modelIncome);
    }

    public ModeleIncomes getModeleIncomeById(long id) {
        return sqLiteModeleIncomeFunctions.getModeleIncomeById(id);
    }
    //
}
