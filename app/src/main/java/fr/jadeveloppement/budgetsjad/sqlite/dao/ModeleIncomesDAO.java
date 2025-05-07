package fr.jadeveloppement.budgetsjad.sqlite.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleIncomes;

@Dao
public interface ModeleIncomesDAO {
    @Query("SELECT * FROM modeleincomes")
    List<ModeleIncomes> getAllModeleIncome();

    @Query("SELECT * FROM modeleincomes WHERE modeleincome_id = :id")
    ModeleIncomes getModeleIncomeById(long id);

    @Insert
    long insertModeleIncome(ModeleIncomes modeleIncomes);

    @Update
    void updateModeleIncome(ModeleIncomes modeleIncomes);

    @Delete
    void deleteModeleIncome(ModeleIncomes modeleIncomes);
}
