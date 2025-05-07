package fr.jadeveloppement.budgetsjad.sqlite.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.jadeveloppement.budgetsjad.sqlite.tables.IncomesTable;

@Dao
public interface IncomesDAO {
    @Query("SELECT * FROM incomes WHERE date = (SELECT label FROM periods WHERE period_id = ( SELECT value FROM settings WHERE label = 'period_id' ) )" +
            "AND account_id = ( SELECT value FROM settings WHERE label = 'account_id' )")
    List<IncomesTable> getAllIncomes();

    @Query("SELECT * FROM incomes WHERE date = :date AND account_id = ( SELECT value FROM settings WHERE label = 'account_id' )")
    List<IncomesTable> getIncomesFromPeriod(String date);

    @Query("SELECT * FROM incomes WHERE income_id = :id")
    IncomesTable getIncomeById(long id);

    @Insert
    long insertIncomes(IncomesTable incomesTable);

    @Update
    void updateIncomes(IncomesTable incomesTable);

    @Delete
    void deleteIncomes(IncomesTable incomesTable);
}
