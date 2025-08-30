package fr.jadeveloppement.budgetsjad.models.classes;

import static java.lang.Long.parseLong;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.sqlite.SQLiteFunctions;
import fr.jadeveloppement.budgetsjad.sqlite.tables.TransactionsTable;

public class BudgetData {
    private final String TAG = "BudgetJAD";

    private final SQLiteFunctions sqLiteFunctions;

    public BudgetData(Context c){
        Context context = c.getApplicationContext();
        this.sqLiteFunctions = new SQLiteFunctions(context);
    }

    public List<TransactionsTable> getInvoicesTransactionsTable(){
        return sqLiteFunctions.getAllTransactionsTableByType(Variables.strTypeInvoice);
    }

    public List<TransactionsTable> getIncomesTransactionsTable(){
        return sqLiteFunctions.getAllTransactionsTableByType(Variables.strTypeIncome);
    }

    public List<TransactionsTable> getExpensesTransactionsTable(){
        List<TransactionsTable> list = sqLiteFunctions.getAllTransactionsTableByType(Variables.strTypeExpense);
        return list;
    }

    public List<TransactionsTable> getModelInvoiceTransactionsTable(){
        return sqLiteFunctions.getAllTransactionsTableByType(Variables.strTypeModelInvoice);
    }

    public List<TransactionsTable> getModelIncomeTransactionsTable(){
        return sqLiteFunctions.getAllTransactionsTableByType(Variables.strTypeModelIncome);
    }

    public void addTransactionsTable(TransactionsTable t){
        sqLiteFunctions.insertTransaction(t);
    }

    public void deleteTransactionsTable(TransactionsTable t){
        sqLiteFunctions.deleteTransactionsTable(t);
    }

    public void updateTransactionsTable(TransactionsTable t) {
        sqLiteFunctions.updateTransactionsTable(t);
    }
}
