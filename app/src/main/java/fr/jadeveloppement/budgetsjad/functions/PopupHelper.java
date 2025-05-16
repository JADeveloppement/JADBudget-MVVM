package fr.jadeveloppement.budgetsjad.functions;

import static java.lang.Long.parseLong;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.components.popups.PopupAccountsContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContainer;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContentLogin;
import fr.jadeveloppement.budgetsjad.components.popups.PopupElementContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupPeriodsContent;
import fr.jadeveloppement.budgetsjad.functions.interfaces.BudgetRequestsInterface;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModel;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class PopupHelper {

    private final Context context;
    private PeriodsTable periodSelected;
    private Functions functions;
    private BudgetViewModel budgetViewModel;

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

    // TODO
    public void popupMakeLogin(BudgetRequestsInterface callback){
    }
}
