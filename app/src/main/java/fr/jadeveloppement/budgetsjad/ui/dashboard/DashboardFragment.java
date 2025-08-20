package fr.jadeveloppement.budgetsjad.ui.dashboard;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Long.parseLong;
import static java.util.Objects.isNull;

import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.AccountTile;
import fr.jadeveloppement.budgetsjad.components.AddAccountTile;
import fr.jadeveloppement.budgetsjad.components.DashboardTile;
import fr.jadeveloppement.budgetsjad.components.PeriodLayout;
import fr.jadeveloppement.budgetsjad.databinding.FragmentDashboardBinding;
import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.PopupHelper;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModel;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModelFactory;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class DashboardFragment extends Fragment
        implements DashboardTile.DashboardTileAddElementClickedInterface,
        PeriodLayout.PeriodLayoutSelectionChanged {

    private final String TAG = "JADBudget";

    private FragmentDashboardBinding binding;

    private LinearLayout dashboardAccountsContainer, dashboardTilesContainer, dashboardPeriodContainer;
    private Functions functions;
    private View root;
    private DashboardTile incomeTile, invoiceTile, expenseTile, forecastFinalTile, forecastEncoursTile;

    private List<AccountTile> accountsTilesList;
    private PeriodLayout periodLayout;

    private BudgetViewModel budgetViewModel;

    private List<AccountsTable> listOfAccounts;
    private PeriodsTable periodSelected;
    private PopupHelper popupHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        functions = new Functions(requireContext());
        dashboardAccountsContainer = binding.dashboardAccountsContainer;
        dashboardTilesContainer = binding.dashboardTilesContainer;
        dashboardPeriodContainer = binding.dashboardPeriodContainer;

        budgetViewModel = new ViewModelProvider(requireActivity(), new BudgetViewModelFactory(requireActivity())).get(BudgetViewModel.class);
        listOfAccounts = functions.getAllAccounts();
        periodSelected = functions.getPeriodById(parseLong(functions.getSettingByLabel(Variables.settingPeriod).value));
        popupHelper = new PopupHelper(requireActivity(), budgetViewModel);

        setDashboardAccountsObserver();
        setPeriodObserver();
        setDashboardTilesLayout();

        return root;
    }

    // ACCOUNTS
    private void setDashboardAccountsObserver() {
        budgetViewModel.getListOfAccounts().observe(getViewLifecycleOwner(), (List<AccountsTable> accounts) -> {
            listOfAccounts = accounts;
            setAccountsLayout();
            // TODO - Update dashboard tiles layouts
        });
    }

    private void setAccountsLayout() {
        dashboardAccountsContainer.removeAllViews();

        SettingsTable settingsActiveAccount = functions.getSettingByLabel(Variables.settingAccount);

        accountsTilesList = new ArrayList<>();

        for (AccountsTable a : functions.getAllAccounts()){
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
                budgetViewModel.updateSettingsAccount(String.valueOf(tile.getAccount().account_id));
            }
        }
        budgetViewModel.accountChanged();
    }

    private void editAccount(AccountsTable a) {

    }
    //

    // PERIOD
    private void setPeriodObserver(){
        budgetViewModel.getPeriodSelected().observe(getViewLifecycleOwner(), (PeriodsTable period) -> {
            periodSelected = period;
            setPeriodLayout();
            // TODO - Update dashboard tiles layouts
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
        budgetViewModel.updateSettingsPeriod(Functions.convertLocaleDateToStd(newDate));
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

        invoiceTile = new DashboardTile(requireContext(), root, this);
        invoiceTile.setIcon(R.drawable.invoice);
        invoiceTile.setTypeTile(Enums.TransactionType.INVOICE);
        invoiceTile.setTileTitle("Prélèvements");
        invoiceTile.setLastElementVisible(false);

        expenseTile = new DashboardTile(requireContext(), root, this);
        expenseTile.setIcon(R.drawable.expense);
        expenseTile.setTypeTile(Enums.TransactionType.EXPENSE);
        expenseTile.setTileTitle("Dépenses");

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

        setDashboardTilesEvents();

        setDashboardTilesbserver();
    }

    private void setDashboardTilesEvents(){
        setInvoiceTileEvents();
        setIncomeTileEvents();
        setExpenseTileEvents();
    }

    private void setInvoiceTileEvents(){
        invoiceTile.getLayout().setOnClickListener(v -> {
            popupHelper.displayListOfTransaction(budgetViewModel.getInvoices(), Enums.TransactionType.INVOICE);
        });

        invoiceTile.getLayout().setOnLongClickListener(v -> {
            tileAddElementClicked(Enums.TransactionType.INVOICE);
            return true;
        });
    }

    private void setIncomeTileEvents(){
        incomeTile.getLayout().setOnClickListener(v -> {
            popupHelper.displayListOfTransaction(budgetViewModel.getIncomes(), Enums.TransactionType.INCOME);
        });

        incomeTile.getLayout().setOnLongClickListener(v -> {
            tileAddElementClicked(Enums.TransactionType.INCOME);
            return true;
        });
    }

    private void setExpenseTileEvents(){
        expenseTile.getLayout().setOnClickListener(v -> {
            popupHelper.displayListOfTransaction(budgetViewModel.getExpenses(), Enums.TransactionType.EXPENSE);
        });

        expenseTile.getLayout().setOnLongClickListener(v -> {
            tileAddElementClicked(Enums.TransactionType.EXPENSE);
            return true;
        });
    }

    private void setDashboardTilesbserver(){
        budgetViewModel.getInvoices().observe(getViewLifecycleOwner(), (List<Transaction> listOfInvoices) -> {
            if (isNull(listOfInvoices) || listOfInvoices.isEmpty()) listOfInvoices = Collections.emptyList();
            double amountInvoices = 0;
            double amountPaid = 0;
            for(Transaction t : listOfInvoices){
                amountInvoices += parseDouble(t.getAmount());
                if (t.getPaid().equalsIgnoreCase("1")){
                    amountPaid += parseDouble(t.getAmount());
                }
            }

            if (!isNull(invoiceTile)) {
                invoiceTile.setTileAmount(Variables.decimalFormat.format(amountInvoices) + " €");
                invoiceTile.setProgressBarText(Variables.decimalFormat.format(amountPaid) + " € / " + Variables.decimalFormat.format(amountInvoices) + " €");
                invoiceTile.setDashboardTileProgressbarProgress((int) Math.ceil(100 * (amountPaid / (amountInvoices))));
                if (!isNull(invoiceTile.getDashboardTileLoading()) && invoiceTile.getDashboardTileLoading().getVisibility() == View.VISIBLE)
                    invoiceTile.getDashboardTileLoading().setVisibility(View.GONE);
            }
        });

        budgetViewModel.getIncomes().observe(getViewLifecycleOwner(), (List<Transaction> listOfIncomes) -> {
            if (isNull(listOfIncomes) || listOfIncomes.isEmpty()) listOfIncomes = Collections.emptyList();
            double amountIncomes = 0;
            for(Transaction t : listOfIncomes)
                amountIncomes += parseDouble(t.getAmount());
            if( !isNull(incomeTile) ) {
                incomeTile.setTileAmount(Variables.decimalFormat.format(amountIncomes) + " €");
                if (!isNull(incomeTile.getDashboardTileLoading()) && incomeTile.getDashboardTileLoading().getVisibility() == View.VISIBLE)
                    incomeTile.getDashboardTileLoading().setVisibility(View.GONE);
            }
        });

        budgetViewModel.getForecastFinal().observe(getViewLifecycleOwner(), (Double forecastFinal) -> {
            if (!isNull(forecastFinalTile)){
                forecastFinalTile.setTileAmount(Variables.decimalFormat.format(forecastFinal) + " €");
                updateExpenseTileProgressbar();
                if (!isNull(forecastFinalTile.getDashboardTileLoading()) && forecastFinalTile.getDashboardTileLoading().getVisibility() == View.VISIBLE)
                    forecastFinalTile.getDashboardTileLoading().setVisibility(View.GONE);
            }
        });

        budgetViewModel.getForecastEncours().observe(getViewLifecycleOwner(), (Double forecastEncours) ->  {
            if (!isNull(forecastEncoursTile)) {
                forecastEncoursTile.setTileAmount(Variables.decimalFormat.format(forecastEncours) + " €");
                double forecastFinal = isNull(budgetViewModel.getForecastFinal().getValue()) ? 0 : budgetViewModel.getForecastFinal().getValue();
                if (forecastEncours < 0 || (forecastEncours / forecastFinal) < 0.2 ) forecastEncoursTile.setTileAmountColor(Color.parseColor("#B22222"));
                else if ((forecastEncours / forecastFinal) < 0.4) forecastEncoursTile.setTileAmountColor(Color.parseColor("#FFA500"));
                else forecastEncoursTile.setTileAmountColor(Color.parseColor("#06a77d"));

                updateExpenseTileProgressbar();
                if (!isNull(forecastEncoursTile.getDashboardTileLoading()) && forecastEncoursTile.getDashboardTileLoading().getVisibility() == View.VISIBLE)
                    forecastEncoursTile.getDashboardTileLoading().setVisibility(View.GONE);
            }
        });

        budgetViewModel.getExpenses().observe(getViewLifecycleOwner(), (List<Transaction> listOfExpenses) -> {
            double amountExpenses = 0;

            if (isNull(listOfExpenses) || listOfExpenses.isEmpty()) listOfExpenses = Collections.emptyList();

            for(Transaction t : listOfExpenses)
                amountExpenses += parseDouble(t.getAmount());

            if (!isNull(expenseTile)) {
                expenseTile.setTileAmount(Variables.decimalFormat.format(amountExpenses) + " €");
                updateExpenseTileProgressbar();
            }
        });
    }

    private void updateExpenseTileProgressbar() {
        Double amountForecastFinal = isNull(budgetViewModel.getForecastFinal().getValue()) ? 0 : budgetViewModel.getForecastFinal().getValue();
        List<Transaction> listOfExpenses = isNull(budgetViewModel.getExpenses().getValue()) ? Collections.emptyList() : budgetViewModel.getExpenses().getValue();
        int progress;
        double amountExpenses = 0;

        assert listOfExpenses != null;
        for (Transaction t : listOfExpenses)
            amountExpenses += parseDouble(t.getAmount());

        if (amountExpenses > amountForecastFinal){
            progress = 100;
            Log.d(TAG, "updateExpenseTileProgressbar: amount > forecastfinal");
        }
        else progress = (int) Math.ceil(100 * (amountExpenses / amountForecastFinal));
        expenseTile.setDashboardTileProgressbarProgress(progress);
        expenseTile.setProgressBarText(Variables.decimalFormat.format(amountExpenses) + " € / " + Variables.decimalFormat.format(amountForecastFinal) + " €");
        if (!listOfExpenses.isEmpty()) expenseTile.setLastElementLabel(listOfExpenses.get(listOfExpenses.size()-1).getLabel() + " - " + listOfExpenses.get(listOfExpenses.size()-1).getAmount() + "€");
        else expenseTile.setLastElementVisible(false);

        if (!isNull(expenseTile.getDashboardTileLoading()) && expenseTile.getDashboardTileLoading().getVisibility() == View.VISIBLE)
            expenseTile.getDashboardTileLoading().setVisibility(View.GONE);
    }

    @Override
    public void tileAddElementClicked(@NonNull Enums.TransactionType type){
        popupHelper.popupAddElement(type);
    }
    //

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        //
    }
}