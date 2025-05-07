package fr.jadeveloppement.budgetsjad.sqlite.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;

@Dao
public interface PeriodsDAO {
    @Query("SELECT * FROM periods ORDER BY label DESC")
    List<PeriodsTable> getAllPeriods();

    @Insert
    long insertPeriod(PeriodsTable period);

    @Update
    void updatePeriod(PeriodsTable period);

    @Delete
    void deletePeriod(PeriodsTable period);

    @Query("SELECT * FROM periods WHERE label = :label")
    PeriodsTable getPeriodByLabel(String label);

    @Query("SELECT * FROM periods WHERE period_id = :id")
    PeriodsTable getPeriodById(long id);
}
