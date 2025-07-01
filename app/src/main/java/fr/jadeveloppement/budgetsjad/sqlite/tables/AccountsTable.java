package fr.jadeveloppement.budgetsjad.sqlite.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class AccountsTable {
    @PrimaryKey(autoGenerate = true)
    public long account_id;

    public String label;
    public String amount;

    public AccountsTable(){

    }

    public AccountsTable(String accountName, String accountAmount) {
        this.label = accountName;
        this.amount = accountAmount;
    }
}