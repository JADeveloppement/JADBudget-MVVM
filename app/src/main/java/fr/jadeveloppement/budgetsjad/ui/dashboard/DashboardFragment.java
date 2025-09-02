package fr.jadeveloppement.budgetsjad.ui.dashboard;

import static java.lang.Long.parseLong;
import static java.util.Objects.isNull;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.AccountTile;
import fr.jadeveloppement.budgetsjad.components.AddAccountTile;
import fr.jadeveloppement.budgetsjad.components.DashboardTile;
import fr.jadeveloppement.budgetsjad.components.PeriodLayout;
import fr.jadeveloppement.budgetsjad.components.popups.interfaces.AccountsInterface;
import fr.jadeveloppement.budgetsjad.components.popups.interfaces.PeriodsInterface;
import fr.jadeveloppement.budgetsjad.components.popups.interfaces.TransactionInterface;
import fr.jadeveloppement.budgetsjad.databinding.FragmentDashboardBinding;
import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.PopupHelper;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.models.AccountsViewModel;
import fr.jadeveloppement.budgetsjad.models.AccountsViewModelFactory;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModel;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModelFactory;
import fr.jadeveloppement.budgetsjad.models.PeriodsViewModel;
import fr.jadeveloppement.budgetsjad.models.PeriodsViewModelFactory;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.TransactionsTable;

