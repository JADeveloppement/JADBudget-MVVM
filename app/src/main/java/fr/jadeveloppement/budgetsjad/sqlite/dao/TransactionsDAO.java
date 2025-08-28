package fr.jadeveloppement.budgetsjad.sqlite.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.jadeveloppement.budgetsjad.sqlite.tables.TransactionsTable;

@Dao
public interface TransactionsDAO {
    @Insert
    long insertTransaction(TransactionsTable t);

    @Update
    void updateTransaction(TransactionsTable t);

    @Delete
    void deleteTransactionsTables(List<TransactionsTable> listOfTransactionToDelete);

    @Query("SELECT * FROM transactions WHERE type = :type AND period_id = (SELECT period_id FROM periods WHERE period_id = (SELECT value FROM settings WHERE label = 'period_id')) AND account_id = ( SELECT value FROM settings WHERE label = 'account_id' )")
    List<TransactionsTable> getTransactionsByType(String type);

    @Query("SELECT * FROM transactions")
    List<TransactionsTable> getAllTransactions();
}
