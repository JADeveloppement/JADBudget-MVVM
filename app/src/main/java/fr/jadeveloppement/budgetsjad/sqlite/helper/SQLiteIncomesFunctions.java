package fr.jadeveloppement.budgetsjad.sqlite.helper;

import android.content.Context;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.sqlite.DatabaseInstance;
import fr.jadeveloppement.budgetsjad.sqlite.dao.IncomesDAO;
import fr.jadeveloppement.budgetsjad.sqlite.dao.InvoicesDAO;
import fr.jadeveloppement.budgetsjad.sqlite.tables.IncomesTable;

public class SQLiteIncomesFunctions {
    private final DatabaseInstance dbFunctions;
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // Shared thread pool
    private final Context context;

    private final IncomesDAO incomesDAO;

    public SQLiteIncomesFunctions(Context c){
        this.context = c.getApplicationContext();
        this.dbFunctions = DatabaseInstance.getInstance(context);
        this.incomesDAO = dbFunctions.incomesDAO();
    }

    public Long insertIncome(IncomesTable income) {
        try {
            return executorService.submit(() -> incomesDAO.insertIncomes(income)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteIncomesFunctions > insertIncome", e);
            return null;
        }
    }

    public List<IncomesTable> getAllIncomes() {
        try {
            return executorService.submit(incomesDAO::getAllIncomes).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteIncomesFunctions > getAllIncome", e);
            return Collections.emptyList();
        }
    }

    public IncomesTable getIncomeById(long id) {
        try {
            return executorService.submit(() -> incomesDAO.getIncomeById(id)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteIncomesFunctions > getIncomeById", e);
            return null;
        }
    }

    public void deleteIncome(IncomesTable i) {
        try {
            executorService.submit(() -> incomesDAO.deleteIncomes(i)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteIncomesFunctions > deleteIncome", e);
        }
    }

    public void updateIncome(IncomesTable i) {
        try {
            executorService.submit(() -> incomesDAO.updateIncomes(i)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteIncomesFunctions > updateIncome", e);
        }
    }

    public List<IncomesTable> getIncomesFromPeriod(String period) {
        try {
            return executorService.submit(() -> incomesDAO.getIncomesFromPeriod(period)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteIncomesFunctions > getIncomesFromPeriod", e);
            return Collections.emptyList();
        }
    }
}
