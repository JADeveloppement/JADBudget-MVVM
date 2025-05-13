package fr.jadeveloppement.budgetsjad.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.MenuIcon;
import fr.jadeveloppement.budgetsjad.components.popups.PopupAccountsContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContainer;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContentLogin;
import fr.jadeveloppement.budgetsjad.components.popups.PopupElementContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupModelContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupPeriodsContent;
import fr.jadeveloppement.budgetsjad.databinding.FragmentHomeBinding;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModel;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModelFactory;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private final String TAG = "JadBudget";

    private FlexboxLayout menuAddElementContainer, menuModelsContainer, menuManageDatasContainer, menuManageImportExportContainer;
    private MenuIcon menuAddInvoice, menuAddIncome, menuAddExpense, menuModelIncome, menuModelInvoice, menuManageAccounts, menuManagePeriods,
    menuDownload, menuUpload;
    private View viewRoot;
    private BudgetViewModel budgetViewModel;
    private SettingsTable settingsAccount;
    private PeriodsTable periodSelected;

    private List<Transaction> listModelInvoices, listModelIncomes;

    private PopupModelContent popupModelContent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        viewRoot = binding.getRoot();

        menuAddElementContainer = binding.menuAddElementContainer;
        menuModelsContainer = binding.menuModelsContainer;
        menuManageDatasContainer = binding.menuManageDatasContainer;
        menuManageImportExportContainer = binding.menuManageImportExportContainer;

        budgetViewModel = new ViewModelProvider(requireActivity(), new BudgetViewModelFactory(requireContext())).get(BudgetViewModel.class);

        setObservers();

        setAddElementMenu();
        setModelsMenu();
        setModelManage();
        setLoginMenu();

        setMenuAddElementEvents();
        setMenuModelsEvents();
        setMenuManageEvents();

        return viewRoot;
    }

    private void setObservers() {
        budgetViewModel.getSettingsAccount().observe(getViewLifecycleOwner(), (SettingsTable s) -> {
            settingsAccount = s;
        });

        budgetViewModel.getPeriodSelected().observe(getViewLifecycleOwner(), (PeriodsTable p) -> {
            periodSelected = p;
        });
    }

    private void makePopupAddElement(Transaction.TransactionType type){
        PopupContainer popupContainer = new PopupContainer(requireContext(), viewRoot);
        PopupElementContent popupElementContent = new PopupElementContent(requireContext(), viewRoot, null);
        popupElementContent.getPopupContentElementPeriodTv().setText(Functions.convertStdDateToLocale(periodSelected.label));
        popupElementContent.getPopupContentElementBtnDelete().setVisibility(View.GONE);
        popupContainer.addContent(popupElementContent.getLayout());

        String titlePopup = "Ajouter un élément";
        if (type == Transaction.TransactionType.EXPENSE) titlePopup = "Ajouter une dépense";
        else if (type == Transaction.TransactionType.INCOME) titlePopup = "Ajouter un revenu";
        else if (type == Transaction.TransactionType.INVOICE) titlePopup = "Ajouter un prélèvement";
        else if (type == Transaction.TransactionType.MODELINCOME) titlePopup = "Nouveau modèle de revenu";
        else if (type == Transaction.TransactionType.MODELINVOICE) titlePopup = "Nouveau modèle de prélèvement";

        popupElementContent.getPopupContentElementTitle().setText(titlePopup);

        if (type != Transaction.TransactionType.INVOICE)
            popupElementContent.getPopupContentElementIsPaid().setVisibility(View.GONE);

        popupElementContent.getPopupContentElementBtnClose().setOnClickListener(v1 -> {
            popupContainer.closePopup();
        });

        popupElementContent.getPopupContentElementBtnSave().setOnClickListener(v -> {
            String label = popupElementContent.getPopupContentElementLabel().getText().toString();
            String amount = popupElementContent.getPopupContentElementAmount().getText().toString();
            if (label.isBlank() || amount.isBlank()) Toast.makeText(requireContext(), "Veuillez renseigner tous les champs", Toast.LENGTH_LONG).show();
            else {
                Transaction transaction = new Transaction(
                        label,
                        amount,
                        Functions.convertLocaleDateToStd(popupElementContent.getPopupContentElementPeriodTv().getText().toString()),
                        settingsAccount.value,
                        type == Transaction.TransactionType.INVOICE ? (popupElementContent.getPopupContentElementIsPaid().isChecked() ? "1" : "0") : "0",
                        type
                );
                budgetViewModel.addTransaction(transaction);
                Toast.makeText(getContext(), "Elément rajouté avec succès", Toast.LENGTH_LONG).show();
                popupContainer.closePopup();
            }
        });
    }

    private void setMenuAddElementEvents() {
        menuAddInvoice.getLayout().setOnClickListener(v -> {
            makePopupAddElement(Transaction.TransactionType.INVOICE);
        });
        menuAddIncome.getLayout().setOnClickListener(v -> {
            makePopupAddElement(Transaction.TransactionType.INCOME);
        });
        menuAddExpense.getLayout().setOnClickListener(v -> {
            makePopupAddElement(Transaction.TransactionType.EXPENSE);
        });
    }

    private void setMenuModelsEvents() {
        menuModelIncome.getLayout().setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_modeleincome);
        });
        menuModelInvoice.getLayout().setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_modeleinvoice);
        });
    }

    // DONE
    private void setMenuManageEvents() {
        menuManageAccounts.getLayout().setOnClickListener(v -> {
            PopupContainer popupContainer = new PopupContainer(requireContext(), viewRoot);
            PopupAccountsContent popupAccountContent = new PopupAccountsContent(requireContext(), viewRoot);

            popupContainer.addContent(popupAccountContent.getLayout());

            popupAccountContent.getBtnClose().setOnClickListener(v1 -> popupContainer.closePopup());
        });
        menuManagePeriods.getLayout().setOnClickListener(v -> {
            PopupContainer popupContainer = new PopupContainer(requireContext(), viewRoot);
            PopupPeriodsContent popupPeriodContent = new PopupPeriodsContent(requireContext(), viewRoot);

            popupContainer.addContent(popupPeriodContent.getLayout());

            popupPeriodContent.getBtnClose().setOnClickListener(v1 -> popupContainer.closePopup());
        });
    }

    private void setModelManage() {
        menuManageAccounts = new MenuIcon(requireContext(), menuAddElementContainer, "Gestion\nomptes", R.drawable.account_card);
        menuManagePeriods = new MenuIcon(requireContext(), menuAddElementContainer, "Gestion\npériode", R.drawable.schedule);

        menuManageDatasContainer.addView(menuManageAccounts.getLayout());
        menuManageDatasContainer.addView(menuManagePeriods.getLayout());
    }

    private void setModelsMenu() {
        menuModelIncome = new MenuIcon(requireContext(), menuAddElementContainer, "Modèles de\nrevenu", R.drawable.model);
        menuModelInvoice = new MenuIcon(requireContext(), menuAddElementContainer, "Modèles de\nprélèvement", R.drawable.model);

        menuModelsContainer.addView(menuModelIncome.getLayout());
        menuModelsContainer.addView(menuModelInvoice.getLayout());
    }

    private void setAddElementMenu() {
        menuAddInvoice = new MenuIcon(requireContext(), menuAddElementContainer, "Nouveau\nprélèvement", R.drawable.invoice);
        menuAddIncome = new MenuIcon(requireContext(), menuAddElementContainer, "Nouveau\nrevenu", R.drawable.income);
        menuAddExpense = new MenuIcon(requireContext(), menuAddElementContainer, "Nouvelle\ndépense", R.drawable.expense);

        menuAddElementContainer.addView(menuAddInvoice.getLayout());
        menuAddElementContainer.addView(menuAddIncome.getLayout());
        menuAddElementContainer.addView(menuAddExpense.getLayout());
    }

    private void setLoginMenu(){
        menuDownload = new MenuIcon(requireContext(), menuManageImportExportContainer, "Récupérer", R.drawable.download);
        menuUpload = new MenuIcon(requireContext(), menuManageImportExportContainer, "Envoyer", R.drawable.upload);

        menuDownload.getLayout().setOnClickListener(v -> {
            PopupContainer popupContainer = new PopupContainer(requireContext(), MainActivity.getViewRoot());
            PopupContentLogin popupContentLogin = new PopupContentLogin(requireContext());
            popupContentLogin.setPopupTitle("Récupérer les données");
            popupContentLogin.setPopupBtnText("Récupérer");
            popupContainer.addContent(popupContentLogin.getLayout());

            popupContentLogin.getBtnClose().setOnClickListener(v1 -> popupContainer.closePopup());
        });

        menuUpload.getLayout().setOnClickListener(v -> {
            PopupContainer popupContainer = new PopupContainer(requireContext(), MainActivity.getViewRoot());
            PopupContentLogin popupContentLogin = new PopupContentLogin(requireContext());
            popupContentLogin.setPopupTitle("Envoyer des données");
            popupContentLogin.setPopupBtnText("Envoyer");
            popupContainer.addContent(popupContentLogin.getLayout());

            popupContentLogin.getBtnClose().setOnClickListener(v1 -> popupContainer.closePopup());
        });

        menuManageImportExportContainer.addView(menuDownload.getLayout());
        menuManageImportExportContainer.addView(menuUpload.getLayout());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}