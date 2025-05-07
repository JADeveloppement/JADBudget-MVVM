package fr.jadeveloppement.budgetsjad.sqlite.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "settings"
)
public class SettingsTable {
    @PrimaryKey(autoGenerate = true)
    public long settings_id;

    public String label;
    public String value;
}
