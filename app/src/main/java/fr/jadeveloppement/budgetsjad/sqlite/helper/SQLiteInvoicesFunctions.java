package fr.jadeveloppement.budgetsjad.sqlite.helper;

import android.content.Context;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.sqlite.DatabaseInstance;
import fr.jadeveloppement.budgetsjad.sqlite.dao.InvoicesDAO;
import fr.jadeveloppement.budgetsjad.sqlite.tables.InvoicesTable;

public class SQLiteInvoicesFunctions {
    private final DatabaseInstance dbFunctions;
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // Shared thread pool
    private final Context context;

    private final InvoicesDAO invoicesDAO;

    public SQLiteInvoicesFunctions(Context c){
        this.context = c.getApplicationContext();
        this.dbFunctions = DatabaseInstance.getInstance(context);
        this.invoicesDAO = dbFunctions.invoicesDAO();
    }

    public Long insertInvoice(InvoicesTable invoice) {
        try {
            return executorService.submit(() -> invoicesDAO.insertInvoice(invoice)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteInvoicesFunction > insertInvoice", e);
            return null;
        }
    }

    public List<InvoicesTable> getAllInvoices() {
        try {
            return executorService.submit(invoicesDAO::getAllInvoices).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteInvoicesFunction > getAllInvoices", e);
            return Collections.emptyList();
        }
    }

    public InvoicesTable getInvoiceById(long id) {
        try {
            return executorService.submit(() -> invoicesDAO.getInvoiceById(id)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteInvoicesFunction > getInvoiceById", e);
            return null;
        }
    }

    public void deleteInvoice(InvoicesTable i) {
        try {
            executorService.submit(() -> invoicesDAO.deleteInvoice(i)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteInvoicesFunction > deleteInvoice", e);
        }
    }

    public void updateInvoice(InvoicesTable i) {
        try {
            executorService.submit(() -> invoicesDAO.updateInvoice(i)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteInvoicesFunction > updateInvoice", e);
        }
    }

    public List<InvoicesTable> getInvoicesFromPeriod(String period) {
        try {
            return executorService.submit(() -> invoicesDAO.getInvoicesFromPeriod(period)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteInvoicesFunction > getInvoicesFromPeriod", e);
            return Collections.emptyList();
        }
    }
}
