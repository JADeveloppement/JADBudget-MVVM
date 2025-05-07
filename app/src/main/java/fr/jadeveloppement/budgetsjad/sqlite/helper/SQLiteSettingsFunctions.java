package fr.jadeveloppement.budgetsjad.sqlite.helper;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.sqlite.DatabaseInstance;
import fr.jadeveloppement.budgetsjad.sqlite.dao.SettingsDAO;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class SQLiteSettingsFunctions {
    private final DatabaseInstance dbFunctions;
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // Shared thread pool
    private final Context context;

    private final SettingsDAO settingsDAO;

    public SQLiteSettingsFunctions(Context c){
        this.context = c.getApplicationContext();
        this.dbFunctions = DatabaseInstance.getInstance(context);
        this.settingsDAO = dbFunctions.settingsDAO();
    }

    public SettingsTable getSettingByLabel(String settingPeriod) {
        try {
            return executorService.submit(() -> settingsDAO.getSettingByLabel(settingPeriod)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteSettingsFunctions > getSettingByLabel", e);
            return null;
        }
    }

    public Long insertSetting(SettingsTable settingsTable) {
        try {
            return executorService.submit(() -> settingsDAO.insertSetting(settingsTable)).get();
        } catch(Exception e){
            Functions.handleExceptions("SQLiteSettingsFunctions > insertSetting", e);
            return null;
        }
    }

    public void updateSettings(SettingsTable settingsPeriod) {
        try {
            executorService.submit(() -> settingsDAO.updateSetting(settingsPeriod)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteSettingsFunctions > updateSettings", e);
        }
    }

    public SettingsTable getSettingById(long id) {
        try {
            return executorService.submit(() -> settingsDAO.getSettingById(id)).get();
        } catch (Exception e){
            Functions.handleExceptions("SQLiteSettingsFunctions > getSettingById", e);
            return null;
        }
    }
}
