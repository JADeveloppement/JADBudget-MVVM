package fr.jadeveloppement.budgetsjad.sqlite.helper;

import android.content.Context;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.sqlite.DatabaseInstance;
import fr.jadeveloppement.budgetsjad.sqlite.dao.ModeleInvoicesDAO;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleInvoices;

public class SQLiteModeleInvoiceFunctions {
    private final DatabaseInstance dbFunctions;
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // Shared thread pool
    private final Context context;

    private final ModeleInvoicesDAO modeleInvoicesDAO;

    public SQLiteModeleInvoiceFunctions(Context c){
        this.context = c.getApplicationContext();
        this.dbFunctions = DatabaseInstance.getInstance(context);
        this.modeleInvoicesDAO = dbFunctions.modeleInvoicesDAO();
    }

    public List<ModeleInvoices> getAllModelInvoice() {
        try {
            return executorService.submit(modeleInvoicesDAO::getAllModeleInvoices).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteModeleInvoiceFunctions > getAllModelInvoice", e);
            return Collections.emptyList();
        }
    }

    public void insertModelInvoice(ModeleInvoices newModelInvoice) {
        try {
            executorService.submit(() -> modeleInvoicesDAO.insertModeleInvoice(newModelInvoice)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteModeleInvoiceFunctions > insertModelInvoice", e);
        }
    }

    public void deleteModelInvoice(ModeleInvoices modelInvoice) {
        try {
            executorService.submit(() -> modeleInvoicesDAO.deleteModeleInvoice(modelInvoice)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteModeleInvoiceFunctions > deleteModelInvoice", e);
        }
    }

    public ModeleInvoices getModeleInvoiceById(long id) {
        try {
            return executorService.submit(() -> modeleInvoicesDAO.getModeleInvoiceById(id)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteModeleInvoiceFunctions > getModeleInvoiceById", e);
            return null;
        }
    }
}
