package fr.jadeveloppement.budgetsjad.functions;

import static java.lang.Long.parseLong;
import static java.util.Objects.isNull;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.components.popups.PopupAccountsContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContainer;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContentLogin;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContentSynchronize;
import fr.jadeveloppement.budgetsjad.components.popups.PopupElementContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupPeriodsContent;
import fr.jadeveloppement.budgetsjad.functions.interfaces.BudgetRequestsInterface;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModel;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;
import fr.jadeveloppement.budgetsjad.ui.home.HomeFragment;

public class PopupHelper {

    private final String TAG = "JADBudget";

    private final Context context;
    private PeriodsTable periodSelected;
    private Functions functions;
    private BudgetViewModel budgetViewModel;

    private PopupContainer popupLogin = null;
    private LinearLayout popupLoadingScreen = null;

    public PopupHelper(@NonNull Context c){
        this.context = c.getApplicationContext();
    }

    public PopupHelper(@NonNull Context c, @Nullable BudgetViewModel bModel){
        this.context = c.getApplicationContext();
        this.budgetViewModel = bModel;
        this.functions = new Functions(context);
    }

    public void popupAddElement(Enums.TransactionType type){
        periodSelected = functions.getPeriodById(parseLong(functions.getSettingByLabel(Variables.settingPeriod).value));
        SettingsTable settingsAccount = functions.getSettingByLabel(Variables.settingAccount);
        PopupContainer popupContainer = new PopupContainer(context, MainActivity.getViewRoot());
        PopupElementContent popupElementContent = new PopupElementContent(context, MainActivity.getViewRoot(), null);
        popupElementContent.getPopupContentElementPeriodTv().setText(Functions.convertStdDateToLocale(periodSelected.label));
        popupElementContent.getPopupContentElementBtnDelete().setVisibility(View.GONE);
        popupContainer.addContent(popupElementContent.getLayout());

        String titlePopup = "Ajouter un élément";
        if (type == Enums.TransactionType.EXPENSE) titlePopup = "Ajouter une dépense";
        else if (type == Enums.TransactionType.INCOME) titlePopup = "Ajouter un revenu";
        else if (type == Enums.TransactionType.INVOICE) titlePopup = "Ajouter un prélèvement";
        else if (type == Enums.TransactionType.MODELINCOME) titlePopup = "Nouveau modèle de revenu";
        else if (type == Enums.TransactionType.MODELINVOICE) titlePopup = "Nouveau modèle de prélèvement";

        popupElementContent.getPopupContentElementTitle().setText(titlePopup);

        if (type != Enums.TransactionType.INVOICE)
            popupElementContent.getPopupContentElementIsPaid().setVisibility(View.GONE);

        popupElementContent.getPopupContentElementBtnClose().setOnClickListener(v1 -> {
            popupContainer.closePopup();
        });

        popupElementContent.getPopupContentElementBtnSave().setOnClickListener(v -> {
            String label = popupElementContent.getPopupContentElementLabel().getText().toString();
            String amount = popupElementContent.getPopupContentElementAmount().getText().toString();
            if (label.isBlank() || amount.isBlank()) functions.makeToast("Veuillez renseigner tous les champs");
            else {
                Transaction transaction = new Transaction(
                        label,
                        amount,
                        Functions.convertLocaleDateToStd(popupElementContent.getPopupContentElementPeriodTv().getText().toString()),
                        settingsAccount.value,
                        type == Enums.TransactionType.INVOICE ? (popupElementContent.getPopupContentElementIsPaid().isChecked() ? "1" : "0") : "0",
                        type
                );
                budgetViewModel.addTransaction(transaction);
                functions.makeToast("Elément rajouté avec succès");
                popupContainer.closePopup();
            }
        });
    }

