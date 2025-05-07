package fr.jadeveloppement.budgetsjad.sqlite.helper;

import android.content.Context;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.sqlite.DatabaseInstance;
import fr.jadeveloppement.budgetsjad.sqlite.dao.AccountsDAO;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;

public class SQLiteAccountsFunctions {
    private final DatabaseInstance dbFunctions;
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // Shared thread pool
    private final Context context;

    private final AccountsDAO accountsDAO;

    public SQLiteAccountsFunctions(Context c){
        this.context = c.getApplicationContext();
        this.dbFunctions = DatabaseInstance.getInstance(context);
        this.accountsDAO = dbFunctions.accountsDAO();
    }

    public List<AccountsTable> getAllAccounts(){
        try {
            return executorService.submit(accountsDAO::getAllAccounts).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteAccountsFunctions > getAllAccounts", e);
            return Collections.emptyList();
        }
    }

    public Long insertAccount(AccountsTable a) {
        try {
            return executorService.submit(() -> accountsDAO.insertAccount(a)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteAccountsFunctions > insertAccount", e);
            return null;
        }
    }

    public void deleteAccount(AccountsTable a) {
        try {
            executorService.submit(() -> accountsDAO.deleteAccount(a)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteAccountsFunctions > deleteAccount", e);
        }
    }

    public void updateAccount(AccountsTable a) {
        try {
            executorService.submit(() -> accountsDAO.updateAccount(a)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteAccountsFunctions > updateAccount", e);
        }
    }

    public AccountsTable getAccountById(long id) {
        try {
            return executorService.submit(() -> accountsDAO.getAccountById(id)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteAccountsFunctions > getAccountById", e);
            return null;
        }
    }
}
