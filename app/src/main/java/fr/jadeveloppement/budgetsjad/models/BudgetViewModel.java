package fr.jadeveloppement.budgetsjad.models;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
import static java.util.Objects.isNull;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.models.classes.BudgetData;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.InvoicesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class BudgetViewModel extends ViewModel {

    private final String TAG = "BudgetJAD";

    private final Context context;
    private BudgetData budgetData;
    private MutableLiveData<List<Transaction>> invoicesLiveData, incomesLiveData, expensesLiveData, modelIncomeLiveData, modelInvoiceLiveData;
    private MutableLiveData<List<AccountsTable>> accountsLiveData;
    private MutableLiveData<Double> forecastFinal, forecastEncours;
    private MutableLiveData<Integer> nbInvoicePaid;
    private MutableLiveData<Double> amountInvoicePaid, amountInvoiceUnpaid;
    private MutableLiveData<PeriodsTable> periodSelected;
    private Functions functions;
    private MutableLiveData<SettingsTable> settingsAccount;
    private MutableLiveData<SettingsTable> settingsPeriod;

    public BudgetViewModel(Context c) {
        this.context = c.getApplicationContext();
        this.budgetData = new BudgetData(context.getApplicationContext());
        this.functions = new Functions(context.getApplicationContext());
        this.invoicesLiveData = new MutableLiveData<>();
        this.incomesLiveData = new MutableLiveData<>();
        this.expensesLiveData = new MutableLiveData<>();
        this.forecastFinal = new MutableLiveData<>();
        this.forecastEncours = new MutableLiveData<>();
        this.nbInvoicePaid = new MutableLiveData<>();
        this.amountInvoicePaid = new MutableLiveData<>();
        this.amountInvoiceUnpaid = new MutableLiveData<>();
        this.accountsLiveData = new MutableLiveData<>();
        this.periodSelected = new MutableLiveData<>();
        this.settingsAccount = new MutableLiveData<>();
        this.modelInvoiceLiveData = new MutableLiveData<>();
        this.modelIncomeLiveData = new MutableLiveData<>();

        this.functions = new Functions(context);

        updateLiveData();
    }

    public LiveData<Double> getForecastFinal(){
        return forecastFinal;
    }

    public LiveData<Double> getForecastEncours(){
        return forecastEncours;
    }

    public LiveData<List<AccountsTable>> getListOfAccounts(){
        return accountsLiveData;
    }

    public LiveData<PeriodsTable> getPeriodSelected(){
        return periodSelected;
    }

    public LiveData<SettingsTable> getSettingsAccount(){
        return settingsAccount;
    }

    private void updateForecastFinal(){
        double amountInvoice = 0;
        double amountIncome = 0;
        for (Transaction invoice : budgetData.getInvoicesTransaction())
            amountInvoice += parseDouble(invoice.getAmount());
        for(Transaction income : budgetData.getIncomesTransaction())
            amountIncome += parseDouble(income.getAmount());

        forecastFinal.postValue(amountIncome - amountInvoice);

    }

    private void updateForecastEnCours(){
        double amountInvoice = 0;
        double amountIncome = 0;
        double amountExpense = 0;
        for (Transaction invoice : budgetData.getInvoicesTransaction())
            if (invoice.getPaid().equalsIgnoreCase("1") ) amountInvoice += parseDouble(invoice.getAmount());
        for(Transaction income : budgetData.getIncomesTransaction())
            amountIncome += parseDouble(income.getAmount());
        for(Transaction expense : budgetData.getExpensesTransaction())
            amountExpense += parseDouble(expense.getAmount());

        forecastEncours.postValue(amountIncome - amountInvoice - amountExpense);
        settingsAccount.postValue(functions.getSettingByLabel(Variables.settingAccount));
    }

    public LiveData<List<Transaction>> getInvoices() {
        return invoicesLiveData;
    }

    public LiveData<List<Transaction>> getIncomes() {
        return incomesLiveData;
    }

    public LiveData<List<Transaction>> getExpenses() {
        return expensesLiveData;
    }

    public LiveData<List<Transaction>> getModelIncome() {
        return modelIncomeLiveData;
    }

    public LiveData<List<Transaction>> getModelInvoice() {
        return modelInvoiceLiveData;
    }

    public void deleteTransaction(Transaction t){
        Log.d(TAG, "deleteTransaction: delete transaction : " + t.getType());
        budgetData.deleteTransaction(t);

        updateLiveData();
    }

    public void addTransaction(Transaction t) {
        budgetData.addTransaction(t);

        updateLiveData();
    }

    public void updateLiveData() {
        invoicesLiveData.postValue(budgetData.getInvoicesTransaction());
        incomesLiveData.postValue(budgetData.getIncomesTransaction());
        expensesLiveData.postValue(budgetData.getExpensesTransaction());
        modelInvoiceLiveData.postValue(budgetData.getModelInvoiceTransaction());
        modelIncomeLiveData.postValue(budgetData.getModelIncomeTransaction());
        updateForecastFinal();
        updateForecastEnCours();

        int nbPaid = 0;
        double amountPaid = 0;
        double amountUnpaid = 0;

        for (Transaction t : budgetData.getInvoicesTransaction()){
            if (t.getPaid().equalsIgnoreCase("1")) {
                nbPaid++;
                amountPaid += parseDouble(t.getAmount());
            } else {
                amountUnpaid += parseDouble(t.getAmount());
            }
        }

        nbInvoicePaid.postValue(nbPaid);
        amountInvoiceUnpaid.postValue(amountUnpaid);
        amountInvoicePaid.postValue(amountPaid);

        List<AccountsTable> updatedListAccounts = functions.getAllAccounts();
        if (isNull(updatedListAccounts) || updatedListAccounts.isEmpty()) updatedListAccounts = Collections.emptyList();
        accountsLiveData.postValue(updatedListAccounts);

        periodSelected.postValue(functions.getPeriodById(parseLong(functions.getSettingByLabel(Variables.settingPeriod).value)));
        settingsAccount.postValue(functions.getSettingByLabel(Variables.settingAccount));
    }

    public void accountChanged() {
        updateLiveData();
    }

    public void updateTransaction(Transaction transaction) {
        budgetData.updateTransaction(transaction);
        updateLiveData();
    }

    // ACCOUNTS
    public void insertAccount(AccountsTable newAccount) {
        functions.insertAccount(newAccount);
        updateLiveData();
    }

    public void updateAccount(AccountsTable accountsTable){
        functions.updateAccount(accountsTable);
        updateLiveData();
    }

    public void deleteAccount(AccountsTable accountsTable){
        functions.deleteAccount(accountsTable);
        updateLiveData();
    }

    public void updateSettingsAccount(String newAccount) {
        if (!functions.getSettingByLabel(Variables.settingAccount).value.equalsIgnoreCase(newAccount)) {
            SettingsTable settingsAccount = functions.getSettingByLabel(Variables.settingAccount);
            settingsAccount.value = newAccount;
            functions.updateSettings(settingsAccount);
            updateLiveData();
        }
    }
    //

    // PERIOD
    public void insertPeriod(PeriodsTable newPeriod){
        functions.insertPeriod(newPeriod);
        updatePeriod(newPeriod.label);
    }

    public void deletePeriod(PeriodsTable periodsTable){

    }

    public void updatePeriod(String newPeriod){
        updateSettingsPeriod(newPeriod);
        updateLiveData();
    }

    public void updateSettingsPeriod(String newPeriod) {
        if (!functions.getPeriodById(parseLong(functions.getSettingByLabel(Variables.settingPeriod).value)).label.equalsIgnoreCase(newPeriod)) {
            SettingsTable settingsPeriod = functions.getSettingByLabel(Variables.settingPeriod);
            PeriodsTable periodsTable = functions.getPeriodByLabel(newPeriod);
            settingsPeriod.value = String.valueOf(periodsTable.period_id);
            functions.updateSettings(settingsPeriod);
            updateLiveData();
        }
    }

    public LiveData<SettingsTable> getSettingsPeriod() {
        return settingsPeriod;
    }

    public void insertModelInvoice(PeriodsTable periodsTable) {
        String date = periodsTable.label;
        List<Transaction> listOfModelInvoice = isNull(getModelInvoice().getValue()) ? Collections.emptyList() : getModelInvoice().getValue() ;
        String account = isNull(settingsAccount.getValue()) ? String.valueOf(functions.getAccountById(parseLong(functions.getSettingByLabel(Variables.settingAccount).value)).account_id) : settingsAccount.getValue().value;

        for (Transaction t : listOfModelInvoice){
            t.setType(Transaction.TransactionType.INVOICE);
            t.setDate(date);
            t.setAccount(account);
            Log.d(TAG, "insertModelInvoice: add model " + t.getLabel() + " account : " + t.getAccount() + " date " + t.getDate());
            addTransaction(t);
        }
    }

    public void insertModelIncome(PeriodsTable periodsTable) {
        String date = periodsTable.label;
        List<Transaction> listOfModelIncome = isNull(getModelIncome().getValue()) ? Collections.emptyList() : getModelIncome().getValue() ;
        String account = isNull(settingsAccount.getValue()) ? String.valueOf(functions.getAccountById(parseLong(functions.getSettingByLabel(Variables.settingAccount).value)).account_id) : settingsAccount.getValue().value;

        for (Transaction t : listOfModelIncome){
            t.setType(Transaction.TransactionType.INCOME);
            t.setDate(date);
            t.setAccount(account);
            addTransaction(t);
        }
    }
}