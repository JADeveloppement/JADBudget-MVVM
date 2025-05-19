package fr.jadeveloppement.budgetsjad.ui.external;

import static java.util.Objects.isNull;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.jadeveloppement.budgetsjad.components.adapters.ElementAdapter;
import fr.jadeveloppement.budgetsjad.databinding.FragmentManageExternalDatasBinding;
import fr.jadeveloppement.budgetsjad.functions.BudgetRequests;
import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.PopupHelper;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.functions.interfaces.BudgetRequestsInterface;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;

public class ManageExternalDatas extends Fragment implements
        BudgetRequestsInterface,
        ElementAdapter.ElementAdapterDeleteClickListener,
        PopupHelper.PopupHelperAddElementBtnClicked {

    private static Enums.TagRequest TAG_REQUEST = null;
    private final String TAG = "JADBudget";

    private FragmentManageExternalDatasBinding binding;
    private View viewRoot;

    private ImageButton fragment_ManageExternalPreviewInvoice, fragment_ManageExternalAddInvoice,
            fragment_ManageExternalPreviewIncome, fragment_ManageExternalAddIncome,
            fragment_ManageExternalPreviewExpense, fragment_ManageExternalAddExpense,
            fragment_ManageExternalPreviewModelInvoice, fragment_ManageExternalAddModelInvoice,
            fragment_ManageExternalPreviewModelIncome, fragment_ManageExternalAddModelIncome;

    private LinearLayout fragment_ManageExternalLoadingScreen;

    private RecyclerView fragment_ManageExternalRecyclerviewInvoices,
            fragment_ManageExternalRecyclerviewIncomes,
            fragment_ManageExternalRecyclerviewExpenses,
            fragment_ManageExternalRecyclerviewModelInvoice,
            fragment_ManageExternalRecyclerviewModelIncome;

    private TextView fragment_ManageExternalInvoiceNb, fragment_ManageExternalIncomeNb, fragment_ManageExternalExpenseNb,
            fragment_ManageExternalModelIncomeNb, fragment_ManageExternalModelInvoiceNb;

    private BudgetRequests budgetRequests;
    private String login;
    private String token;
    private Functions functions;
    private PopupHelper popupHelper;

    ArrayList<List<String>> datasImportedArray = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentManageExternalDatasBinding.inflate(inflater, container, false);
        viewRoot = binding.getRoot();

        initFields();

        return viewRoot;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        initializeUi();
        initializeUiEvents();
    }

    private void initFields(){
        functions = new Functions(requireContext());
        popupHelper = new PopupHelper(requireContext(), this);
        login = functions.getSettingByLabel(Variables.settingUsername).value;
        String password = functions.getSettingByLabel(Variables.settingPassword).value;
        token = functions.getSettingByLabel(Variables.settingsToken).value;
        budgetRequests = new BudgetRequests(requireContext(), login, password, this);

        if (login.isBlank() || password.isBlank() || token.isBlank()) {
            exitFragment();
        }
    }

    private void initializeUi() {
        fragment_ManageExternalLoadingScreen = binding.fragmentManageExternalLoadingScreen;

        fragment_ManageExternalPreviewInvoice = binding.fragmentManageExternalPreviewInvoice;
        fragment_ManageExternalPreviewIncome = binding.fragmentManageExternalPreviewIncome;
        fragment_ManageExternalPreviewExpense = binding.fragmentManageExternalPreviewExpenses;
        fragment_ManageExternalPreviewModelInvoice = binding.fragmentManageExternalPreviewModelInvoice;
        fragment_ManageExternalPreviewModelIncome = binding.fragmentManageExternalPreviewModelIncome;

        fragment_ManageExternalRecyclerviewInvoices = binding.fragmentManageExternalRecyclerviewInvoices;
        fragment_ManageExternalRecyclerviewIncomes = binding.fragmentManageExternalRecyclerviewIncomes;
        fragment_ManageExternalRecyclerviewExpenses = binding.fragmentManageExternalRecyclerviewExpenses;
        fragment_ManageExternalRecyclerviewModelInvoice = binding.fragmentManageExternalRecyclerviewModelInvoice;
        fragment_ManageExternalRecyclerviewModelIncome = binding.fragmentManageExternalRecyclerviewModelIncome;


        fragment_ManageExternalAddInvoice = binding.fragmentManageExternalAddInvoice;
        fragment_ManageExternalAddIncome = binding.fragmentManageExternalAddIncome;
        fragment_ManageExternalAddExpense = binding.fragmentManageExternalAddExpenses;
        fragment_ManageExternalAddModelInvoice = binding.fragmentManageExternalAddModelInvoice;
        fragment_ManageExternalAddModelIncome = binding.fragmentManageExternalAddModelIncome;

        fragment_ManageExternalInvoiceNb = binding.fragmentManageExternalInvoiceNb;
        fragment_ManageExternalIncomeNb = binding.fragmentManageExternalIncomeNb;
        fragment_ManageExternalExpenseNb = binding.fragmentManageExternalExpenseNb;
        fragment_ManageExternalModelIncomeNb = binding.fragmentManageExternalModelIncomeNb;
        fragment_ManageExternalModelInvoiceNb = binding.fragmentManageExternalModelInvoiceNb;
    }

    private void initializeUiEvents() {
        fragment_ManageExternalPreviewInvoice.setOnClickListener(v -> toggleRecyclerView(fragment_ManageExternalRecyclerviewInvoices));
        fragment_ManageExternalAddInvoice.setOnClickListener(v -> {
            popupHelper.popupAddElement(Enums.TransactionType.INVOICE, true);
        });

        fragment_ManageExternalPreviewIncome.setOnClickListener(v -> toggleRecyclerView(fragment_ManageExternalRecyclerviewIncomes));
        fragment_ManageExternalAddIncome.setOnClickListener(v -> {
            popupHelper.popupAddElement(Enums.TransactionType.INCOME, true);
        });

        fragment_ManageExternalPreviewExpense.setOnClickListener(v -> toggleRecyclerView(fragment_ManageExternalRecyclerviewExpenses));
        fragment_ManageExternalAddExpense.setOnClickListener(v -> {
            popupHelper.popupAddElement(Enums.TransactionType.EXPENSE, true);
        });

        fragment_ManageExternalPreviewModelInvoice.setOnClickListener(v -> toggleRecyclerView(fragment_ManageExternalRecyclerviewModelInvoice));
        fragment_ManageExternalAddModelInvoice.setOnClickListener(v -> {
            popupHelper.popupAddElement(Enums.TransactionType.MODELINVOICE, true);
        });

        fragment_ManageExternalPreviewModelIncome.setOnClickListener(v -> toggleRecyclerView(fragment_ManageExternalRecyclerviewModelIncome));
        fragment_ManageExternalAddModelIncome.setOnClickListener(v -> {
            popupHelper.popupAddElement(Enums.TransactionType.MODELINCOME, true);
        });

        budgetRequests.handleLogin();
    }

    @Override
    public void loginOk() {
        token = functions.getSettingByLabel(Variables.settingsToken).value;
        budgetRequests.checkToken(login, token);
    }

    @Override
    public void tokenOk() {
        initializeData();
    }

    private void initializeData(){
        datasImportedArray = new ArrayList<>();
        TAG_REQUEST = Enums.TagRequest.IMPORT_DATA;
        budgetRequests.retrieveAllTransactions(token);
    }

    @Override
    public void allDataRetrieved(ArrayList<Transaction> listOfTransaction) {
        List<Transaction> listOfInvoiceTransaction = new ArrayList<>();
        List<Transaction> listOfExpenseTransaction = new ArrayList<>();
        List<Transaction> listOfIncomeTransaction = new ArrayList<>();
        List<Transaction> listOfModelIncomeTransaction = new ArrayList<>();
        List<Transaction> listOfModelInvoiceTransaction = new ArrayList<>();

        if(!listOfTransaction.isEmpty()){
            for (Transaction t : listOfTransaction){
                if (t.getType() == Enums.TransactionType.INVOICE){
                    listOfInvoiceTransaction.add(t);
                } else if (t.getType() == Enums.TransactionType.INCOME){
                    listOfIncomeTransaction.add(t);
                } else if (t.getType() == Enums.TransactionType.EXPENSE){
                    listOfExpenseTransaction.add(t);
                } else if (t.getType() == Enums.TransactionType.MODELINCOME){
                    listOfModelIncomeTransaction.add(t);
                } else if (t.getType() == Enums.TransactionType.MODELINVOICE){
                    listOfModelInvoiceTransaction.add(t);
                }
            }
        }

        ElementAdapter elementAdapterInvoice = new ElementAdapter(requireContext(), listOfInvoiceTransaction, null, this, true);
        ElementAdapter elementAdapterIncome = new ElementAdapter(requireContext(), listOfIncomeTransaction, null, this,true);
        ElementAdapter elementAdapterExpense = new ElementAdapter(requireContext(), listOfExpenseTransaction, null, this, true);
        ElementAdapter elementAdapterModelIncome = new ElementAdapter(requireContext(), listOfModelIncomeTransaction, null, this, true);
        ElementAdapter elementAdapterModelInvoice = new ElementAdapter(requireContext(), listOfModelInvoiceTransaction, null, this, true);

        fragment_ManageExternalInvoiceNb.setText(String.valueOf(elementAdapterInvoice.getItemCount()));
        fragment_ManageExternalIncomeNb.setText(String.valueOf(elementAdapterIncome.getItemCount()));
        fragment_ManageExternalExpenseNb.setText(String.valueOf(elementAdapterExpense.getItemCount()));
        fragment_ManageExternalModelIncomeNb.setText(String.valueOf(elementAdapterModelIncome.getItemCount()));
        fragment_ManageExternalModelInvoiceNb.setText(String.valueOf(elementAdapterModelInvoice.getItemCount()));

        fragment_ManageExternalRecyclerviewInvoices.setAdapter(elementAdapterInvoice);
        fragment_ManageExternalRecyclerviewIncomes.setAdapter(elementAdapterIncome);
        fragment_ManageExternalRecyclerviewExpenses.setAdapter(elementAdapterExpense);
        fragment_ManageExternalRecyclerviewModelIncome.setAdapter(elementAdapterModelIncome);
        fragment_ManageExternalRecyclerviewModelInvoice.setAdapter(elementAdapterModelInvoice);

        fragment_ManageExternalRecyclerviewInvoices.setLayoutManager(new LinearLayoutManager(requireContext()));
        fragment_ManageExternalRecyclerviewIncomes.setLayoutManager(new LinearLayoutManager(requireContext()));
        fragment_ManageExternalRecyclerviewExpenses.setLayoutManager(new LinearLayoutManager(requireContext()));
        fragment_ManageExternalRecyclerviewModelIncome.setLayoutManager(new LinearLayoutManager(requireContext()));
        fragment_ManageExternalRecyclerviewModelInvoice.setLayoutManager(new LinearLayoutManager(requireContext()));

        if (!isNull(fragment_ManageExternalLoadingScreen) && fragment_ManageExternalLoadingScreen.getVisibility() == View.VISIBLE) fragment_ManageExternalLoadingScreen.setVisibility(View.GONE);
    }

    @Override
    public void requestsFinished() {
        initializeData();
        if (TAG_REQUEST == Enums.TagRequest.DELETE_TRANSACTION){
            functions.makeToast("Elément supprimé avec succès.");
        } else if (TAG_REQUEST == Enums.TagRequest.ADD_TRANSACTION){
            functions.makeToast("Elément ajouté avec succès.");
        }
        TAG_REQUEST = null;
        if (!isNull(fragment_ManageExternalLoadingScreen) && fragment_ManageExternalLoadingScreen.getVisibility() == View.VISIBLE) fragment_ManageExternalLoadingScreen.setVisibility(View.GONE);
    }

    @Override
    public void elementAdapterDeleteClicked(Transaction t) {
        if (!isNull(fragment_ManageExternalLoadingScreen)) fragment_ManageExternalLoadingScreen.setVisibility(View.VISIBLE);
        TAG_REQUEST = Enums.TagRequest.DELETE_TRANSACTION;
        budgetRequests.deleteTransaction(t.getId());
    }

    @Override
    public void popupAddElementBtnSaveClicked(String label, String amount, Enums.TransactionType type) {
        TAG_REQUEST = Enums.TagRequest.ADD_TRANSACTION;
        if (!isNull(fragment_ManageExternalLoadingScreen)) fragment_ManageExternalLoadingScreen.setVisibility(View.VISIBLE);
        budgetRequests.addTransaction(label, amount, String.valueOf(type));
    }

    private void toggleRecyclerView(RecyclerView recyclerView) {
        recyclerView.setVisibility(recyclerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private void exitFragment(){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    @Override
    public void tokenNonOk() {
        functions.makeToast("Session expirée, veuillez vous reconnecter.");
        exitFragment();
    }

    @Override
    public void loginNonOk() {
        functions.makeToast("Session expirée, veuillez vous reconnecter.");
        exitFragment();
    }

    @Override
    public void datasSaved() {}

    @Override
    public void datasImported(JSONObject response) {}

    @Override
    public void previewDatas(Enums.DataToRequest type) {
    }
}
