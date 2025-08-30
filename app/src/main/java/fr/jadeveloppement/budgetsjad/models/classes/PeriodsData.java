package fr.jadeveloppement.budgetsjad.models.classes;

import android.content.Context;

import androidx.annotation.NonNull;

import fr.jadeveloppement.budgetsjad.sqlite.SQLiteFunctions;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;

public class PeriodsData {

    private final Context context;
    private final SQLiteFunctions sqLiteFunctions;

    public PeriodsData(@NonNull Context c){
        this.context = c;
        this.sqLiteFunctions = new SQLiteFunctions(context);
    }

    public void insertPeriodsTable(PeriodsTable p){
        sqLiteFunctions.insertPeriod(p);
    }

    public void deletePeriodsTable(PeriodsTable p){
        sqLiteFunctions.deletePeriod(p);
    }
}
