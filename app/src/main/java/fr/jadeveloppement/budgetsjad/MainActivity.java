package fr.jadeveloppement.budgetsjad;

import static java.util.Objects.isNull;

import android.icu.util.ValueIterator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.AppBarLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.jadeveloppement.budgetsjad.databinding.ActivityMainBinding;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleInvoices;
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

        List<AccountsTable> listOfAccounts = functions.getAllAccounts();
        if (listOfAccounts.isEmpty()){
            Log.d(TAG, "MainActivity > onCreate : Create first Account");
            AccountsTable accountsTable = new AccountsTable();
            accountsTable.label = "Compte principal";
            accountsTable.amount = "0";
            functions.insertAccount(accountsTable);
        }

        List<PeriodsTable> listOfPeriod = functions.getAllPeriods();
        if (listOfPeriod.isEmpty()) {
            Log.d(TAG, "MainActivity > onCreate: Create new period");
            PeriodsTable periodsTable = new PeriodsTable();
            periodsTable.label = Functions.getTodayDate();
            functions.insertPeriod(periodsTable);
        }

        SettingsTable settingsPeriod = functions.getSettingByLabel(Variables.settingPeriod);
        if (isNull(settingsPeriod)){
            Log.d(TAG, "MainActivity > onCreate: Create setting Period data");
            SettingsTable settingPeriod = new SettingsTable();
            settingPeriod.label = Variables.settingPeriod;
            settingPeriod.value = String.valueOf(functions.getAllPeriods().get(0).period_id);
            functions.insertSettings(settingPeriod);
        }

        SettingsTable settingsAccount = functions.getSettingByLabel(Variables.settingAccount);
        Log.d(TAG, "onCreate: nb accounts : " + functions.getAllAccounts().get(0).account_id);
        if (isNull(settingsAccount)){
            Log.d(TAG, "MainActivity > onCreate: Create setting account data");
            SettingsTable settingAccount = new SettingsTable();
            settingAccount.label = Variables.settingAccount;
            settingAccount.value = String.valueOf(functions.getAllAccounts().get(0).account_id);
            functions.insertSettings(settingAccount);
        }

        SettingsTable settingsUserName = functions.getSettingByLabel(Variables.settingUsername);
        if (isNull(settingsUserName)){
            Log.d(TAG, "MainActivity > onCreate: Create setting username data");
            settingsUserName = new SettingsTable();
            settingsUserName.label = Variables.settingUsername;
            settingsUserName.value = "";
            functions.insertSettings(settingsUserName);
        }

        SettingsTable settingsUserPassword = functions.getSettingByLabel(Variables.settingPassword);
        if (isNull(settingsUserPassword)){
            Log.d(TAG, "MainActivity > onCreate: Create setting password data");
            settingsUserPassword = new SettingsTable();
            settingsUserPassword.label = Variables.settingPassword;
            settingsUserPassword.value = "";
            functions.insertSettings(settingsUserPassword);
        }

        SettingsTable settingsToken = functions.getSettingByLabel(Variables.settingsToken);
        if (isNull(settingsToken)){
            Log.d(TAG, "MainActivity > onCreate: Create setting token data");
            settingsToken = new SettingsTable();
            settingsToken.label = Variables.settingsToken;
            settingsToken.value = "";
            functions.insertSettings(settingsToken);
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