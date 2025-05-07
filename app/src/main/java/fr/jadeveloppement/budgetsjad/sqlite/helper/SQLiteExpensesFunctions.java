package fr.jadeveloppement.budgetsjad.sqlite.helper;

import android.content.Context;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.sqlite.DatabaseInstance;
import fr.jadeveloppement.budgetsjad.sqlite.dao.ExpensesDAO;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ExpensesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.InvoicesTable;

public class SQLiteExpensesFunctions {
    private final DatabaseInstance dbFunctions;
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // Shared thread pool
    private final Context context;

    private final ExpensesDAO expensesDAO;

    public SQLiteExpensesFunctions(Context c){
        this.context = c.getApplicationContext();
        this.dbFunctions = DatabaseInstance.getInstance(context);
        this.expensesDAO = dbFunctions.expensesDAO();
    }
    public Long insertExpense(ExpensesTable expense) {
        try {
            return executorService.submit(() -> expensesDAO.insertExpense(expense)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteExpensesFunctions > insertExpense", e);
            return null;
        }
    }

    public List<ExpensesTable> getAllExpenses() {
        try {
            return executorService.submit(expensesDAO::getAllExpenses).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteInvoicesFunction > getAllExpenses", e);
            return Collections.emptyList();
        }
    }

    public ExpensesTable getExpenseById(long id) {
        try {
            return executorService.submit(() -> expensesDAO.getExpenseById(id)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteInvoicesFunction > getExpenseById", e);
            return null;
        }
    }

    public void deleteExpense(ExpensesTable ex) {
        try {
            executorService.submit(() -> expensesDAO.deleteExpense(ex)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteInvoicesFunction > deleteExpense", e);
        }
    }

    public void updateExpense(ExpensesTable ex) {
        try {
            executorService.submit(() -> expensesDAO.updateExpense(ex)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteInvoicesFunction > updateExpense", e);
        }
    }

    public List<ExpensesTable> getExpensesFromPeriod(String period) {
        try {
            return executorService.submit(() -> expensesDAO.getExpensesFromPeriod(period)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteInvoicesFunction > getExpensesFromPeriod", e);
            return Collections.emptyList();
        }
    }
}
