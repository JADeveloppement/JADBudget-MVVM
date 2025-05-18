package fr.jadeveloppement.budgetsjad.ui.external;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

import fr.jadeveloppement.budgetsjad.databinding.FragmentManageExternalDatasBinding;
import fr.jadeveloppement.budgetsjad.functions.BudgetRequests;
import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.PopupHelper;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.functions.interfaces.BudgetRequestsInterface;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;

public class ManageExternalDatas extends Fragment implements BudgetRequestsInterface {
    private final String TAG = "JADBudget";

    private FragmentManageExternalDatasBinding binding;
    private View viewRoot;

    private LinearLayout fragment_ManageExternalLoadingScreen;

    private ImageButton fragment_ManageExternalPreviewInvoice, fragment_ManageExternalAddInvoice,
            fragment_ManageExternalPreviewIncome, fragment_ManageExternalAddIncome,
            fragment_ManageExternalPreviewExpense, fragment_ManageExternalAddExpense,
            fragment_ManageExternalPreviewModelInvoice, fragment_ManageExternalAddModelInvoice,
            fragment_ManageExternalPreviewModelIncome, fragment_ManageExternalAddModelIncome;

    private RecyclerView fragment_ManageExternalRecyclerviewInvoices,
            fragment_ManageExternalRecyclerviewIncomes,
            fragment_ManageExternalRecyclerviewExpenses,
            fragment_ManageExternalRecyclerviewModelInvoice,
            fragment_ManageExternalRecyclerviewModelIncome;

    private BudgetRequests budgetRequests;
    private String login, password, token;
    private Functions functions;
    private PopupHelper popupHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentManageExternalDatasBinding.inflate(inflater, container, false);
        viewRoot = binding.getRoot();

        initFields();

        return viewRoot;
    }

    private void initializeUiEvents() {
        budgetRequests.handleLogin();

        fragment_ManageExternalPreviewInvoice.setOnClickListener(v -> toggleRecyclerView(fragment_ManageExternalRecyclerviewInvoices));
        fragment_ManageExternalAddInvoice.setOnClickListener(v -> {
            Log.d(TAG, "initializeUiEvents: add external invoice");
        });

        fragment_ManageExternalPreviewIncome.setOnClickListener(v -> toggleRecyclerView(fragment_ManageExternalRecyclerviewIncomes));
        fragment_ManageExternalAddIncome.setOnClickListener(v -> {
            Log.d(TAG, "initializeUiEvents: add external income");
        });

        fragment_ManageExternalPreviewExpense.setOnClickListener(v -> toggleRecyclerView(fragment_ManageExternalRecyclerviewExpenses));
        fragment_ManageExternalAddExpense.setOnClickListener(v -> {
            Log.d(TAG, "initializeUiEvents: add external expense");
        });

        fragment_ManageExternalPreviewModelInvoice.setOnClickListener(v -> toggleRecyclerView(fragment_ManageExternalRecyclerviewModelInvoice));
        fragment_ManageExternalAddModelInvoice.setOnClickListener(v -> {
            Log.d(TAG, "initializeUiEvents: add external model invoice");
        });

        fragment_ManageExternalPreviewModelIncome.setOnClickListener(v -> toggleRecyclerView(fragment_ManageExternalRecyclerviewModelIncome));
        fragment_ManageExternalAddModelIncome.setOnClickListener(v -> {
            Log.d(TAG, "initializeUiEvents: add external model income");
        });
    }

    private void toggleRecyclerView(RecyclerView recyclerView) {
        recyclerView.setVisibility(recyclerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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
    }

    private void initFields(){
        functions = new Functions(requireContext());
        popupHelper = new PopupHelper(requireContext());
        login = functions.getSettingByLabel(Variables.settingUsername).value;
        password = functions.getSettingByLabel(Variables.settingPassword).value;
        token = functions.getSettingByLabel(Variables.settingsToken).value;
        budgetRequests = new BudgetRequests(requireContext(), login, password, this);

        if (login.isBlank() || password.isBlank() || token.isBlank()) {
            exitFragment();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        initializeUi();
        initializeUiEvents();
    }

    private void exitFragment(){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    @Override
    public void tokenOk() {
        fragment_ManageExternalLoadingScreen.setVisibility(View.GONE);
    }

    @Override
    public void tokenNonOk() {
        Functions.makeSnakebar("Session expirée, veuillez vous reconnecter.");
        exitFragment();
    }

    @Override
    public void loginOk() {
        token = functions.getSettingByLabel(Variables.settingsToken).value;
        budgetRequests.checkToken(login, token);
    }

    @Override
    public void loginNonOk() {
        Functions.makeSnakebar("Session expirée, veuillez vous reconnecter.");
        exitFragment();
    }

    @Override
    public void datasSaved() {

    }

    @Override
    public void datasImported(JSONObject response) {

    }

    @Override
    public void requestsFinished() {

    }

    @Override
    public void previewDatas(Enums.DataToRequest type) {

    }
}
