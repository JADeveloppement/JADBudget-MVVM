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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.popups.PopupAccountContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupAccountsContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContainer;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContentLogin;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContentSynchronize;
import fr.jadeveloppement.budgetsjad.components.popups.PopupDisplayTileContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupElementContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupModelContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupPeriodContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupPeriodsContent;
import fr.jadeveloppement.budgetsjad.functions.interfaces.BudgetRequestsInterface;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModel;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;
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
        this.budgetViewModel = null;
        this.functions = new Functions(context);
    }

    public PopupHelper(@NonNull Context c, @Nullable BudgetViewModel bModel){
        this.context = c.getApplicationContext();
        this.budgetViewModel = bModel;
        this.functions = new Functions(context);
    }

    public void popupAddElement(Enums.TransactionType type, boolean... isEx){
        boolean isExternal = isEx.length > 0 && isEx[0];

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

                if (!isExternal){
                    budgetViewModel.addTransaction(transaction);
                    functions.makeToast("Elément rajouté avec succès");
                } else {

                }
                popupContainer.closePopup();
            }
        });
    }

    public void popupAddExternal(Enums.TransactionType type){

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

        popupContentSynchronize.getPopupContentSynchronizePreviewInvoice().setOnClickListener(v -> {
            popupLoadingScreen = popupContentSynchronize.getLoadingScreen();
            toggleLoadingScreen(true);
            callback.previewDatas(Enums.DataToRequest.INVOICE);
        });

        popupContentSynchronize.getPopupContentSynchronizePreviewIncome().setOnClickListener(v -> {
            popupLoadingScreen = popupContentSynchronize.getLoadingScreen();
            toggleLoadingScreen(true);
            callback.previewDatas(Enums.DataToRequest.INCOME);
        });

        popupContentSynchronize.getPopupContentSynchronizePreviewExpense().setOnClickListener(v -> {
            popupLoadingScreen = popupContentSynchronize.getLoadingScreen();
            toggleLoadingScreen(true);
            callback.previewDatas(Enums.DataToRequest.EXPENSE);
        });

        popupContentSynchronize.getPopupContentSynchronizePreviewModelInvoice().setOnClickListener(v -> {
            popupLoadingScreen = popupContentSynchronize.getLoadingScreen();
            toggleLoadingScreen(true);
            callback.previewDatas(Enums.DataToRequest.MODELINVOICE);
        });

        popupContentSynchronize.getPopupContentSynchronizePreviewModelIncome().setOnClickListener(v -> {
            popupLoadingScreen = popupContentSynchronize.getLoadingScreen();
            toggleLoadingScreen(true);
            callback.previewDatas(Enums.DataToRequest.MODELINCOME);
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
        popupContentSynchronize.setTitle("Envoyer les données");
        popupContentSynchronize.setBtnSaveLabel("Envoyer");
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

        popupContentSynchronize.getPopupContentSynchronizePreviewInvoice().setOnClickListener(v -> {
            List<Transaction> listOfInvoices = functions.getAllInvoicesTransaction();
            if (listOfInvoices.isEmpty()) Functions.makeSnakebar("Aucune données à afficher");
            else displayListOfTransaction(new MutableLiveData<>(listOfInvoices), Enums.TransactionType.INVOICE);
        });

        popupContentSynchronize.getPopupContentSynchronizePreviewIncome().setOnClickListener(v -> {
            List<Transaction> listOfIncomes = functions.getAllIncomesTransaction();
            if (listOfIncomes.isEmpty()) Functions.makeSnakebar("Aucune données à afficher");
            else displayListOfTransaction(new MutableLiveData<>(listOfIncomes), Enums.TransactionType.INCOME);
        });

        popupContentSynchronize.getPopupContentSynchronizePreviewExpense().setOnClickListener(v -> {
            List<Transaction> listOfExpenses = functions.getAllExpensesTransaction();
            if (listOfExpenses.isEmpty()) Functions.makeSnakebar("Aucune données à afficher");
            else displayListOfTransaction(new MutableLiveData<>(listOfExpenses), Enums.TransactionType.EXPENSE);
        });

        popupContentSynchronize.getPopupContentSynchronizePreviewModelInvoice().setOnClickListener(v -> {
            List<Transaction> listOfModelInvoices = functions.getModelInvoiceTransaction();
            if (listOfModelInvoices.isEmpty()) Functions.makeSnakebar("Aucune données à afficher");
            else displayListOfTransaction(new MutableLiveData<>(listOfModelInvoices), Enums.TransactionType.MODELINVOICE);
        });

        popupContentSynchronize.getPopupContentSynchronizePreviewModelIncome().setOnClickListener(v -> {
            List<Transaction> listOfModelIncomes = functions.getModelIncomeTransaction();
            if (listOfModelIncomes.isEmpty()) Functions.makeSnakebar("Aucune données à afficher");
            else displayListOfTransaction(new MutableLiveData<>(listOfModelIncomes), Enums.TransactionType.MODELINCOME);
        });
    }

    public void makeLoginPopup(BudgetRequestsInterface callback) {
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

    public void displayListOfTransaction(LiveData<List<Transaction>> listOfTransaction, Enums.TransactionType type, boolean... isEx) {
        boolean isExternal = isEx.length > 0 && isEx[0];

        if (isNull(budgetViewModel) && !isExternal) {
            functions.makeToast("BudgetViewModel not defined but required.");
            return;
        }

        int icon = R.drawable.undefined;
        String title = "";
        if (type == Enums.TransactionType.INVOICE || type == Enums.TransactionType.MODELINVOICE) {
            icon = type == Enums.TransactionType.INVOICE ? R.drawable.invoice : R.drawable.model;
            title = "Liste des prélèvements";
        } else if (type == Enums.TransactionType.INCOME || type == Enums.TransactionType.MODELINCOME){
            icon = type == Enums.TransactionType.INCOME ? R.drawable.income : R.drawable.model;
            title = "Liste des revenus";
        } else if (type == Enums.TransactionType.EXPENSE){
            icon = R.drawable.expense;
            title = "Liste des dépenses";
        }

        PopupContainer popupContainer = new PopupContainer(context, MainActivity.getViewRoot());
        PopupDisplayTileContent popupDisplayTileContent = new PopupDisplayTileContent(context, MainActivity.getViewRoot(), listOfTransaction, budgetViewModel, isExternal);
        popupContainer.addContent(popupDisplayTileContent.getLayout());
        popupDisplayTileContent.setPopupDisplayContentTitle(title);
        popupDisplayTileContent.setPopupDisplayContentTitleIcon(icon);
        popupDisplayTileContent.setPopupDisplayTileContentPeriodTv(Functions.convertStdDateToLocale(functions.getPeriodById(parseLong(functions.getSettingByLabel(Variables.settingPeriod).value)).label));

        popupDisplayTileContent.getPopupDisplayTileContentBtnClose().setOnClickListener(v2 -> popupContainer.closePopup());
    }

    public void popupCreatePeriod() {
        PopupContainer popupContainer = new PopupContainer(context, MainActivity.getViewRoot());
        PopupPeriodContent popupPeriodContent = new PopupPeriodContent(context, MainActivity.getViewRoot());
        popupContainer.addContent(popupPeriodContent.getLayout());

        popupPeriodContent.getPopupContentElementBtnClose().setOnClickListener(v1 -> popupContainer.closePopup());

        popupPeriodContent.getPopupContentPeriodPreviewModelIncome().setOnClickListener(v1 -> {
            PopupContainer popupContainer1 = new PopupContainer(context, MainActivity.getViewRoot());
            PopupModelContent popupModelContent = new PopupModelContent(context, MainActivity.getViewRoot(), Enums.TransactionType.MODELINCOME, budgetViewModel);

            popupModelContent.getPopupContentModelBtnAdd().setVisibility(View.GONE);

            popupContainer1.addContent(popupModelContent.getLayout());
        });

        popupPeriodContent.getPopupContentPeriodPreviewModelInvoice().setOnClickListener(v1 -> {
            PopupContainer popupContainer1 = new PopupContainer(context, MainActivity.getViewRoot());
            PopupModelContent popupModelContent = new PopupModelContent(context, MainActivity.getViewRoot(), Enums.TransactionType.MODELINVOICE, budgetViewModel);

            popupModelContent.getPopupContentModelBtnAdd().setVisibility(View.GONE);
            popupContainer1.addContent(popupModelContent.getLayout());
        });

        popupPeriodContent.getPopupContentPeriodSaveBtn().setOnClickListener(v1 -> {
            try {
                String selectedDate = Functions.convertLocaleDateToStd(popupPeriodContent.getPopupContentPeriodPeriodPreview().getText().toString());
                PeriodsTable periodsTable = new PeriodsTable();
                periodsTable.label = selectedDate;
                budgetViewModel.insertPeriod(periodsTable);
                if (popupPeriodContent.getPopupContentPeriodUseModelInvoice().isChecked())
                    budgetViewModel.insertModelInvoice(periodsTable);
                if (popupPeriodContent.getPopupContentPeriodUseModelIncome().isChecked())
                    budgetViewModel.insertModelIncome(periodsTable);
                popupContainer.closePopup();
            } catch (Exception e){
                functions.makeToast("Une erreur est survenue");
                Log.d(TAG, "popupPeriodContent.getPopupContentPeriodSaveBtn() > setPeriodEvents: "+e.getMessage());
            }
        });
    }

    public void popupAddAccount(){
        PopupContainer popupContainer = new PopupContainer(context, MainActivity.getViewRoot());
        PopupAccountContent popupAccountContent = new PopupAccountContent(context, MainActivity.getViewRoot(), null);
        popupContainer.addContent(popupAccountContent.getLayout());
        popupAccountContent.getBtnSave().setOnClickListener(v1 -> {
            String label = popupAccountContent.getPopupContentAccountLabel().getText().toString();
            String amount = popupAccountContent.getPopupContentAccountAmount().getText().toString();

            if (label.isBlank() || amount.isBlank()) functions.makeToast("Veuillez renseigner tous les champs");
            else {
                AccountsTable newAccount = new AccountsTable();
                newAccount.label = label;
                newAccount.amount = amount;

                budgetViewModel.insertAccount(newAccount);
                popupContainer.closePopup();
            }
        });

        popupAccountContent.getBtnClose().setOnClickListener(v2 -> popupContainer.closePopup());
    }

    public void editAccount(AccountsTable a) {
        PopupContainer popupContainer = new PopupContainer(context, MainActivity.getViewRoot());
        PopupAccountContent popupAccountContent = new PopupAccountContent(context, MainActivity.getViewRoot(), a);
        popupContainer.addContent(popupAccountContent.getLayout());

        popupAccountContent.getBtnSave().setOnClickListener(v1 -> {
            String label = popupAccountContent.getPopupContentAccountLabel().getText().toString();
            String amount = popupAccountContent.getPopupContentAccountAmount().getText().toString();

            if (label.isBlank() || amount.isBlank()) functions.makeToast("Veuillez renseigner tous les champs");
            else {
                a.label = label;
                a.amount = amount;

                budgetViewModel.updateAccount(a);
                popupContainer.closePopup();
            }
        });

        popupAccountContent.getBtnDelete().setVisibility(functions.getAllAccounts().size() == 1 ? View.GONE : View.VISIBLE);
        popupAccountContent.getBtnDelete().setOnClickListener(v2 -> {
            budgetViewModel.deleteAccount(a);
            popupContainer.closePopup();
        });

        popupAccountContent.getBtnClose().setOnClickListener(v2 -> popupContainer.closePopup());
    }
}
