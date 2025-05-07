package fr.jadeveloppement.budgetsjad.sqlite.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;

@Dao
public interface AccountsDAO {
    @Query("SELECT * FROM accounts")
    List<AccountsTable> getAllAccounts();

    @Query("SELECT * FROM accounts WHERE account_id = :id")
    AccountsTable getAccountById(long id);

    @Insert
    long insertAccount(AccountsTable accountsTable);

    @Update
    void updateAccount(AccountsTable accountsTable);

    @Delete
    void deleteAccount(AccountsTable accountsTable);
}
