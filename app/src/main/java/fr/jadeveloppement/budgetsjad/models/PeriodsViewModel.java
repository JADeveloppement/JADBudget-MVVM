package fr.jadeveloppement.budgetsjad.models;

import static java.lang.Long.parseLong;
import static java.util.Objects.isNull;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;

import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.models.classes.PeriodsData;
import fr.jadeveloppement.budgetsjad.sqlite.SQLiteFunctions;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class PeriodsViewModel extends AndroidViewModel {
    private final String TAG = "JADBudget > PeriodsViewModel";

    private final PeriodsData periodsData;
    private final SQLiteFunctions sqLiteFunctions;

    private final MutableLiveData<List<PeriodsTable>> listOfPeriodsTable;
    private final MutableLiveData<SettingsTable> settingsPeriod;
    private final MutableLiveData<PeriodsTable> periodSelected;

    public PeriodsViewModel(Application application){
        super(application);

        this.periodsData = new PeriodsData(application);
        this.sqLiteFunctions = new SQLiteFunctions(application);
        this.listOfPeriodsTable = new MutableLiveData<>();
        this.settingsPeriod = new MutableLiveData<>();
        this.periodSelected = new MutableLiveData<>();

        updateListPeriods();
    }

    public LiveData<PeriodsTable> getPeriodSelected() {
        return periodSelected;
    }

    public LiveData<List<PeriodsTable>> getListOfPeriodsTable() {
        return listOfPeriodsTable;
    }
    public LiveData<SettingsTable> getSettingsPeriod() {
        return settingsPeriod;
    }
    public void insertPeriod(PeriodsTable newPeriod) {
        periodsData.insertPeriodsTable(newPeriod);
        updateSettingsPeriod(newPeriod.label);
        updateListPeriods();
    }

    public void updatePeriod(String newPeriod) {
        updateSettingsPeriod(newPeriod);
    }

    public void deletePeriod(PeriodsTable periodsTable) {
        periodsData.deletePeriodsTable(periodsTable);
        updateListPeriods();
    }

    public void updateSettingsPeriod(String newPeriod) {
        if (!sqLiteFunctions.getPeriodById(parseLong(sqLiteFunctions.getSettingByLabel(Variables.settingPeriod).value)).label.equalsIgnoreCase(newPeriod)) {
            SettingsTable settingsPeriod = sqLiteFunctions.getSettingByLabel(Variables.settingPeriod);
            PeriodsTable periodsTable = sqLiteFunctions.getPeriodByLabel(newPeriod);
            settingsPeriod.value = String.valueOf(periodsTable.period_id);
            sqLiteFunctions.updateSettings(settingsPeriod);
            periodSelected.postValue(periodsTable);
        }
    }

    public void updateListPeriods(){
        Log.d(TAG, "updateListPeriods: list updated");
        List<PeriodsTable> updatedListOfPeriods = sqLiteFunctions.getAllPeriods();
        if (isNull(updatedListOfPeriods)) updatedListOfPeriods = Collections.emptyList();

        listOfPeriodsTable.postValue(updatedListOfPeriods);
    }

    public void periodActiveChanged(){
        updateListPeriods();
    }
}
