package fr.jadeveloppement.budgetsjad.models;


import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.models.classes.BudgetData;
import fr.jadeveloppement.budgetsjad.sqlite.tables.TransactionsTable;

public class BudgetViewModel extends AndroidViewModel {

    private final String TAG = "JADBudget";
    private final MutableLiveData<Double> forecastFinalTransactionsTable, forecastEncoursTransactionsTable,
            amountInvoiceTransactionsTablePaid, amountInvoiceTransactionsTableUnpaid;
    private final MutableLiveData<Integer> nbInvoiceTransactionsTablePaid;
    private final MutableLiveData<List<TransactionsTable>>
            invoicesTransactionsTableLiveData,
            incomesTransactionsTableLiveData,
            expensesTransactionsTableLiveData,
            modelIncomeTransactionsTableLiveData,
            modelInvoiceTransactionsTableLiveData;
    private final BudgetData budgetData;
    private Functions functions;


    public BudgetViewModel(Application application) {
        super(application);
        this.budgetData = new BudgetData(application);
        this.functions = new Functions(application);


        this.nbInvoiceTransactionsTablePaid = new MutableLiveData<>();
        this.amountInvoiceTransactionsTablePaid = new MutableLiveData<>();
        this.amountInvoiceTransactionsTableUnpaid = new MutableLiveData<>();
        this.invoicesTransactionsTableLiveData = new MutableLiveData<>();
        this.incomesTransactionsTableLiveData = new MutableLiveData<>();
        this.expensesTransactionsTableLiveData = new MutableLiveData<>();
        this.modelIncomeTransactionsTableLiveData = new MutableLiveData<>();
        this.modelInvoiceTransactionsTableLiveData = new MutableLiveData<>();
        this.forecastFinalTransactionsTable = new MutableLiveData<>();
        this.forecastEncoursTransactionsTable = new MutableLiveData<>();

        updateLiveDataTransactionsTable();
    }

    public LiveData<List<TransactionsTable>> getInvoicesTransactionsTable() {
        return invoicesTransactionsTableLiveData;
    }
    public LiveData<List<TransactionsTable>> getIncomesTransactionsTable() {
        return incomesTransactionsTableLiveData;
    }
    public LiveData<List<TransactionsTable>> getExpensesTransactionsTable() {
        return expensesTransactionsTableLiveData;
    }
    public LiveData<List<TransactionsTable>> getModelIncomeTransactionsTable() {
        return modelIncomeTransactionsTableLiveData;
    }
    public LiveData<List<TransactionsTable>> getModelInvoiceTransactionsTable() {
        return modelInvoiceTransactionsTableLiveData;
    }
    public LiveData<Double> getForecastFinalTransactionsTable() {
        return forecastFinalTransactionsTable;
    }
    public LiveData<Double> getForecastEncoursTransactionsTable() {
        return forecastEncoursTransactionsTable;
    }
    public void addTransactionsTable(TransactionsTable t) {
        budgetData.addTransactionsTable(t);
        updateLiveDataTransactionsTable();
    }
    public void updateTransactionsTable(TransactionsTable transaction) {
        budgetData.updateTransactionsTable(transaction);
        updateLiveDataTransactionsTable();
    }
    public void deleteTransactionsTable(TransactionsTable t) {
        budgetData.deleteTransactionsTable(t);

        updateLiveDataTransactionsTable();
    }
    public void updateLiveDataTransactionsTable() {
        invoicesTransactionsTableLiveData.postValue(budgetData.getInvoicesTransactionsTable());
        incomesTransactionsTableLiveData.postValue(budgetData.getIncomesTransactionsTable());
        expensesTransactionsTableLiveData.postValue(budgetData.getExpensesTransactionsTable());
        modelInvoiceTransactionsTableLiveData.postValue(budgetData.getModelInvoiceTransactionsTable());
        modelIncomeTransactionsTableLiveData.postValue(budgetData.getModelIncomeTransactionsTable());
        updateForecastFinalTransactionsTable();
        updateForecastEnCoursTransactionsTable();

        int nbPaid = 0;
        double amountPaid = 0;
        double amountUnpaid = 0;

        for (TransactionsTable t : budgetData.getInvoicesTransactionsTable()) {
            if (t.paid.equalsIgnoreCase("1")) {
                nbPaid++;
                amountPaid += t.amount;
            } else {
                amountUnpaid += t.amount;
            }
        }

        nbInvoiceTransactionsTablePaid.postValue(nbPaid);
        amountInvoiceTransactionsTableUnpaid.postValue(amountUnpaid);
        amountInvoiceTransactionsTablePaid.postValue(amountPaid);
    }
    private void updateForecastFinalTransactionsTable() {
        double amountInvoice = 0;
        double amountIncome = 0;
        for (TransactionsTable invoice : budgetData.getInvoicesTransactionsTable())
            amountInvoice += invoice.amount;
        for (TransactionsTable income : budgetData.getIncomesTransactionsTable())
            amountIncome += income.amount;

        forecastFinalTransactionsTable.postValue(amountIncome - amountInvoice);

    }
    private void updateForecastEnCoursTransactionsTable() {
        double amountInvoice = 0;
        double amountIncome = 0;
        double amountExpense = 0;
        for (TransactionsTable invoice : budgetData.getInvoicesTransactionsTable())
            if (invoice.paid.equalsIgnoreCase("1")) amountInvoice += invoice.amount;
        for (TransactionsTable income : budgetData.getIncomesTransactionsTable())
            amountIncome += income.amount;
        for (TransactionsTable expense : budgetData.getExpensesTransactionsTable())
            amountExpense += expense.amount;

        forecastEncoursTransactionsTable.postValue(amountIncome - amountInvoice - amountExpense);
    }
}