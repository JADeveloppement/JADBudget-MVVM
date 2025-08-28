package fr.jadeveloppement.budgetsjad.sqlite.helper;

import android.content.Context;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.sqlite.DatabaseInstance;
import fr.jadeveloppement.budgetsjad.sqlite.dao.InvoicesDAO;
import fr.jadeveloppement.budgetsjad.sqlite.dao.TransactionsDAO;
import fr.jadeveloppement.budgetsjad.sqlite.tables.InvoicesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.TransactionsTable;

public class SQLiteTransactionsFunctions {
    private final DatabaseInstance dbFunctions;
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // Shared thread pool
    private final Context context;

    private final TransactionsDAO transactionsDAO;

    public SQLiteTransactionsFunctions(Context c){
        this.context = c.getApplicationContext();
        this.dbFunctions = DatabaseInstance.getInstance(context);
        this.transactionsDAO = dbFunctions.transactionsDAO();
    }

    public List<TransactionsTable> getTransactionsByType(String type) {
        try {
            return executorService.submit(() -> transactionsDAO.getTransactionsByType(type)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteTransactionsFunctions > getTransactionsByType", e);
            return Collections.emptyList();
        }
    }

    public List<TransactionsTable> getAllTransactions(){
        try {
            return executorService.submit(transactionsDAO::getAllTransactions).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteTransactionsFunctions > getAllTransactions", e);
            return Collections.emptyList();
        }
    }
}
