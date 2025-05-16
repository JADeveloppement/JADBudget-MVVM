package fr.jadeveloppement.budgetsjad.ui.home;

import static java.lang.Long.parseLong;
import static java.util.Objects.isNull;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.MenuIcon;
import fr.jadeveloppement.budgetsjad.components.popups.PopupAccountsContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContainer;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContentLogin;
import fr.jadeveloppement.budgetsjad.components.popups.PopupElementContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupPeriodsContent;
import fr.jadeveloppement.budgetsjad.databinding.FragmentHomeBinding;
import fr.jadeveloppement.budgetsjad.functions.BudgetRequests;
import fr.jadeveloppement.budgetsjad.functions.interfaces.BudgetRequestsInterface;
import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModel;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModelFactory;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ExpensesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.IncomesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.InvoicesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleIncomes;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ModeleInvoices;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class HomeFragment extends Fragment implements BudgetRequestsInterface {

    private FragmentHomeBinding binding;

    private final String TAG = "JadBudget";

    private FlexboxLayout
            menuAddElementContainer,
            menuModelsContainer,
            menuManageDatasContainer,
            menuManageImportExportContainer;

    private MenuIcon
            menuAddInvoice, menuAddIncome, menuAddExpense,
            menuModelIncome, menuModelInvoice,
            menuManageAccounts, menuManagePeriods,
            menuDownload, menuUpload;

    private View viewRoot;

    private BudgetViewModel budgetViewModel;
    private SettingsTable settingsAccount;
    private PeriodsTable periodSelected;

    private Functions functions;

    private Enums.TagRequest TAG_REQUEST = null;
    private List<Enums.DataToRequest> datasToSend, datasToImport = new ArrayList<>();
    private String login = "", password = "";
    private LinearLayout popupLoadingScreen;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        viewRoot = binding.getRoot();

        functions = new Functions(requireActivity());

        menuAddElementContainer = binding.menuAddElementContainer;
        menuModelsContainer = binding.menuModelsContainer;
        menuManageDatasContainer = binding.menuManageDatasContainer;
        menuManageImportExportContainer = binding.menuManageImportExportContainer;

        budgetViewModel = new ViewModelProvider(requireActivity(), new BudgetViewModelFactory(requireActivity())).get(BudgetViewModel.class);

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

    private void makePopupAddElement(Enums.TransactionType type){
        PopupContainer popupContainer = new PopupContainer(requireContext(), viewRoot);
        PopupElementContent popupElementContent = new PopupElementContent(requireContext(), viewRoot, null);
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
            if (label.isBlank() || amount.isBlank()) Toast.makeText(requireContext(), "Veuillez renseigner tous les champs", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getContext(), "Elément rajouté avec succès", Toast.LENGTH_LONG).show();
                popupContainer.closePopup();
            }
        });
    }

    private void setMenuAddElementEvents() {
        menuAddInvoice.getLayout().setOnClickListener(v -> {
            makePopupAddElement(Enums.TransactionType.INVOICE);
        });
        menuAddIncome.getLayout().setOnClickListener(v -> {
            makePopupAddElement(Enums.TransactionType.INCOME);
        });
        menuAddExpense.getLayout().setOnClickListener(v -> {
            makePopupAddElement(Enums.TransactionType.EXPENSE);
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
            PopupContentLogin popupContentLogin = new PopupContentLogin(requireContext(), 1);
            popupContentLogin.setPopupTitle("Récupérer les données");
            popupContentLogin.setPopupBtnText("Récupérer");
            popupContainer.addContent(popupContentLogin.getLayout());

            popupContentLogin.getBtnSave().setOnClickListener(v1 -> {
                TAG_REQUEST = Enums.TagRequest.IMPORT_DATA;
                datasToImport = new ArrayList<>();

                popupLoadingScreen = popupContentLogin.getPopupContentLoginLoadingScreen();
                popupLoadingScreen.setVisibility(View.VISIBLE);

                if (popupContentLogin.getPopupContentLoginInvoiceCb().isChecked())
                    datasToImport.add(Enums.DataToRequest.INVOICE);
                if (popupContentLogin.getPopupContentLoginIncomeCb().isChecked())
                    datasToImport.add(Enums.DataToRequest.INCOME);
                if (popupContentLogin.getPopupContentLoginExpenseCb().isChecked())
                    datasToImport.add(Enums.DataToRequest.EXPENSE);
                if (popupContentLogin.getPopupContentLoginModelInvoiceCb().isChecked())
                    datasToImport.add(Enums.DataToRequest.MODELINVOICE);
                if (popupContentLogin.getPopupContentLoginModelIncomeCb().isChecked())
                    datasToImport.add(Enums.DataToRequest.MODELINCOME);

                login = popupContentLogin.getLogin();
                password = popupContentLogin.getPassword();

                BudgetRequests budgetRequests = new BudgetRequests(requireContext(), login, password, this);
                budgetRequests.handleLogin();
            });

            popupContentLogin.getBtnClose().setOnClickListener(v1 -> popupContainer.closePopup());
        });

        menuUpload.getLayout().setOnClickListener(v -> {
            PopupContainer popupContainer = new PopupContainer(requireContext(), MainActivity.getViewRoot());
            PopupContentLogin popupContentLogin = new PopupContentLogin(requireContext(), 2);
            popupContentLogin.setPopupTitle("Envoyer des données");
            popupContentLogin.setPopupBtnText("Envoyer");
            popupContainer.addContent(popupContentLogin.getLayout());

            popupContentLogin.getBtnSave().setOnClickListener(v1 -> {
                TAG_REQUEST = Enums.TagRequest.EXPORT_DATA;
                datasToSend = new ArrayList<>();

                popupLoadingScreen = popupContentLogin.getPopupContentLoginLoadingScreen();
                popupLoadingScreen.setVisibility(View.VISIBLE);

                if (popupContentLogin.getPopupContentLoginInvoiceCb().isChecked())
                    datasToSend.add(Enums.DataToRequest.INVOICE);
                if (popupContentLogin.getPopupContentLoginIncomeCb().isChecked())
                    datasToSend.add(Enums.DataToRequest.INCOME);
                if (popupContentLogin.getPopupContentLoginExpenseCb().isChecked())
                    datasToSend.add(Enums.DataToRequest.EXPENSE);
                if (popupContentLogin.getPopupContentLoginModelInvoiceCb().isChecked())
                    datasToSend.add(Enums.DataToRequest.MODELINVOICE);
                if (popupContentLogin.getPopupContentLoginModelIncomeCb().isChecked())
                    datasToSend.add(Enums.DataToRequest.MODELINCOME);

                login = popupContentLogin.getLogin();
                password = popupContentLogin.getPassword();

                BudgetRequests budgetRequests = new BudgetRequests(requireContext(), login, password, this);
                budgetRequests.handleLogin();
                popupLoadingScreen.setVisibility(View.VISIBLE);
            });

            popupContentLogin.getBtnClose().setOnClickListener(v1 -> popupContainer.closePopup());
        });

        menuManageImportExportContainer.addView(menuDownload.getLayout());
        menuManageImportExportContainer.addView(menuUpload.getLayout());
    }

    @Override
    public void loginNonOk(){
        resetTagAndLogins();
    }

    @Override
    public void loginOk(String t){
        if (isNull(TAG_REQUEST)) {
            resetTagAndLogins();
            return;
        }

        if (TAG_REQUEST == Enums.TagRequest.EXPORT_DATA){
            if (datasToSend.isEmpty()) {
                functions.makeToast("Veuillez cocher au moins une case SVP.");
                resetTagAndLogins();
                return;
            }

            StringBuilder datas = new StringBuilder("&datas=");

            if (datasToSend.contains(Enums.DataToRequest.INVOICE)) datas.append("<n>").append(functions.convertListToDatas(functions.getAllInvoicesTransaction()));
            if (datasToSend.contains(Enums.DataToRequest.INCOME)) datas.append("<n>").append(functions.convertListToDatas(functions.getAllIncomesTransaction()));
            if (datasToSend.contains(Enums.DataToRequest.EXPENSE)) datas.append("<n>").append(functions.convertListToDatas(functions.getAllExpensesTransaction()));
            if (datasToSend.contains(Enums.DataToRequest.MODELINCOME)) datas.append("<n>").append(functions.convertListToDatas(functions.getModelIncomeTransaction()));
            if (datasToSend.contains(Enums.DataToRequest.MODELINVOICE)) datas.append("<n>").append(functions.convertListToDatas(functions.getModelInvoiceTransaction()));

            BudgetRequests budgetRequests = new BudgetRequests(requireContext(), login, password, this);
            budgetRequests.makeSaveDatas(t, datas.toString());

            resetTagAndLogins();
        } else if (TAG_REQUEST == Enums.TagRequest.IMPORT_DATA){
            BudgetRequests budgetRequests = new BudgetRequests(requireContext(), login, password, this);
            if (datasToImport.isEmpty()) {
                functions.makeToast("Veuillez cocher au moins une case SVP.");
                resetTagAndLogins();
                return;
            }

            for (Enums.DataToRequest d : datasToImport){
                budgetRequests.makeImportDatas(t, d);
            }

            functions.makeToast("Données récupérées avec succès.");
            resetTagAndLogins();
        }
    }

    private void resetTagAndLogins() {
        if (!isNull(popupLoadingScreen)) popupLoadingScreen.setVisibility(View.GONE);
        login = "";
        password = "";
        TAG_REQUEST = null;
        datasToImport = new ArrayList<>();
        datasToSend = new ArrayList<>();
        datasToImport = new ArrayList<>();
        popupLoadingScreen = null;
    }

    @Override
    public void datasSaved(){
        functions.makeToast("Données sauvées avec succès.");
    }

    @Override
    public void datasImported(JSONObject r){
        try {
            String datas = r.getString("datas").trim();
            if (datas.contains("<n>")){
                String[] rows = datas.split("<n>");
                for(String line : rows){
                    if (line.contains("<l>")){
                        String[] cols = line.split("<l>");
                        String transaction_ID = cols[0];
                        String label = cols[1];
                        String amount = cols[2];
                        String period = cols[3];
                        String account_id = cols[4];
                        String paid = cols[5];
                        String type = cols[6];

                        if (type.equalsIgnoreCase(String.valueOf(Enums.TransactionType.INVOICE))){
                            InvoicesTable invoicesTable = functions.getInvoiceById(parseLong(transaction_ID));
                            boolean create = false;

                            if (isNull(invoicesTable)){
                                invoicesTable = new InvoicesTable();
                                create = true;
                            }
                            invoicesTable.label = label;
                            invoicesTable.amount = amount;
                            invoicesTable.account_id = parseLong(account_id);
                            invoicesTable.date = period;
                            invoicesTable.paid = paid;

                            if (create) {
                                budgetViewModel.addTransaction(functions.convertObjectToTransaction(invoicesTable));
                            }
                            else {
                                budgetViewModel.updateTransaction(functions.convertObjectToTransaction(invoicesTable));
                            }

                        } else if (type.equalsIgnoreCase(String.valueOf(Enums.TransactionType.INCOME))){

                            IncomesTable incomesTable = functions.getIncomeById(parseLong(transaction_ID));
                            boolean create = false;

                            if (isNull(incomesTable)){
                                incomesTable = new IncomesTable();
                                create = true;
                            }
                            incomesTable.label = label;
                            incomesTable.amount = amount;
                            incomesTable.account_id = parseLong(account_id);
                            incomesTable.date = period;
                            incomesTable.paid = paid;

                            if (create) budgetViewModel.addTransaction(functions.convertObjectToTransaction(incomesTable));
                            else budgetViewModel.updateTransaction(functions.convertObjectToTransaction(incomesTable));

                        } else if (type.equalsIgnoreCase(String.valueOf(Enums.TransactionType.EXPENSE))){

                            ExpensesTable expensesTable = functions.getExpenseById(parseLong(transaction_ID));
                            boolean create = false;

                            if (isNull(expensesTable)){
                                expensesTable = new ExpensesTable();
                                create = true;
                            }
                            expensesTable.label = label;
                            expensesTable.amount = amount;
                            expensesTable.account_id = parseLong(account_id);
                            expensesTable.date = period;

                            if (create) budgetViewModel.addTransaction(functions.convertObjectToTransaction(expensesTable));
                            else budgetViewModel.updateTransaction(functions.convertObjectToTransaction(expensesTable));

                        } else if (type.equalsIgnoreCase(String.valueOf(Enums.TransactionType.MODELINVOICE))){

                            ModeleInvoices modeleInvoices = functions.getModeleInvoiceById(parseLong(transaction_ID));
                            boolean create = false;

                            if (isNull(modeleInvoices)){
                                modeleInvoices = new ModeleInvoices();
                                create = true;
                            }
                            modeleInvoices.label = label;
                            modeleInvoices.amount = amount;
                            modeleInvoices.date = period;

                            if (create) budgetViewModel.addTransaction(functions.convertObjectToTransaction(modeleInvoices));
                            else budgetViewModel.updateTransaction(functions.convertObjectToTransaction(modeleInvoices));

                        } else if (type.equalsIgnoreCase(String.valueOf(Enums.TransactionType.MODELINCOME))){
                            ModeleIncomes modeleIncomes = functions.getModeleIncomeById(parseLong(transaction_ID));
                            boolean create = false;

                            if (isNull(modeleIncomes)){
                                modeleIncomes = new ModeleIncomes();
                                create = true;
                            }
                            modeleIncomes.label = label;
                            modeleIncomes.amount = amount;
                            modeleIncomes.date = period;

                            if (create) budgetViewModel.addTransaction(functions.convertObjectToTransaction(modeleIncomes));
                            else budgetViewModel.updateTransaction(functions.convertObjectToTransaction(modeleIncomes));
                        }
                    }
                }
            }
        } catch(Exception e){
            Log.d(TAG, "HomeFragment > datasImported: ", e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}