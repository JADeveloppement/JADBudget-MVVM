package fr.jadeveloppement.budgetsjad.sqlite.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleInvoices;

@Dao
public interface ModeleInvoicesDAO {
    @Query("SELECT * FROM modeleinvoices")
    List<ModeleInvoices> getAllModeleInvoices();

    @Query("SELECT * FROM modeleinvoices WHERE modeleinvoice_id = :id")
    ModeleInvoices getModeleInvoiceById(long id);

    @Insert
    long insertModeleInvoice(ModeleInvoices modeleInvoices);

    @Update
    void updateModeleInvoice(ModeleInvoices modeleInvoices);

    @Delete
    void deleteModeleInvoice(ModeleInvoices modeleInvoices);
}
