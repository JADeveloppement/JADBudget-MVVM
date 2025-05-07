package fr.jadeveloppement.budgetsjad.sqlite.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "periods"
)
public class PeriodsTable {
    @PrimaryKey(autoGenerate = true)
    public long period_id;

    public String label;
}
