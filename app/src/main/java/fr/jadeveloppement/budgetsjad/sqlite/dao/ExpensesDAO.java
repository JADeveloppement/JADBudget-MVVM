package fr.jadeveloppement.budgetsjad.sqlite.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.jadeveloppement.budgetsjad.sqlite.tables.ExpensesTable;

@Dao
public interface ExpensesDAO {
    @Query("SELECT * FROM expenses " +
            "WHERE date = (SELECT label FROM periods WHERE period_id = ( SELECT value FROM settings WHERE label = 'period_id' ) ) " +
            "AND account_id = ( SELECT value FROM settings WHERE label = 'account_id' )")
    List<ExpensesTable> getAllExpenses();

    @Query("SELECT * FROM expenses WHERE date = :date AND account_id = ( SELECT value FROM settings WHERE label = 'account_id' )")
    List<ExpensesTable> getExpensesFromPeriod(String date);

    @Query("SELECT * FROM expenses WHERE expense_id = :id")
    ExpensesTable getExpenseById(long id);

    @Insert
    long insertExpense(ExpensesTable expensesTable);

    @Update
    void updateExpense(ExpensesTable expensesTable);

    @Delete
    void deleteExpense(ExpensesTable expensesTable);
}