    public void popupManageAccounts(){
        PopupContainer popupContainer = new PopupContainer(context, MainActivity.getViewRoot());
        PopupAccountsContent popupAccountContent = new PopupAccountsContent(context, MainActivity.getViewRoot());

        popupContainer.addContent(popupAccountContent.getLayout());

        popupAccountContent.getBtnClose().setOnClickListener(v1 -> popupContainer.closePopup());
    }

    public void popupManagePeriods(){
        PopupContainer popupContainer = new PopupContainer(context, MainActivity.getViewRoot());
        PopupPeriodsContent popupPeriodContent = new PopupPeriodsContent(context, MainActivity.getViewRoot());

        popupContainer.addContent(popupPeriodContent.getLayout());

        popupPeriodContent.getBtnClose().setOnClickListener(v1 -> popupContainer.closePopup());
    }

    public void popupImportDatas(BudgetRequestsInterface callback){
        String login = functions.getSettingByLabel(Variables.settingUsername).value;
        String password = functions.getSettingByLabel(Variables.settingPassword).value;

        SettingsTable settingTableToken = functions.getSettingByLabel(Variables.settingsToken);
        if (isNull(settingTableToken) || settingTableToken.value.isBlank()){
            callback.tokenNonOk();
            return;
        }

        PopupContainer popupContainer = new PopupContainer(context, MainActivity.getViewRoot());
        PopupContentSynchronize popupContentSynchronize = new PopupContentSynchronize(context);
        popupContentSynchronize.setTitle("Récupérer les données");
        popupContentSynchronize.setBtnSaveLabel("Récupérer");
        popupContainer.addContent(popupContentSynchronize.getLayout());

        popupContentSynchronize.getLoadingScreen().setOnClickListener(v1 -> {});

        popupContentSynchronize.getBtnClose().setOnClickListener(v1 -> popupContainer.closePopup());
        popupContentSynchronize.getBtnSave().setOnClickListener(v1 -> {
            popupLoadingScreen = popupContentSynchronize.getLoadingScreen();
            toggleLoadingScreen(true);

            List<Enums.DataToRequest> datasToImport = new ArrayList<>();
            if (popupContentSynchronize.getPopupContentSynchronizeInvoiceCb().isChecked()) datasToImport.add(Enums.DataToRequest.INVOICE);
            if (popupContentSynchronize.getPopupContentSynchronizeIncomeCb().isChecked()) datasToImport.add(Enums.DataToRequest.INCOME);
            if (popupContentSynchronize.getPopupContentSynchronizeExpenseCb().isChecked()) datasToImport.add(Enums.DataToRequest.EXPENSE);
            if (popupContentSynchronize.getPopupContentSynchronizeModelInvoiceCb().isChecked()) datasToImport.add(Enums.DataToRequest.MODELINVOICE);
            if (popupContentSynchronize.getPopupContentSynchronizeModelIncomeCb().isChecked()) datasToImport.add(Enums.DataToRequest.MODELINCOME);

            if (datasToImport.isEmpty()) {
                functions.makeToast("Veuillez cocher au moins une case.");
                return;
            }

            Log.d(TAG, "PopupHelper > popupImportDatas : login " + login + " password : " + password + " token : " + settingTableToken.value);
            BudgetRequests budgetRequests = new BudgetRequests(context, login, password, callback);
            budgetRequests.makeImportDatas(settingTableToken.value, datasToImport);

        });
    }

