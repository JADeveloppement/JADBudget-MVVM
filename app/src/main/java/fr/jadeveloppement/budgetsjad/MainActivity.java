package fr.jadeveloppement.budgetsjad;

import static java.util.Objects.isNull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import java.util.List;

import fr.jadeveloppement.budgetsjad.databinding.ActivityMainBinding;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "BudgetJAD";

    private static ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Functions functions = new Functions(getApplicationContext());

        if (functions.getAllAccounts().isEmpty()){
            Log.d(TAG, "MainActivity > onCreate : Create first Account");
            functions.insertAccount(new AccountsTable("Compte Principal", "0"));
        }

        List<PeriodsTable> listOfPeriod = functions.getAllPeriods();
        if (listOfPeriod.isEmpty()) {
            Log.d(TAG, "MainActivity > onCreate: Create first period");
            functions.insertPeriod(new PeriodsTable(Functions.getTodayDate()));
        }

        if (isNull(functions.getSettingByLabel(Variables.settingPeriod))){
            Log.d(TAG, "MainActivity > onCreate: create new settingPeriod");
            functions.insertSettings(new SettingsTable(Variables.settingPeriod, String.valueOf(functions.getAllPeriods().get(0).period_id)));
        }

        if (isNull(functions.getSettingByLabel(Variables.settingAccount))){
            Log.d(TAG, "MainActivity > onCreate: Create setting account data");
            functions.insertSettings(new SettingsTable(Variables.settingAccount, String.valueOf(functions.getAllAccounts().get(0).account_id)));
        }

        if (isNull(functions.getSettingByLabel(Variables.settingUsername))){
            Log.d(TAG, "MainActivity > onCreate: Create setting username data");
            functions.insertSettings(new SettingsTable(Variables.settingUsername, ""));
        }

        if (isNull(functions.getSettingByLabel(Variables.settingPassword))){
            Log.d(TAG, "MainActivity > onCreate: Create setting password data");
            functions.insertSettings(new SettingsTable(Variables.settingPassword, ""));
        }

        if (isNull(functions.getSettingByLabel(Variables.settingsToken))){
            Log.d(TAG, "MainActivity > onCreate: Create setting token data");
            functions.insertSettings(new SettingsTable(Variables.settingsToken, ""));
        }

        if (isNull(functions.getSettingByLabel(Variables.settingCategory))){
            Log.d(TAG, "MainActivity > onCreate: Create setting token data");
            functions.insertSettings(new SettingsTable(Variables.settingCategory, "0"));
        }

        functions.checkSettingPeriod();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public static View getViewRoot(){
        return binding.getRoot();
    }


}