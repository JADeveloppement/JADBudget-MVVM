package fr.jadeveloppement.budgetsjad.sqlite.tables;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "expenses",
        foreignKeys = @ForeignKey(
                entity = AccountsTable.class,
                parentColumns = "account_id",
                childColumns = "account_id",
                onDelete = ForeignKey.SET_NULL,
                onUpdate = ForeignKey.CASCADE
        ))
public class ExpensesTable {
    @PrimaryKey(autoGenerate = true)
    public long expense_id;

    public Long account_id;
    public String label;
    public String date;
    public String amount;

    public Long category_id;
}
