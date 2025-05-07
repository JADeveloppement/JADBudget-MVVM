package fr.jadeveloppement.budgetsjad.sqlite.helper;

import android.content.Context;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.sqlite.DatabaseInstance;
import fr.jadeveloppement.budgetsjad.sqlite.dao.PeriodsDAO;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;

public class SQLitePeriodsFunctions {
    private final DatabaseInstance dbFunctions;
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // Shared thread pool
    private final Context context;

    private final PeriodsDAO periodsDAO;

    public SQLitePeriodsFunctions(Context c){
        this.context = c.getApplicationContext();
        this.dbFunctions = DatabaseInstance.getInstance(context);
        this.periodsDAO = dbFunctions.periodsDAO();
    }

    public List<PeriodsTable> getAllPeriods() {
        try {
             return executorService.submit(periodsDAO::getAllPeriods).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLitePeriodsFunctions > getAllPeriods", e);
            return Collections.emptyList();
        }
    }

    public void insertPeriod(PeriodsTable periodsTable) {
        try {
            executorService.submit(() -> periodsDAO.insertPeriod(periodsTable)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLitePeriodsFunctions > insertPeriod", e);
        }
    }

    public PeriodsTable getPeriodById(long id) {
        try {
            return executorService.submit(() -> periodsDAO.getPeriodById(id)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLitePeriodsFunctions > getPeriodById", e);
            return null;
        }
    }

    public PeriodsTable getPeriodByLabel(String period) {
        try {
            return executorService.submit(() -> periodsDAO.getPeriodByLabel(period)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLitePeriodsFunctions > getPeriodByLabel", e);
            return null;
        }
    }

    public void deletePeriod(PeriodsTable p) {
        try {
            executorService.submit(() -> periodsDAO.deletePeriod(p)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLitePeriodsFunctions > deletePeriod", e);
        }
    }
}
