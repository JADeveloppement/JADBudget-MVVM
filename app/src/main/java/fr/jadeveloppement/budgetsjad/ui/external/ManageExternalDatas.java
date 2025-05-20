package fr.jadeveloppement.budgetsjad.ui.external;

import static java.util.Objects.isNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.ExternalDataTile;
import fr.jadeveloppement.budgetsjad.components.adapters.ElementAdapter;
import fr.jadeveloppement.budgetsjad.databinding.FragmentManageExternalDatasBinding;
import fr.jadeveloppement.budgetsjad.functions.BudgetRequests;
import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.PopupHelper;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.functions.interfaces.BudgetRequestsInterface;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;

public class ManageExternalDatas extends Fragment
        implements
        BudgetRequestsInterface,
        ElementAdapter.ElementAdapterDeleteClickListener,
        PopupHelper.PopupHelperAddElementBtnClicked,
        ExternalDataTile.ExternalDataTileClicked
{

    private static Enums.TagRequest TAG_REQUEST = null;
    private final String TAG = "JADBudget";

    private FragmentManageExternalDatasBinding binding;

    private LinearLayout fragment_ManageExternalLoadingScreen;

    private BudgetRequests budgetRequests;
    private String login;
    private String token;
    private Functions functions;
    private PopupHelper popupHelper;

    ArrayList<List<String>> datasImportedArray = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentManageExternalDatasBinding.inflate(inflater, container, false);
        View viewRoot = binding.getRoot();

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

    private ExternalDataTile externalDataTileInvoice, externalDataTileIncome, externalDataTileExpense,
        externalDataTileModelInvoice, externalDataTileModelIncome;

    private final LinearLayout.LayoutParams tileLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    );

    private void initializeUi() {
        fragment_ManageExternalLoadingScreen = binding.fragmentManageExternalLoadingScreen;

        LinearLayout fragment_ManageExternalTilesContainer = binding.fragmentManageExternalTilesContainer;

        externalDataTileInvoice = new ExternalDataTile(requireContext(), Enums.TransactionType.INVOICE, this);
        externalDataTileInvoice.setLayoutParams(tileLayoutParams);
        externalDataTileInvoice.setTitle(getString(R.string.invoices));

        externalDataTileIncome = new ExternalDataTile(requireContext(), Enums.TransactionType.INCOME, this);
        externalDataTileIncome.setLayoutParams(tileLayoutParams);
        externalDataTileIncome.setTitle(getString(R.string.income));

        externalDataTileExpense = new ExternalDataTile(requireContext(), Enums.TransactionType.EXPENSE, this);
        externalDataTileExpense.setLayoutParams(tileLayoutParams);
        externalDataTileExpense.setTitle(getString(R.string.expenses));

        externalDataTileModelInvoice = new ExternalDataTile(requireContext(), Enums.TransactionType.MODELINVOICE, this);
        externalDataTileModelInvoice.setLayoutParams(tileLayoutParams);
        externalDataTileModelInvoice.setTitle(getString(R.string.model_invoice));

        externalDataTileModelIncome = new ExternalDataTile(requireContext(), Enums.TransactionType.MODELINCOME, this);
        externalDataTileModelIncome.setLayoutParams(tileLayoutParams);
        externalDataTileModelIncome.setTitle(getString(R.string.model_income));

        fragment_ManageExternalTilesContainer.addView(externalDataTileInvoice.getLayout());
        fragment_ManageExternalTilesContainer.addView(externalDataTileIncome.getLayout());
        fragment_ManageExternalTilesContainer.addView(externalDataTileExpense.getLayout());
        fragment_ManageExternalTilesContainer.addView(externalDataTileModelInvoice.getLayout());
        fragment_ManageExternalTilesContainer.addView(externalDataTileModelIncome.getLayout());
    }

    private void initializeUiEvents() {
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

        externalDataTileInvoice.setNbElements(String.valueOf(elementAdapterInvoice.getItemCount()));
        externalDataTileIncome.setNbElements(String.valueOf(elementAdapterIncome.getItemCount()));
        externalDataTileExpense.setNbElements(String.valueOf(elementAdapterExpense.getItemCount()));
        externalDataTileModelIncome.setNbElements(String.valueOf(elementAdapterModelIncome.getItemCount()));
        externalDataTileModelInvoice.setNbElements(String.valueOf(elementAdapterModelInvoice.getItemCount()));

        externalDataTileInvoice.getManageExternDataTileListElementsContainer().setAdapter(elementAdapterInvoice);
        externalDataTileIncome.getManageExternDataTileListElementsContainer().setAdapter(elementAdapterIncome);
        externalDataTileExpense.getManageExternDataTileListElementsContainer().setAdapter(elementAdapterExpense);
        externalDataTileModelIncome.getManageExternDataTileListElementsContainer().setAdapter(elementAdapterModelIncome);
        externalDataTileModelInvoice.getManageExternDataTileListElementsContainer().setAdapter(elementAdapterModelInvoice);

        externalDataTileInvoice.getManageExternDataTileListElementsContainer().setLayoutManager(new LinearLayoutManager(requireContext()));
        externalDataTileIncome.getManageExternDataTileListElementsContainer().setLayoutManager(new LinearLayoutManager(requireContext()));
        externalDataTileExpense.getManageExternDataTileListElementsContainer().setLayoutManager(new LinearLayoutManager(requireContext()));
        externalDataTileModelIncome.getManageExternDataTileListElementsContainer().setLayoutManager(new LinearLayoutManager(requireContext()));
        externalDataTileModelInvoice.getManageExternDataTileListElementsContainer().setLayoutManager(new LinearLayoutManager(requireContext()));

        if (!isNull(fragment_ManageExternalLoadingScreen) && fragment_ManageExternalLoadingScreen.getVisibility() == View.VISIBLE) fragment_ManageExternalLoadingScreen.setVisibility(View.GONE);
    }

    private void exitFragment(){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
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

    @Override
    public void externalDataTileAddElementClicked(Enums.TransactionType type) {
        popupHelper.popupAddElement(type, true);
    }

    @Override
    public void externalDataTilePreviewElementsClicked(ExternalDataTile tile){
        tile.toggleRecyclerView();
    }
}