public class DashboardFragment extends Fragment
        implements DashboardTile.DashboardTileAddElementClickedInterface,
        PeriodLayout.PeriodLayoutSelectionChanged,
        TransactionInterface,
        AccountsInterface,
        PeriodsInterface {

    private final String TAG = "JADBudget > DashboardFragment";

    private FragmentDashboardBinding binding;
    private PopupHelper popupHelper;
    private BudgetViewModel budgetViewModel;
    private AccountsViewModel accountsViewModel;
    private PeriodsViewModel periodsViewModel;
    private DashboardTile incomeTile, invoiceTile, expenseTile, forecastFinalTile, forecastEncoursTile;
    private PeriodLayout periodLayout;
    private List<AccountTile> accountsTilesList;
    private LinearLayout dashboardAccountsContainer, dashboardTilesContainer, dashboardPeriodContainer;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        dashboardAccountsContainer = binding.dashboardAccountsContainer;
        dashboardTilesContainer = binding.dashboardTilesContainer;
        dashboardPeriodContainer = binding.dashboardPeriodContainer;

        budgetViewModel = new ViewModelProvider(requireActivity(), new BudgetViewModelFactory(requireActivity().getApplication())).get(BudgetViewModel.class);
        popupHelper = new PopupHelper(requireActivity(), this, this, this, null);

        accountsViewModel = new ViewModelProvider(requireActivity(), new AccountsViewModelFactory(requireActivity().getApplication())).get(AccountsViewModel.class);
        periodsViewModel = new ViewModelProvider(requireActivity(), new PeriodsViewModelFactory(requireActivity().getApplication())).get(PeriodsViewModel.class);

        setDashboardAccountsObserver();
        setPeriodsObserver();
        setPeriodLayout();
        setDashboardTilesLayout();

        return root;
    }

    // ACCOUNTS
    private void setDashboardAccountsObserver() {
        accountsViewModel.setSettingsAccount();
        accountsViewModel.getListOfAccountsTable().observe(getViewLifecycleOwner(), this::setAccountsLayout);
        accountsViewModel.updateListAccounts();
    }

    private void setAccountsLayout(List<AccountsTable> listOfAccounts) {
        dashboardAccountsContainer.removeAllViews();

        SettingsTable settingsActiveAccount = accountsViewModel.getSettingsAccount().getValue();

        accountsTilesList = new ArrayList<>();

        for (AccountsTable a : listOfAccounts){
            AccountTile accountTile = new AccountTile(requireContext(), a);

            accountTile.getLayout().setOnLongClickListener(v -> {
                popupHelper.editAccount(a);
                return true;
            });

            if (a.account_id == parseLong(settingsActiveAccount.value))
                accountTile.setActive();
            else accountTile.setInactive();

            accountsTilesList.add(accountTile);
            dashboardAccountsContainer.addView(accountTile.getLayout());

            accountTile.getLayout().setOnClickListener(v -> setActiveAccount(accountTile));

        }

        AddAccountTile addAccountTile = new AddAccountTile(requireContext());
        addAccountTile.getLayout().setOnClickListener(v -> popupHelper.popupAddAccount());
        dashboardAccountsContainer.addView(addAccountTile.getLayout());
    }

    private void setActiveAccount(AccountTile accountTile) {
        for (AccountTile tile : accountsTilesList){
            if (tile != accountTile) tile.setInactive();
            else {
                tile.setActive();
                accountsViewModel.updateSettingsAccount(String.valueOf(tile.getAccount().account_id));
                budgetViewModel.updateLiveDataTransactionsTable();
            }
        }
        accountsViewModel.accountActiveChanged();
    }
    @Override
    public void accountAdded(AccountsTable a) {
        accountsViewModel.insertAccount(a);
    }

    @Override
    public void accountEdited(AccountsTable a) {
        accountsViewModel.updateAccount(a);
    }

    @Override
    public void accountDeleted(AccountsTable a) {
        accountsViewModel.deleteAccount(a);
    }
    //

    // PERIOD
    public void setPeriodsObserver(){
        periodsViewModel.getListOfPeriodsTable().observe(getViewLifecycleOwner(), (List<PeriodsTable> listOfPeriods) -> {
            setPeriodLayout();
        });
    }
    private void setPeriodLayout(){
        periodLayout = new PeriodLayout(requireContext(), dashboardPeriodContainer, this);
        dashboardPeriodContainer.removeAllViews();
        dashboardPeriodContainer.addView(periodLayout.getLayout());

        setPeriodEvents();
    }
    private void setPeriodEvents() {
        periodLayout.getPeriodLayoutBtnAddPeriod().setOnClickListener(v -> {
            popupHelper.popupCreatePeriod();
        });
    }
    @Override
    public void periodChanged(String newDate){
        periodsViewModel.updateSettingsPeriod(Functions.convertLocaleDateToStd(newDate));
        budgetViewModel.updateLiveDataTransactionsTable();
    }
    @Override
    public void periodAdded(PeriodsTable p, boolean hasModelInvoice, boolean hasModelIncome) {
        periodsViewModel.insertPeriod(p);
        budgetViewModel.updateLiveDataTransactionsTable();
    }
    @Override
    public void periodDeleted(PeriodsTable p) {
        periodsViewModel.deletePeriod(p);
        budgetViewModel.updateLiveDataTransactionsTable();
    }
    //

    // TILES
    private void setDashboardTilesLayout() {
        dashboardTilesContainer.removeAllViews();
        incomeTile = new DashboardTile(requireContext(), root, this);
        incomeTile.setIcon(R.drawable.income);
        incomeTile.setTileTitle("Revenus");
        incomeTile.setTypeTile(Enums.TransactionType.INCOME);
        incomeTile.setProgressBarVisible(false);
        incomeTile.setLastElementVisible(false);
        setupTileEvent(incomeTile, Variables.strTypeIncome, Enums.TransactionType.INCOME);

        invoiceTile = new DashboardTile(requireContext(), root, this);
        invoiceTile.setIcon(R.drawable.invoice);
        invoiceTile.setTypeTile(Enums.TransactionType.INVOICE);
        invoiceTile.setTileTitle("Prélèvements");
        invoiceTile.setLastElementVisible(false);
        setupTileEvent(invoiceTile, Variables.strTypeInvoice, Enums.TransactionType.INVOICE);

        expenseTile = new DashboardTile(requireContext(), root, this);
        expenseTile.setIcon(R.drawable.expense);
        expenseTile.setTypeTile(Enums.TransactionType.EXPENSE);
        expenseTile.setTileTitle("Dépenses");
        setupTileEvent(expenseTile, Variables.strTypeExpense, Enums.TransactionType.EXPENSE);

        forecastFinalTile = new DashboardTile(requireContext(), root, this);
        forecastFinalTile.setIcon(R.drawable.forecast);
        forecastFinalTile.setTileTitle("Argent de poche");
        forecastFinalTile.setProgressBarVisible(false);
        forecastFinalTile.setLastElementVisible(false);
        forecastFinalTile.setBtnAddElementVisible(false);

        forecastEncoursTile = new DashboardTile(requireContext(), root, null);
        forecastEncoursTile.setIcon(R.drawable.account_card);
        forecastEncoursTile.setTileTitle("Argent en cours");
        forecastEncoursTile.setProgressBarVisible(false);
        forecastEncoursTile.setLastElementVisible(false);
        forecastEncoursTile.setBtnAddElementVisible(false);

        dashboardTilesContainer.addView(incomeTile.getLayout());
        dashboardTilesContainer.addView(invoiceTile.getLayout());
        dashboardTilesContainer.addView(expenseTile.getLayout());
        dashboardTilesContainer.addView(forecastFinalTile.getLayout());
        dashboardTilesContainer.addView(forecastEncoursTile.getLayout());

        setDashboardTileOsbserver();
    }

    private void setupTileEvent(DashboardTile tile, String type, Enums.TransactionType eType){
        tile.getLayout().setOnClickListener(v -> {
            switch(type){
                case Variables.strTypeInvoice:
                    popupHelper.displayListOfTransactionsTable(budgetViewModel.getInvoicesTransactionsTable().getValue(), Variables.strTypeInvoice);
                    break;
                case Variables.strTypeIncome:
                    popupHelper.displayListOfTransactionsTable(budgetViewModel.getIncomesTransactionsTable().getValue(), Variables.strTypeIncome);
                    break;
                case Variables.strTypeExpense:
                    popupHelper.displayListOfTransactionsTable(budgetViewModel.getExpensesTransactionsTable().getValue(), Variables.strTypeExpense);
                    break;
            }
        });

        tile.getLayout().setOnLongClickListener(v -> {
            tileAddElementClicked(eType);
            return true;
        });
    }

    private void setDashboardTileOsbserver(){
        budgetViewModel.getInvoicesTransactionsTable().observe(getViewLifecycleOwner(), (List<TransactionsTable> listOfInvoices) -> {
            if (isNull(listOfInvoices)) listOfInvoices = Collections.emptyList();

            double amountInvoices = 0;
            double amountPaid = 0;

            for(TransactionsTable t : listOfInvoices){
                amountInvoices += t.amount;
                if (t.paid.equalsIgnoreCase("1")) amountPaid += t.amount;
            }

            if (!isNull(invoiceTile)){
                invoiceTile.setTileAmount(Variables.decimalFormat.format(amountInvoices) + " €");
                invoiceTile.setProgressBarText(Variables.decimalFormat.format(amountPaid) + " € / " + Variables.decimalFormat.format(amountInvoices) + " €");
                invoiceTile.setDashboardTileProgressbarProgress((int) Math.ceil(100 * (amountPaid / (amountInvoices))));
                if (!isNull(invoiceTile.getDashboardTileLoading()) && invoiceTile.getDashboardTileLoading().getVisibility() == View.VISIBLE)
                    invoiceTile.getDashboardTileLoading().setVisibility(View.GONE);
            }
        });

        budgetViewModel.getIncomesTransactionsTable().observe(getViewLifecycleOwner(), (List<TransactionsTable> listOfIncomes) -> {
            if (isNull(listOfIncomes) || listOfIncomes.isEmpty()) listOfIncomes = Collections.emptyList();
            double amountIncomes = 0;

            for(TransactionsTable t : listOfIncomes)
                amountIncomes += t.amount;

            if( !isNull(incomeTile) ) {
                incomeTile.setTileAmount(Variables.decimalFormat.format(amountIncomes) + " €");
                if (!isNull(incomeTile.getDashboardTileLoading()) && incomeTile.getDashboardTileLoading().getVisibility() == View.VISIBLE)
                    incomeTile.getDashboardTileLoading().setVisibility(View.GONE);
            }
        });

        budgetViewModel.getExpensesTransactionsTable().observe(getViewLifecycleOwner(), (List<TransactionsTable> listOfExpenses) -> {
            double amountExpenses = 0;

            if (isNull(listOfExpenses)) listOfExpenses = Collections.emptyList();

            for(TransactionsTable t : listOfExpenses){
                amountExpenses += t.amount;
            }

            if (!isNull(expenseTile)) {
                expenseTile.setTileAmount(Variables.decimalFormat.format(amountExpenses) + " €");
                updateExpenseTileProgressbar();
            }
        });

        budgetViewModel.getForecastFinalTransactionsTable().observe(getViewLifecycleOwner(), (Double forecastFinal) -> {
            if (!isNull(forecastFinalTile)){
                forecastFinalTile.setTileAmount(Variables.decimalFormat.format(forecastFinal) + " €");
                updateExpenseTileProgressbar();
                if (!isNull(forecastFinalTile.getDashboardTileLoading()) && forecastFinalTile.getDashboardTileLoading().getVisibility() == View.VISIBLE)
                    forecastFinalTile.getDashboardTileLoading().setVisibility(View.GONE);
            }
        });

        budgetViewModel.getForecastEncoursTransactionsTable().observe(getViewLifecycleOwner(), (Double forecastEncours) ->  {
            if (!isNull(forecastEncoursTile)) {
                forecastEncoursTile.setTileAmount(Variables.decimalFormat.format(forecastEncours) + " €");
                double forecastFinal = isNull(budgetViewModel.getForecastFinalTransactionsTable().getValue()) ? 0 : budgetViewModel.getForecastFinalTransactionsTable().getValue();
                if (forecastEncours < 0 || (forecastEncours / forecastFinal) < 0.2 ) forecastEncoursTile.setTileAmountColor(Color.parseColor("#B22222"));
                else if ((forecastEncours / forecastFinal) < 0.4) forecastEncoursTile.setTileAmountColor(Color.parseColor("#FFA500"));
                else forecastEncoursTile.setTileAmountColor(Color.parseColor("#06a77d"));

                updateExpenseTileProgressbar();
                if (!isNull(forecastEncoursTile.getDashboardTileLoading()) && forecastEncoursTile.getDashboardTileLoading().getVisibility() == View.VISIBLE)
                    forecastEncoursTile.getDashboardTileLoading().setVisibility(View.GONE);
            }
        });
    }

    private void updateExpenseTileProgressbar() {
        Double amountForecastFinal = isNull(budgetViewModel.getForecastFinalTransactionsTable().getValue()) ? 0 : budgetViewModel.getForecastFinalTransactionsTable().getValue();
        List<TransactionsTable> listOfExpenses = isNull(budgetViewModel.getExpensesTransactionsTable().getValue()) ? Collections.emptyList() : budgetViewModel.getExpensesTransactionsTable().getValue();
        int progress;
        double amountExpenses = 0;

        for (TransactionsTable t : listOfExpenses)
            amountExpenses += t.amount;

        if (amountExpenses > amountForecastFinal){
            progress = 100;
        }
        else progress = (int) Math.ceil(100 * (amountExpenses / amountForecastFinal));
        expenseTile.setDashboardTileProgressbarProgress(progress);
        expenseTile.setProgressBarText(Variables.decimalFormat.format(amountExpenses) + " € / " + Variables.decimalFormat.format(amountForecastFinal) + " €");
        if (!listOfExpenses.isEmpty()) expenseTile.setLastElementLabel(listOfExpenses.get(listOfExpenses.size()-1).label + " - " + listOfExpenses.get(listOfExpenses.size()-1).amount + "€");
        else expenseTile.setLastElementVisible(false);

        if (!isNull(expenseTile.getDashboardTileLoading()) && expenseTile.getDashboardTileLoading().getVisibility() == View.VISIBLE)
            expenseTile.getDashboardTileLoading().setVisibility(View.GONE);
    }

    @Override
    public void tileAddElementClicked(@NonNull Enums.TransactionType type){
        popupHelper.popupAddElement(type);
    }

    @Override
    public void popupTransactionAdded(TransactionsTable t){
        budgetViewModel.addTransactionsTable(t);
    }

    @Override
    public void popupTransactionEdited(TransactionsTable t){
        budgetViewModel.updateTransactionsTable(t);
    }

    @Override
    public void popupTransactionDeleted(TransactionsTable t){
        budgetViewModel.deleteTransactionsTable(t);
    }
    //

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        //
    }
}