package fr.jadeveloppement.budgetsjad.sqlite.helper;

import android.content.Context;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.sqlite.DatabaseInstance;
import fr.jadeveloppement.budgetsjad.sqlite.dao.ModeleIncomesDAO;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleIncomes;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleInvoices;

public class SQLiteModeleIncomeFunctions {

    private final DatabaseInstance dbFunctions;
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // Shared thread pool
    private final Context context;

    private final ModeleIncomesDAO modeleIncomesDAO;

    public SQLiteModeleIncomeFunctions(Context c){
        this.context = c.getApplicationContext();
        this.dbFunctions = DatabaseInstance.getInstance(context);
        this.modeleIncomesDAO = dbFunctions.modeleIncomesDAO();
    }

    public List<ModeleIncomes> getAllModelIncome() {
        try {
            return executorService.submit(modeleIncomesDAO::getAllModeleIncome).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteModeleIncomeFunctions > getAllModelIncome", e);
            return Collections.emptyList();
        }
    }

    public void insertModelIncome(ModeleIncomes newModelIncome) {
        try {
            executorService.submit(() -> modeleIncomesDAO.insertModeleIncome(newModelIncome)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteModeleIncomeFunctions > insertModelIncome", e);
        }
    }

    public void deleteModelIncome(ModeleIncomes modelIncome) {
        try {
            executorService.submit(() -> modeleIncomesDAO.deleteModeleIncome(modelIncome)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteModeleIncomeFunctions > deleteModelIncome", e);
        }
    }

    public ModeleIncomes getModeleIncomeById(long id) {
        try {
            return executorService.submit(() -> modeleIncomesDAO.getModeleIncomeById(id)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteModeleIncomeFunctions > getModeleIncomeById", e);
            return null;
        }
    }

    public void updateModeleIncome(ModeleIncomes modeleIncomes) {
        try {
            executorService.submit(() -> modeleIncomesDAO.updateModeleIncome(modeleIncomes)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteModeleIncomeFunctions > updateModeleIncome", e);
        }
    }
}
