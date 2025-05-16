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
import fr.jadeveloppement.budgetsjad.components.popups.PopupContentSynchronize;
import fr.jadeveloppement.budgetsjad.components.popups.PopupElementContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupPeriodsContent;
import fr.jadeveloppement.budgetsjad.databinding.FragmentHomeBinding;
import fr.jadeveloppement.budgetsjad.functions.BudgetRequests;
import fr.jadeveloppement.budgetsjad.functions.PopupHelper;
import fr.jadeveloppement.budgetsjad.functions.Variables;
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

    private MenuIcon
            menuAddInvoice, menuAddIncome, menuAddExpense,
            menuModelIncome, menuModelInvoice,
            menuManageAccounts, menuManagePeriods,
            menuLogin, menuLogout, menuDownload, menuUpload;

    private View viewRoot;

    private BudgetViewModel budgetViewModel;
    private SettingsTable settingsAccount;
    private PeriodsTable periodSelected;

    private Functions functions;

    private Enums.TagRequest TAG_REQUEST = null;
    private List<Enums.DataToRequest> datasToSend, datasToImport = new ArrayList<>();
    private String login = "", password = "";
    private LinearLayout popupLoadingScreen;

    private PopupContainer popupLoginContainer = null;

    private PopupHelper popupHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        viewRoot = binding.getRoot();

        functions = new Functions(requireActivity());
        budgetViewModel = new ViewModelProvider(requireActivity(), new BudgetViewModelFactory(requireActivity())).get(BudgetViewModel.class);
        popupHelper = new PopupHelper(requireContext(), budgetViewModel);

        setObservers();

        initializeUI();
        initializeUIEvents();

        checkIfLogged();

        return viewRoot;
    }

    private void initializeUI(){
        FlexboxLayout menuAddElementContainer = binding.menuAddElementContainer;
        FlexboxLayout menuModelsContainer = binding.menuModelsContainer;
        FlexboxLayout menuManageDatasContainer = binding.menuManageDatasContainer;
        FlexboxLayout menuManageImportExportContainer = binding.menuManageImportExportContainer;

        // Add Element MENU
        menuAddInvoice = new MenuIcon(requireContext(), menuAddElementContainer, "Nouveau\nprélèvement", R.drawable.invoice);
        menuAddIncome = new MenuIcon(requireContext(), menuAddElementContainer, "Nouveau\nrevenu", R.drawable.income);
        menuAddExpense = new MenuIcon(requireContext(), menuAddElementContainer, "Nouvelle\ndépense", R.drawable.expense);

        menuAddElementContainer.addView(menuAddInvoice.getLayout());
        menuAddElementContainer.addView(menuAddIncome.getLayout());
        menuAddElementContainer.addView(menuAddExpense.getLayout());
        //

        // Models MENU
        menuModelIncome = new MenuIcon(requireContext(), menuAddElementContainer, "Modèles de\nrevenu", R.drawable.model);
        menuModelInvoice = new MenuIcon(requireContext(), menuAddElementContainer, "Modèles de\nprélèvement", R.drawable.model);

        menuModelsContainer.addView(menuModelIncome.getLayout());
        menuModelsContainer.addView(menuModelInvoice.getLayout());
        //

        // Manage Accounts/Period MENU
        menuManageAccounts = new MenuIcon(requireContext(), menuAddElementContainer, "Gestion\nomptes", R.drawable.account_card);
        menuManagePeriods = new MenuIcon(requireContext(), menuAddElementContainer, "Gestion\npériode", R.drawable.schedule);

        menuManageDatasContainer.addView(menuManageAccounts.getLayout());
        menuManageDatasContainer.addView(menuManagePeriods.getLayout());
        //

        // Manage Login MENU
        menuLogin = new MenuIcon(requireContext(), menuManageImportExportContainer, "Connexion", R.drawable.user);
        menuLogout = new MenuIcon(requireContext(), menuManageImportExportContainer, "Déconnexion", R.drawable.logout);
        menuDownload = new MenuIcon(requireContext(), menuManageImportExportContainer, "Récupérer", R.drawable.download);
        menuUpload = new MenuIcon(requireContext(), menuManageImportExportContainer, "Envoyer", R.drawable.upload);

        menuManageImportExportContainer.addView(menuLogin.getLayout());
        menuManageImportExportContainer.addView(menuLogout.getLayout());
        menuManageImportExportContainer.addView(menuDownload.getLayout());
        menuManageImportExportContainer.addView(menuUpload.getLayout());
        //

    }

    private void initializeUIEvents(){
        // Add element MENU
        menuAddInvoice.getLayout().setOnClickListener(v -> {
            popupHelper.popupAddElement(Enums.TransactionType.INVOICE);
        });
        menuAddIncome.getLayout().setOnClickListener(v -> {
            popupHelper.popupAddElement(Enums.TransactionType.INVOICE);
        });
        menuAddExpense.getLayout().setOnClickListener(v -> {
            popupHelper.popupAddElement(Enums.TransactionType.INVOICE);
        });
        //

        // Manage Account/Period MENU
        menuManageAccounts.getLayout().setOnClickListener(v -> {
            popupHelper.popupManageAccounts();
        });
        menuManagePeriods.getLayout().setOnClickListener(v -> {
            popupHelper.popupManagePeriods();
        });
        //

        // Models MENU
        menuModelIncome.getLayout().setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_modeleincome);
        });
        menuModelInvoice.getLayout().setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_modeleinvoice);
        });
        //

        // Login MENU
        menuLogin.getLayout().setOnClickListener(v -> {
            PopupContainer popupContainer = new PopupContainer(requireContext(), viewRoot);
            PopupContentLogin popupContentLogin = new PopupContentLogin(requireContext(), 0);
            popupContainer.addContent(popupContentLogin.getLayout());

            popupContentLogin.getBtnSave().setOnClickListener(v1 -> {
                login = popupContentLogin.getLogin();
                password = popupContentLogin.getPassword();

                if (login.isBlank() || password.isBlank()) functions.makeToast("Veuillez vous identifiez.");
                else {
                    popupLoadingScreen = popupContentLogin.getPopupContentLoginLoadingScreen();
                    popupLoginContainer = popupContainer;

                    popupLoadingScreen.setVisibility(View.VISIBLE);
                    TAG_REQUEST = Enums.TagRequest.LOGIN;
                    BudgetRequests budgetRequests = new BudgetRequests(requireContext(), login, password, this);
                    budgetRequests.handleLogin();
                }
            });

            popupContentLogin.getBtnClose().setOnClickListener(v1 -> {
                popupContainer.closePopup();
            });
        });

        menuLogout.getLayout().setOnClickListener(v -> {
            SettingsTable settingsTable = functions.getSettingByLabel(Variables.settingsToken);

            if (isNull(settingsTable)){
                settingsTable = new SettingsTable();
                settingsTable.label = Variables.settingsToken;
                settingsTable.value = "";
                functions.insertSettings(settingsTable);
            } else {
                settingsTable.value = "";
                functions.updateSettings(settingsTable);
            }

            handleLoginMenuVisibility(false);
        });

        menuDownload.getLayout().setOnClickListener(v -> {
            login = functions.getSettingByLabel(Variables.settingUsername).value;
            password = functions.getSettingByLabel(Variables.settingPassword).value;

            SettingsTable settingTableToken = functions.getSettingByLabel(Variables.settingsToken);
            if (isNull(settingTableToken)){
                handleLoginMenuVisibility(false);
                return;
            }

            PopupContainer popupContainer = new PopupContainer(requireContext(), MainActivity.getViewRoot());
            PopupContentSynchronize popupContentSynchronize = new PopupContentSynchronize(requireContext());
            popupContentSynchronize.setTitle("Récupérer les données");
            popupContentSynchronize.setBtnSaveLabel("Récupérer");
            popupContainer.addContent(popupContentSynchronize.getLayout());

            popupContentSynchronize.getLoadingScreen().setOnClickListener(v1 -> {});

            popupContentSynchronize.getBtnClose().setOnClickListener(v1 -> popupContainer.closePopup());
            popupContentSynchronize.getBtnSave().setOnClickListener(v1 -> {
                popupLoadingScreen = popupContentSynchronize.getLoadingScreen();

                datasToImport = new ArrayList<>();
                if (popupContentSynchronize.getPopupContentSynchronizeInvoiceCb().isChecked()) datasToImport.add(Enums.DataToRequest.INVOICE);
                if (popupContentSynchronize.getPopupContentSynchronizeIncomeCb().isChecked()) datasToImport.add(Enums.DataToRequest.INCOME);
                if (popupContentSynchronize.getPopupContentSynchronizeExpenseCb().isChecked()) datasToImport.add(Enums.DataToRequest.EXPENSE);
                if (popupContentSynchronize.getPopupContentSynchronizeModelInvoiceCb().isChecked()) datasToImport.add(Enums.DataToRequest.MODELINVOICE);
                if (popupContentSynchronize.getPopupContentSynchronizeModelIncomeCb().isChecked()) datasToImport.add(Enums.DataToRequest.MODELINCOME);

                if (datasToImport.isEmpty()) {
                    functions.makeToast("Veuillez cocher au moins une case.");
                    return;
                }

                BudgetRequests budgetRequests = new BudgetRequests(requireContext(), login, password, this);

                for (Enums.DataToRequest d : datasToImport){
                    budgetRequests.makeImportDatas(settingTableToken.value, d);
                }
            });
        });
        menuUpload.getLayout().setOnClickListener(v -> {
            SettingsTable settingTableToken = functions.getSettingByLabel(Variables.settingsToken);
            if (isNull(settingTableToken)){
                Log.d(TAG, "setMenuLoginEvents: settingToken dont exist");
                return;
            }
            login = functions.getSettingByLabel(Variables.settingUsername).value;
            password = functions.getSettingByLabel(Variables.settingPassword).value;

            PopupContainer popupContainer = new PopupContainer(requireContext(), MainActivity.getViewRoot());
            PopupContentSynchronize popupContentSynchronize = new PopupContentSynchronize(requireContext());
            popupContentSynchronize.setTitle("Récupérer les données");
            popupContentSynchronize.setBtnSaveLabel("Récupérer");
            popupContainer.addContent(popupContentSynchronize.getLayout());

            popupContentSynchronize.getBtnClose().setOnClickListener(v1 -> popupContainer.closePopup());
            popupContentSynchronize.getBtnSave().setOnClickListener(v1 -> {
                popupLoadingScreen = popupContentSynchronize.getLoadingScreen();
                datasToSend = new ArrayList<>();
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

                BudgetRequests budgetRequests = new BudgetRequests(requireContext(), login, password, this);
                budgetRequests.makeSaveDatas(settingTableToken.value, datas.toString());
            });
        });
        //
    }

    private void setObservers() {
        budgetViewModel.getSettingsAccount().observe(getViewLifecycleOwner(), (SettingsTable s) -> {
            settingsAccount = s;
        });

        budgetViewModel.getPeriodSelected().observe(getViewLifecycleOwner(), (PeriodsTable p) -> {
            periodSelected = p;
        });
    }

    private void checkIfLogged() {
        SettingsTable settingUser = functions.getSettingByLabel(Variables.settingUsername);
        SettingsTable settingsToken = functions.getSettingByLabel(Variables.settingsToken);

        if (settingUser.value.isBlank() || settingsToken.value.isBlank()) {
            handleLoginMenuVisibility(false);
            return;
        }
        TAG_REQUEST = Enums.TagRequest.CHECK_TOKEN;
        BudgetRequests budgetRequests = new BudgetRequests(requireContext(), settingUser.value, "", this);
        budgetRequests.checkToken(settingUser.value, settingsToken.value);
    }

    private void handleLoginMenuVisibility(boolean isLogged) {
        menuLogin.getLayout().setVisibility(isLogged ? View.GONE : View.VISIBLE);
        menuLogout.getLayout().setVisibility(isLogged ? View.VISIBLE : View.GONE);
        menuDownload.getLayout().setVisibility(isLogged ? View.VISIBLE : View.GONE);
        menuUpload.getLayout().setVisibility(isLogged ? View.VISIBLE : View.GONE);
    }

    @Override
    public void tokenOk(){
        handleLoginMenuVisibility(true);
    }

    @Override
    public void loginOk(String t){
        if (isNull(TAG_REQUEST)) {
            requestsFinished();
            return;
        }

        if (TAG_REQUEST == Enums.TagRequest.LOGIN){
            if (!isNull(popupLoadingScreen)) popupLoadingScreen.setVisibility(View.GONE);
            SettingsTable settingsToken = functions.getSettingByLabel(Variables.settingsToken);
            settingsToken.value = t;
            functions.updateSettings(settingsToken);

            SettingsTable settingUser = functions.getSettingByLabel(Variables.settingUsername);
            settingUser.value = login;
            functions.updateSettings(settingUser);

            SettingsTable settingPassword = functions.getSettingByLabel(Variables.settingPassword);
            settingPassword.value = password;
            functions.updateSettings(settingPassword);

            functions.makeToast("Vous êtes connectés");

            if (!isNull(popupLoginContainer)){
                popupLoginContainer.closePopup();
                popupLoginContainer = null;
                handleLoginMenuVisibility(true);
            }
        }
    }

    @Override
    public void datasSaved(){
        functions.makeToast("Données sauvées avec succès.");
        requestsFinished();
    }

    @Override
    public void datasImported(JSONObject r){
        try {
            String datas = r.getString("datas").trim();
            processImportDatas(datas);
            functions.makeToast("Données récupérées avec succès.");
            requestsFinished();
        } catch(Exception e){
            Log.d(TAG, "HomeFragment > datasImported: ", e);
        }
    }

    private void processImportDatas(String datas) throws Exception{
        if (datas.contains("<n>")){
            String[] rows = datas.split("<n>");
            for(String line : rows){
                if (line.contains("<l>")){
                    String[] cols = line.split("<l>");
                    String label = cols[1];
                    String amount = cols[2];
                    String period = cols[3];
                    String account_id = cols[4];
                    String paid = cols[5];
                    String type = cols[6];

                    if (type.equalsIgnoreCase(String.valueOf(Enums.TransactionType.INVOICE))){
                        InvoicesTable invoicesTable = new InvoicesTable();

                        invoicesTable.label = label;
                        invoicesTable.amount = amount;
                        invoicesTable.account_id = parseLong(account_id);
                        invoicesTable.date = period;
                        invoicesTable.paid = paid;

                        budgetViewModel.addTransaction(functions.convertObjectToTransaction(invoicesTable));

                    } else if (type.equalsIgnoreCase(String.valueOf(Enums.TransactionType.INCOME))){

                        IncomesTable incomesTable = new IncomesTable();

                        incomesTable.label = label;
                        incomesTable.amount = amount;
                        incomesTable.account_id = parseLong(account_id);
                        incomesTable.date = period;
                        incomesTable.paid = paid;

                        budgetViewModel.addTransaction(functions.convertObjectToTransaction(incomesTable));

                    } else if (type.equalsIgnoreCase(String.valueOf(Enums.TransactionType.EXPENSE))){

                        ExpensesTable expensesTable = new ExpensesTable();

                        expensesTable.label = label;
                        expensesTable.amount = amount;
                        expensesTable.account_id = parseLong(account_id);
                        expensesTable.date = period;

                        budgetViewModel.addTransaction(functions.convertObjectToTransaction(expensesTable));

                    } else if (type.equalsIgnoreCase(String.valueOf(Enums.TransactionType.MODELINVOICE))){

                        ModeleInvoices modeleInvoices = new ModeleInvoices();

                        modeleInvoices.label = label;
                        modeleInvoices.amount = amount;
                        modeleInvoices.date = period;

                        budgetViewModel.addTransaction(functions.convertObjectToTransaction(modeleInvoices));

                    } else if (type.equalsIgnoreCase(String.valueOf(Enums.TransactionType.MODELINCOME))){
                        ModeleIncomes modeleIncomes = new ModeleIncomes();

                        modeleIncomes.label = label;
                        modeleIncomes.amount = amount;
                        modeleIncomes.date = period;

                        budgetViewModel.addTransaction(functions.convertObjectToTransaction(modeleIncomes));
                    }
                }
            }
        }
    }

    @Override
    public void loginNonOk(){
        requestsFinished();
    }

    @Override
    public void requestsFinished(){
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}