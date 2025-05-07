package fr.jadeveloppement.budgetsjad.sqlite.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

@Dao
public interface SettingsDAO {
    @Query("SELECT * FROM settings")
    List<SettingsTable> getAllSettings();

    @Insert
    long insertSetting(SettingsTable accountsTable);

    @Update
    void updateSetting(SettingsTable accountsTable);

    @Delete
    void deleteSetting(SettingsTable accountsTable);

    @Query("SELECT * FROM settings WHERE label = :label")
    SettingsTable getSettingByLabel(String label);

    @Query("SELECT * FROM settings WHERE settings_id = :id")
    SettingsTable getSettingById(long id);
}