    public void popupExportDatas(BudgetRequestsInterface callback) {
        SettingsTable settingTableToken = functions.getSettingByLabel(Variables.settingsToken);
        if (isNull(settingTableToken)){
            callback.tokenNonOk();
            return;
        }
        String login = functions.getSettingByLabel(Variables.settingUsername).value;
        String password = functions.getSettingByLabel(Variables.settingPassword).value;

        PopupContainer popupContainer = new PopupContainer(context, MainActivity.getViewRoot());
        PopupContentSynchronize popupContentSynchronize = new PopupContentSynchronize(context);
        popupContentSynchronize.setTitle("Récupérer les données");
        popupContentSynchronize.setBtnSaveLabel("Récupérer");
        popupContainer.addContent(popupContentSynchronize.getLayout());

        popupContentSynchronize.getBtnClose().setOnClickListener(v1 -> popupContainer.closePopup());
        popupContentSynchronize.getBtnSave().setOnClickListener(v1 -> {
            popupLoadingScreen = popupContentSynchronize.getLoadingScreen();
            List<Enums.DataToRequest> datasToSend = new ArrayList<>();
            if (popupContentSynchronize.getPopupContentSynchronizeInvoiceCb().isChecked()) datasToSend.add(Enums.DataToRequest.INVOICE);
            if (popupContentSynchronize.getPopupContentSynchronizeIncomeCb().isChecked()) datasToSend.add(Enums.DataToRequest.INCOME);
            if (popupContentSynchronize.getPopupContentSynchronizeExpenseCb().isChecked()) datasToSend.add(Enums.DataToRequest.EXPENSE);
            if (popupContentSynchronize.getPopupContentSynchronizeModelInvoiceCb().isChecked()) datasToSend.add(Enums.DataToRequest.MODELINVOICE);
            if (popupContentSynchronize.getPopupContentSynchronizeModelIncomeCb().isChecked()) datasToSend.add(Enums.DataToRequest.MODELINCOME);

            if (datasToSend.isEmpty()) {
                functions.makeToast("Veuillez cocher au moins une case SVP.");
                return;
            }

            StringBuilder datas = new StringBuilder();

            if (datasToSend.contains(Enums.DataToRequest.INVOICE)) datas.append("<n>").append(functions.convertListToDatas(budgetViewModel.getInvoices().getValue()));
            if (datasToSend.contains(Enums.DataToRequest.INCOME)) datas.append("<n>").append(functions.convertListToDatas(budgetViewModel.getIncomes().getValue()));
            if (datasToSend.contains(Enums.DataToRequest.EXPENSE)) datas.append("<n>").append(functions.convertListToDatas(budgetViewModel.getExpenses().getValue()));
            if (datasToSend.contains(Enums.DataToRequest.MODELINCOME)) datas.append("<n>").append(functions.convertListToDatas(budgetViewModel.getModelIncome().getValue()));
            if (datasToSend.contains(Enums.DataToRequest.MODELINVOICE)) datas.append("<n>").append(functions.convertListToDatas(budgetViewModel.getModelInvoice().getValue()));

            BudgetRequests budgetRequests = new BudgetRequests(context, login, password, callback);
            budgetRequests.makeExportDatas(settingTableToken.value, datas.toString());
        });
    }

    public void makeLoginRequest(BudgetRequestsInterface callback) {
        PopupContainer popupContainer = new PopupContainer(context, MainActivity.getViewRoot());
        PopupContentLogin popupContentLogin = new PopupContentLogin(context);
        popupContainer.addContent(popupContentLogin.getLayout());

        popupLogin = popupContainer;

        popupContentLogin.getBtnSave().setOnClickListener(v1 -> {
            popupLoadingScreen = popupContentLogin.getPopupContentLoginLoadingScreen();
            toggleLoadingScreen(true);

            String login = popupContentLogin.getLogin();
            String password = popupContentLogin.getPassword();

            if (login.isBlank() || password.isBlank()) functions.makeToast("Veuillez renseigner vos identifiants.");
            else {
                BudgetRequests budgetRequests = new BudgetRequests(context, login, password, callback);
                budgetRequests.handleLogin();
            }
        });

        popupContentLogin.getBtnClose().setOnClickListener(v1 -> {
            popupContainer.closePopup();
        });
    }

    public void closeLoginPopup(){
        if (!isNull(popupLogin)){
            popupLogin.closePopup();
            popupLogin = null;
        }
    }

    public void toggleLoadingScreen(boolean isVisible){
        if (!isNull(popupLoadingScreen)){
            popupLoadingScreen.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            if (!isVisible) popupLoadingScreen = null;
        }
    }
}
