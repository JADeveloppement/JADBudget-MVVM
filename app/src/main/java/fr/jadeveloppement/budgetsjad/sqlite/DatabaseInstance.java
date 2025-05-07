package fr.jadeveloppement.budgetsjad.sqlite;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import fr.jadeveloppement.budgetsjad.sqlite.dao.AccountsDAO;
import fr.jadeveloppement.budgetsjad.sqlite.dao.ExpensesDAO;
import fr.jadeveloppement.budgetsjad.sqlite.dao.IncomesDAO;
import fr.jadeveloppement.budgetsjad.sqlite.dao.InvoicesDAO;
import fr.jadeveloppement.budgetsjad.sqlite.dao.ModeleIncomesDAO;
import fr.jadeveloppement.budgetsjad.sqlite.dao.ModeleInvoicesDAO;
import fr.jadeveloppement.budgetsjad.sqlite.dao.PeriodsDAO;
import fr.jadeveloppement.budgetsjad.sqlite.dao.SettingsDAO;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ExpensesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.IncomesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.InvoicesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleIncomes;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleInvoices;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

@Database(
        entities = {
                ModeleIncomes.class,
                ModeleInvoices.class,
                IncomesTable.class,
                InvoicesTable.class,
                ExpensesTable.class,
                AccountsTable.class,
                SettingsTable.class,
                PeriodsTable.class
        },
        version = 1
)
public abstract class DatabaseInstance extends RoomDatabase {
    private static volatile DatabaseInstance INSTANCE;

    public abstract ModeleIncomesDAO modeleIncomesDAO();
    public abstract ModeleInvoicesDAO modeleInvoicesDAO();
    public abstract AccountsDAO accountsDAO();
    public abstract IncomesDAO incomesDAO();
    public abstract InvoicesDAO invoicesDAO();
    public abstract ExpensesDAO expensesDAO();
    public abstract SettingsDAO settingsDAO();
    public abstract PeriodsDAO periodsDAO();

    public static DatabaseInstance getInstance(Context c){
        if (INSTANCE == null){
            synchronized (DatabaseInstance.class) {
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(
                                    c.getApplicationContext(),
                                    DatabaseInstance.class,
                                    "BudgetsJAD.db"
                            )
                            .build();
                }
            }
        }

        return INSTANCE;
    }
}