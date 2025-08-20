package fr.jadeveloppement.budgetsjad.sqlite.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration_1_2 extends Migration {

    public Migration_1_2(){
        super(1, 2);
    }

    public void migrate(@NonNull SupportSQLiteDatabase database){
        database.execSQL("CREATE TABLE IF NOT EXISTS `category` (" +
                "`category_id` INTEGER PRIMARY KEY NOT NULL," +
                "`label` TEXT NOT NULL)");

        database.execSQL("ALTER TABLE `invoices` ADD COLUMN `category_id` INTEGER");
        database.execSQL("ALTER TABLE `incomes` ADD COLUMN `category_id` INTEGER");
        database.execSQL("ALTER TABLE `expenses` ADD COLUMN `category_id` INTEGER");
        database.execSQL("ALTER TABLE `modeleinvoices` ADD COLUMN `category_id` INTEGER");
        database.execSQL("ALTER TABLE `modeleincomes` ADD COLUMN `category_id` INTEGER");
    }
}
