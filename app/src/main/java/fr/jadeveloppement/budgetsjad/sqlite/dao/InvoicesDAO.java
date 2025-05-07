package fr.jadeveloppement.budgetsjad.sqlite.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.jadeveloppement.budgetsjad.sqlite.tables.InvoicesTable;

@Dao
public interface InvoicesDAO {
    @Query("SELECT * FROM invoices WHERE date = (SELECT label FROM periods WHERE period_id = ( SELECT value FROM settings WHERE label = 'period_id' ) )" +
            "AND account_id = ( SELECT value FROM settings WHERE label = 'account_id' ) ORDER BY IFNULL(paid, 0) ASC")
    List<InvoicesTable> getAllInvoices();

    @Query("SELECT * FROM invoices WHERE date = :date AND account_id = ( SELECT value FROM settings WHERE label = 'account_id' ) ORDER BY IFNULL(paid, 0) ASC")
    List<InvoicesTable> getInvoicesFromPeriod(String date);

    @Query("SELECT * FROM invoices WHERE invoice_id = :id")
    InvoicesTable getInvoiceById(long id);

    @Insert
    long insertInvoice(InvoicesTable invoicesTable);

    @Update
    void updateInvoice(InvoicesTable invoicesTable);

    @Delete
    void deleteInvoice(InvoicesTable invoicesTable);
}
