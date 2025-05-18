package fr.jadeveloppement.budgetsjad.ui.home;

import static java.lang.Long.parseLong;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.MenuIcon;
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
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class HomeFragment extends Fragment implements BudgetRequestsInterface {

    private static Enums.TagRequest TAG_REQUEST = null;
    
    private FragmentHomeBinding binding;

    private final String TAG = "JadBudget";

    private MenuIcon
            menuAddInvoice, menuAddIncome, menuAddExpense,
            menuModelIncome, menuModelInvoice,
            menuManageAccounts, menuManagePeriods,
            menuLogin, menuLogout, menuDownload, menuUpload;

    private View viewRoot;

    private BudgetViewModel budgetViewModel;

    private Functions functions;

    private LinearLayout menuManageImportExportLoadingScreen;

    private PopupHelper popupHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        viewRoot = binding.getRoot();

        functions = new Functions(requireActivity());
        budgetViewModel = new ViewModelProvider(requireActivity(), new BudgetViewModelFactory(requireActivity())).get(BudgetViewModel.class);
        popupHelper = new PopupHelper(requireContext(), budgetViewModel);

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

        menuManageImportExportLoadingScreen = binding.menuManageImportExportLoadingScreen;

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
            TAG_REQUEST = Enums.TagRequest.LOGIN;

            popupHelper.makeLoginPopup(this);
            popupHelper.toggleLoadingScreen(true);
        });

        menuLogout.getLayout().setOnClickListener(v -> {
            TAG_REQUEST = Enums.TagRequest.LOGOUT;

            SettingsTable settingsTable = functions.getSettingByLabel(Variables.settingsToken);
            settingsTable.value = "";
            functions.updateSettings(settingsTable);

            SettingsTable settingsPassword = functions.getSettingByLabel(Variables.settingPassword);
            settingsPassword.value = "";
            functions.updateSettings(settingsPassword);

            handleLoginMenuVisibility(false);

            requestsFinished();
        });

        menuDownload.getLayout().setOnClickListener(v -> {
            TAG_REQUEST = Enums.TagRequest.IMPORT_DATA;
            popupHelper.popupImportDatas(this);
            popupHelper.toggleLoadingScreen(true);
        });
        menuUpload.getLayout().setOnClickListener(v -> {
            TAG_REQUEST = Enums.TagRequest.EXPORT_DATA;
            popupHelper.popupExportDatas(this);
            popupHelper.toggleLoadingScreen(true);
        });
        //
    }

    private void checkIfLogged() {
        SettingsTable settingUser = functions.getSettingByLabel(Variables.settingUsername);
        SettingsTable settingsToken = functions.getSettingByLabel(Variables.settingsToken);

        if (settingUser.value.isBlank() || settingsToken.value.isBlank()) {
            handleLoginMenuVisibility(false);
            return;
        }
        BudgetRequests budgetRequests = new BudgetRequests(requireContext(), settingUser.value, "", this);
        budgetRequests.checkToken(settingUser.value, settingsToken.value);
    }

    private void handleLoginMenuVisibility(boolean isLogged) {
        menuManageImportExportLoadingScreen.setVisibility(View.GONE);

        menuLogin.getLayout().setVisibility(isLogged ? View.GONE : View.VISIBLE);
        menuLogout.getLayout().setVisibility(isLogged ? View.VISIBLE : View.GONE);
        menuDownload.getLayout().setVisibility(isLogged ? View.VISIBLE : View.GONE);
        menuUpload.getLayout().setVisibility(isLogged ? View.VISIBLE : View.GONE);
    }

    private void processImportDatas(String datas) throws Exception{
        Log.d(TAG, "processImportDatas: datas : " + datas);
        if (datas.contains("<n>")){
            String[] rows = datas.split("<n>");
            for(String line : rows){
                if (line.contains("<l>")){
                    String[] cols = line.split("<l>");
                    String label = cols[1];
                    String amount = cols[2];
                    String period = functions.getPeriodById(parseLong(functions.getSettingByLabel(Variables.settingPeriod).value)).label;
                    String account_id = functions.getSettingByLabel(Variables.settingAccount).value;
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
    public void tokenOk(){
        requestsFinished();
        handleLoginMenuVisibility(true);
    }

    @Override
    public void tokenNonOk(){
        TAG_REQUEST = Enums.TagRequest.TOKEN_NON_OK;
        functions.emptyUserInfos();
        handleLoginMenuVisibility(false);
        requestsFinished();
    }

    @Override
    public void previewDatas(Enums.DataToRequest type) {
        TAG_REQUEST = Enums.TagRequest.DISPLAY_DATA;
        String login = functions.getSettingByLabel(Variables.settingUsername).value;
        String password = functions.getSettingByLabel(Variables.settingPassword).value;
        String token = functions.getSettingByLabel(Variables.settingsToken).value;
        BudgetRequests budgetRequests = new BudgetRequests(requireContext(), login, password, this);
        budgetRequests.makeImportDatas(token, Collections.singletonList(type));
    }

    @Override
    public void loginOk(){
        popupHelper.closeLoginPopup();
        handleLoginMenuVisibility(true);
        requestsFinished();
    }

    @Override
    public void loginNonOk(){
        TAG_REQUEST = Enums.TagRequest.LOGIN_NON_OK;
        requestsFinished();
    }

    @Override
    public void datasSaved(){
        requestsFinished();
    }

    @Override
    public void datasImported(JSONObject r){
        try {
            String datas = r.getString("datas").trim();
            Enums.TransactionType type = Functions.convertStrtypeToTransactionType(r.getString("type").trim());
            Log.d(TAG, "datasImported: type : " + type);
            if (TAG_REQUEST == Enums.TagRequest.DISPLAY_DATA) {
                List<Transaction> listOfDatas = new ArrayList<>();
                if (datas.contains("<n>") && datas.contains("<l>")){
                    for(String row : datas.split("<n>")){
                        String[] cols = row.split("<l>");
                        listOfDatas.add(new Transaction(cols[1], cols[2], cols[3], cols[4], cols[5], Functions.convertStrtypeToTransactionType(cols[6])));
                    }
                    popupHelper.displayListOfTransaction(new MutableLiveData<>(listOfDatas), type, true);
                } else Functions.makeSnakebar("Aucune données à afficher.");
                popupHelper.toggleLoadingScreen(false);
            }
            else {
                processImportDatas(datas);
            }
        } catch(Exception e){
            Log.d(TAG, "HomeFragment > datasImported: ", e);
        }
    }

    @Override
    public void requestsFinished(){
        if (TAG_REQUEST == Enums.TagRequest.LOGIN)
            Functions.makeSnakebar("Vous êtes connecté.");
        else if (TAG_REQUEST == Enums.TagRequest.LOGIN_NON_OK)
            functions.makeToast("Mauvais identifiants.");
        else if (TAG_REQUEST == Enums.TagRequest.LOGOUT)
            Functions.makeSnakebar("Vous êtes déconnecté.");
        if (TAG_REQUEST == Enums.TagRequest.IMPORT_DATA)
            functions.makeToast("Données importées avec succès.");
        else if (TAG_REQUEST == Enums.TagRequest.EXPORT_DATA)
            functions.makeToast("Données envoyées avec succès.");
        else if (TAG_REQUEST == Enums.TagRequest.TOKEN_NON_OK)
            Functions.makeSnakebar("Votre session a expiré.");

        TAG_REQUEST = null;
        popupHelper.toggleLoadingScreen(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        TAG_REQUEST = null;
    }
}