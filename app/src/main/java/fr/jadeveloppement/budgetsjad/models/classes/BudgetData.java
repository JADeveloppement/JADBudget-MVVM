package fr.jadeveloppement.budgetsjad.models.classes;

import static java.lang.Long.parseLong;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ExpensesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.IncomesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.InvoicesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleIncomes;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleInvoices;

public class BudgetData {
    private final String TAG = "BudgetJAD";

    private List<Transaction> transactions = new ArrayList<>();
    private Context context;
    private Functions functions;

    public BudgetData(Context c){
        this.context = c.getApplicationContext();
        this.functions = new Functions(context);
    }

    public void addTransaction(Transaction t){
        if (t.getType() == Enums.TransactionType.INVOICE) addInvoice(t);
        else if (t.getType() == Enums.TransactionType.INCOME) addIncome(t);
        else if (t.getType() == Enums.TransactionType.EXPENSE) addExpense(t);
        else if (t.getType() == Enums.TransactionType.MODELINVOICE) addModelInvoice(t);
        else if (t.getType() == Enums.TransactionType.MODELINCOME) addModelIncome(t);
    }

    private void addModelInvoice(Transaction t) {
        ModeleInvoices newModelInvoice = new ModeleInvoices();
        newModelInvoice.label = t.getLabel();
        newModelInvoice.amount = t.getAmount();
        newModelInvoice.paid = "0";
        newModelInvoice.date = "";
        functions.insertModelInvoice(newModelInvoice);
    }

    private void addModelIncome(Transaction t) {
        ModeleIncomes newModelIncome = new ModeleIncomes();
        newModelIncome.label = t.getLabel();
        newModelIncome.amount = t.getAmount();
        newModelIncome.paid = "0";
        newModelIncome.date = "";
        functions.insertModelIncome(newModelIncome);
    }

    private void addInvoice(Transaction t){
        InvoicesTable newInvoice = new InvoicesTable();
        newInvoice.label = t.getLabel();
        newInvoice.amount = t.getAmount();
        newInvoice.account_id = parseLong(t.getAccount());
        newInvoice.paid = "0";
        newInvoice.date = functions.getPeriodById(parseLong(functions.getSettingByLabel(Variables.settingPeriod).value)).label;
        functions.insertInvoice(newInvoice);
    }

    private void addIncome(Transaction t){
        IncomesTable newIncome = new IncomesTable();
        newIncome.label = t.getLabel();
        newIncome.amount = t.getAmount();
        newIncome.account_id = parseLong(functions.getSettingByLabel(Variables.settingAccount).value);
        newIncome.paid = "0";
        newIncome.date = functions.getPeriodById(parseLong(functions.getSettingByLabel(Variables.settingPeriod).value)).label;
        functions.insertIncome(newIncome);
    }

    private void addExpense(Transaction t){
        ExpensesTable newExpense = new ExpensesTable();
        newExpense.label = t.getLabel();
        newExpense.amount = t.getAmount();
        newExpense.account_id = parseLong(functions.getSettingByLabel(Variables.settingAccount).value);
        newExpense.date = functions.getPeriodById(parseLong(functions.getSettingByLabel(Variables.settingPeriod).value)).label;
        functions.insertExpense(newExpense);
    }

    public List<Transaction> getInvoicesTransaction(){
        return functions.getAllInvoicesTransaction();
    }

    public List<Transaction> getIncomesTransaction(){
        return functions.getAllIncomesTransaction();
    }

    public List<Transaction> getExpensesTransaction(){
        return functions.getAllExpensesTransaction();
    }

    public List<Transaction> getModelInvoiceTransaction(){
        return functions.getModelInvoiceTransaction();
    }

    public List<Transaction> getModelIncomeTransaction(){
        return functions.getModelIncomeTransaction();
    }

    public void deleteTransaction(Transaction t) {
        if (!t.getId().isBlank()){
            if (t.getType() == Enums.TransactionType.INCOME){
                IncomesTable i = functions.getIncomeById(parseLong(t.getId()));
                functions.deleteIncome(i);
            } else if (t.getType() == Enums.TransactionType.INVOICE){
                InvoicesTable i = functions.getInvoiceById(parseLong(t.getId()));
                functions.deleteInvoice(i);
            } else if (t.getType() == Enums.TransactionType.EXPENSE){
                ExpensesTable e = functions.getExpenseById(parseLong(t.getId()));
                functions.deleteExpense(e);
            } else if (t.getType() == Enums.TransactionType.MODELINCOME){
                ModeleIncomes modeleIncomes = functions.getModeleIncomeById(parseLong(t.getId()));
                functions.deleteModelIncome(modeleIncomes);
            } else if (t.getType() == Enums.TransactionType.MODELINVOICE){
                ModeleInvoices modeleInvoices = functions.getModeleInvoiceById(parseLong(t.getId()));
                functions.deleteModelInvoice(modeleInvoices);
            }
        }
    }

    public void updateTransaction(Transaction transaction) {
        Log.d(TAG, "BudgetData > updateTransaction: " + transaction.getLabel() + " / " + transaction.getAmount() + " / ID : " + transaction.getId() + " / " + transaction.getType());
        if (!transaction.getId().isBlank()){
            if (transaction.getType() == Enums.TransactionType.INVOICE){
                InvoicesTable i = functions.getInvoiceById(parseLong(transaction.getId()));
                i.label = transaction.getLabel();
                i.amount = transaction.getAmount();
                i.paid = transaction.getPaid();
                Log.d(TAG, "BudgetData > updateTransaction: " + i.label + " / " + i.amount + " / ID : " + i.invoice_id);
                functions.updateInvoice(i);
            } else if (transaction.getType() == Enums.TransactionType.INCOME){
                IncomesTable i = functions.getIncomeById(parseLong(transaction.getId()));
                i.label = transaction.getLabel();
                i.amount = transaction.getAmount();
                i.paid = transaction.getPaid();
                functions.updateIncome(i);
            } else if (transaction.getType() == Enums.TransactionType.EXPENSE){
                ExpensesTable e = functions.getExpenseById(parseLong(transaction.getId()));
                e.label = transaction.getLabel();
                e.amount = transaction.getAmount();
                functions.updateExpense(e);
            } else if (transaction.getType() == Enums.TransactionType.MODELINCOME){
                ModeleIncomes modeleIncomes = functions.getModeleIncomeById(parseLong(transaction.getId()));
                modeleIncomes.label = transaction.getLabel();
                modeleIncomes.amount = transaction.getAmount();
                functions.updateModeleIncome(modeleIncomes);
            } else if (transaction.getType() == Enums.TransactionType.MODELINVOICE){
                ModeleInvoices modeleInvoices = functions.getModeleInvoiceById(parseLong(transaction.getId()));
                modeleInvoices.label = transaction.getLabel();
                modeleInvoices.amount = transaction.getAmount();
                functions.updateModeleInvoice(modeleInvoices);
            }
        }
    }
}
