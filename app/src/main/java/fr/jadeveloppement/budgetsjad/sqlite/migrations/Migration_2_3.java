package fr.jadeveloppement.budgetsjad.sqlite.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration_2_3 extends Migration {

    public Migration_2_3(){
        super(2, 3);
    }

    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (" +
                "`transaction_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "`category_id` INTEGER," +
                "`account_id` INTEGER," +
                "`period_id` INTEGER," +
                "`label` TEXT," +
                "`amount` REAL," +
                "`paid` TEXT," +
                "`type` TEXT," +
                "FOREIGN KEY(`account_id`) REFERENCES `accounts`(`account_id`) ON UPDATE CASCADE ON DELETE SET NULL," +
                "FOREIGN KEY(`period_id`) REFERENCES `periods`(`period_id`) ON UPDATE CASCADE ON DELETE SET NULL," +
                "FOREIGN KEY(`category_id`) REFERENCES `category`(`category_id`) ON UPDATE CASCADE ON DELETE SET NULL)");

        database.execSQL("INSERT INTO `transactions` (label, amount, paid, type, account_id, period_id, category_id) " +
                                  "SELECT i.label, i.amount, i.paid, 'INVOICE', i.account_id, p.period_id, i.category_id FROM invoices AS i LEFT JOIN periods AS p ON p.label = i.date");

        database.execSQL("INSERT INTO `transactions` (label, amount, paid, type, account_id, period_id, category_id) " +
                "SELECT e.label, e.amount, 0, 'EXPENSE', e.account_id, p.period_id, e.category_id FROM expenses AS e LEFT JOIN periods AS p ON p.label = e.date");

        database.execSQL("INSERT INTO `transactions` (label, amount, paid, type, account_id, period_id, category_id) " +
                "SELECT i.label, i.amount, 0, 'INCOME', i.account_id, p.period_id, i.category_id FROM incomes AS i LEFT JOIN periods AS p ON p.label = i.date");

        database.execSQL("INSERT INTO `transactions` (label, amount, paid, type, account_id, period_id, category_id) " +
                "SELECT label, amount, 0, 'MODELINVOICE', NULL, NULL, NULL FROM modeleinvoices");

        database.execSQL("INSERT INTO `transactions` (label, amount, paid, type, account_id, period_id, category_id) " +
                "SELECT label, amount, 0, 'MODELINCOME', NULL, NULL, NULL FROM modeleincomes");
    }
}
