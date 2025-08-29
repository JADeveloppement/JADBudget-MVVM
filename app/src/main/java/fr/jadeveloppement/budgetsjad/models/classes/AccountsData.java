package fr.jadeveloppement.budgetsjad.models.classes;

import android.content.Context;

import androidx.annotation.NonNull;

import fr.jadeveloppement.budgetsjad.sqlite.SQLiteFunctions;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;

public class AccountsData {
    private final Context context;
    private final SQLiteFunctions sqLiteFunctions;

    public AccountsData(@NonNull Context c){
        this.context = c;
        this.sqLiteFunctions = new SQLiteFunctions(context);
    }

    public void insertAccountsTable(AccountsTable a){
        sqLiteFunctions.insertAccount(a);
    }

    public void updateAccountsTable(AccountsTable a){
        sqLiteFunctions.updateAccount(a);
    }

    public void deleteAccountsTable(AccountsTable a){
        sqLiteFunctions.updateAccount(a);
    }
}
