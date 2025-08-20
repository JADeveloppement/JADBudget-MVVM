package fr.jadeveloppement.budgetsjad.sqlite.helper;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.sqlite.DatabaseInstance;
import fr.jadeveloppement.budgetsjad.sqlite.dao.CategoryDAO;
import fr.jadeveloppement.budgetsjad.sqlite.tables.CategoryTable;

public class SQLiteCategoryFunctions {
    private final DatabaseInstance dbFunctions;
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // Shared thread pool
    private final Context context;

    private final CategoryDAO categoryDAO;

    public SQLiteCategoryFunctions(Context c){
        this.context = c.getApplicationContext();
        this.dbFunctions = DatabaseInstance.getInstance(context);
        this.categoryDAO = dbFunctions.categoryDAO();
    }

    public List<CategoryTable> getAllCategories(){
        try {
            return executorService.submit(categoryDAO::getAllCategories).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteCategoryFunctions > getAllCategories", e);
            return Collections.emptyList();
        }
    }

    public Long insertCategory(CategoryTable a) {
        try {
            return executorService.submit(() -> categoryDAO.insertCategory(a)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteCategoryFunctions > insertCategory", e);
            return null;
        }
    }

    public void deleteCategory(CategoryTable a) {
        try {
            executorService.submit(() -> categoryDAO.deleteCategory(a)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteCategoryFunctions > deleteCategory", e);
        }
    }

    public void updateCategory(CategoryTable a) {
        try {
            executorService.submit(() -> categoryDAO.updateCategory(a)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteCategoryFunctions > updateCategory", e);
        }
    }

    public CategoryTable getCategoryById(long id) {
        try {
            return executorService.submit(() -> categoryDAO.getCategoryById(id)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteCategoryFunctions > getCategoryById", e);
            return null;
        }
    }

    public CategoryTable getCategoryByLabel(@NonNull String label) {
        try {
            return executorService.submit(() -> categoryDAO.getCategoryByLabel(label)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteCategoryFunctions > getCategoryById", e);
            return null;
        }
    }
}
