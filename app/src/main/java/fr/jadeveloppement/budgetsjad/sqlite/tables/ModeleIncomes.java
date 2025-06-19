package fr.jadeveloppement.budgetsjad.sqlite.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "modeleincomes")
public class ModeleIncomes {
    @PrimaryKey(autoGenerate = true)
    public long modeleincome_id;

    public String label;
    public String date;
    public String amount;
    public String paid;

    public Long category_id;
}
