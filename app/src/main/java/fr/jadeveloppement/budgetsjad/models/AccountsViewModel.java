package fr.jadeveloppement.budgetsjad.models;

import static java.util.Objects.isNull;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;

import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.models.classes.AccountsData;
import fr.jadeveloppement.budgetsjad.sqlite.SQLiteFunctions;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class AccountsViewModel extends AndroidViewModel {
    private final String TAG = "JADBudget";

    private final AccountsData accountsData;
    private final SQLiteFunctions sqLiteFunctions;

    private final MutableLiveData<List<AccountsTable>> listOfAccountsTable;
    private final MutableLiveData<SettingsTable> settingsAccount;


    public AccountsViewModel(Application application){
        super(application);

        this.accountsData = new AccountsData(application);
        this.sqLiteFunctions = new SQLiteFunctions(application);
        this.listOfAccountsTable = new MutableLiveData<>();
        this.settingsAccount = new MutableLiveData<>();
    }

    public LiveData<List<AccountsTable>> getListOfAccountsTable() {
        return listOfAccountsTable;
    }
    public LiveData<SettingsTable> getSettingsAccount() {
        return settingsAccount;
    }
    public void setSettingsAccount(){
        settingsAccount.postValue(sqLiteFunctions.getSettingByLabel(Variables.settingAccount));
    }
    public void insertAccount(AccountsTable newAccount) {
        accountsData.insertAccountsTable(newAccount);
        updateListAccounts();
    }

    public void updateAccount(AccountsTable accountsTable) {
        accountsData.updateAccountsTable(accountsTable);
        updateListAccounts();
    }

    public void deleteAccount(AccountsTable accountsTable) {
        accountsData.deleteAccountsTable(accountsTable);
        updateListAccounts();
    }

    public void updateSettingsAccount(String newAccount) {
        if (!sqLiteFunctions.getSettingByLabel(Variables.settingAccount).value.equalsIgnoreCase(newAccount)) {
            SettingsTable newSettingsAccount = sqLiteFunctions.getSettingByLabel(Variables.settingAccount);
            newSettingsAccount.value = newAccount;
            sqLiteFunctions.updateSettings(newSettingsAccount);
            settingsAccount.postValue(newSettingsAccount);
            updateListAccounts();
        }
    }

    public void updateListAccounts(){
        List<AccountsTable> updatedListOfAccounts = sqLiteFunctions.getAllAccounts();
        if (isNull(updatedListOfAccounts)) updatedListOfAccounts = Collections.emptyList();

        listOfAccountsTable.postValue(updatedListOfAccounts);
    }

    public void accountActiveChanged(){
        updateListAccounts();
    }
}
