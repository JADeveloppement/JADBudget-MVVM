package fr.jadeveloppement.budgetsjad.ui.model.modelincome;

import static java.lang.Double.parseDouble;
import static java.util.Objects.isNull;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.jadeveloppement.budgetsjad.components.adapters.ElementAdapter;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContainer;
import fr.jadeveloppement.budgetsjad.components.popups.PopupElementContent;
import fr.jadeveloppement.budgetsjad.databinding.FragmentModeleincomesBinding;
import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModel;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModelFactory;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class ModeleIncomesFragment extends Fragment {
    private FragmentModeleincomesBinding binding;
    private View viewRoot;

    private Button fragmentModelIncomeAddElement;
    private LinearLayout fragmentModelIncomeLoading;
    private RecyclerView fragmentModelIncomeListContainer;
    private TextView fragmentModelIncomeTotalTv;

    private BudgetViewModel budgetViewModel;
    private PeriodsTable periodSelected;
    private SettingsTable settingsAccount;
    private ElementAdapter modelIncomeAdapter;

    private List<Transaction> listOfIncomes;
    private boolean listLoaded = false, accountLoaded = false, periodLoaded = false;

    private enum DatatypeLoaded {
        LIST, ACCOUNT, PERIOD
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentModeleincomesBinding.inflate(inflater, container, false);
        viewRoot = binding.getRoot();

        budgetViewModel = new ViewModelProvider(requireActivity(), new BudgetViewModelFactory(requireActivity())).get(BudgetViewModel.class);

        fragmentModelIncomeAddElement = binding.fragmentModelIncomeAddElement;
        fragmentModelIncomeLoading = binding.fragmentModelIncomeLoading;
        fragmentModelIncomeListContainer = binding.fragmentModelIncomeListContainer;
        fragmentModelIncomeTotalTv = binding.fragmentModelIncomeTotalTv;

        fragmentModelIncomeAddElement.setClickable(false);

        fragmentModelIncomeAddElement.setOnClickListener(v -> {
            if (!periodLoaded || !accountLoaded || !listLoaded) return;

            PopupContainer popupContainer = new PopupContainer(requireContext(), viewRoot);
            PopupElementContent popupElementContent = new PopupElementContent(requireContext(), viewRoot, null);
            popupElementContent.getPopupContentElementIsPaid().setVisibility(View.GONE);
            popupElementContent.getPopupContentElementBtnDelete().setVisibility(View.GONE);
            popupElementContent.getPopupContentElementPeriodLayout().setVisibility(View.GONE);
            popupElementContent.getPopupContentElementTitle().setText("Ajouter un modèle de revenu");
            popupElementContent.getPopupContentElementCategoryLayout().setVisibility(View.GONE);

            popupContainer.addContent(popupElementContent.getLayout());

            popupElementContent.getPopupContentElementBtnSave().setOnClickListener(v1 -> {
                String label = popupElementContent.getPopupContentElementLabel().getText().toString();
                String amount = popupElementContent.getPopupContentElementAmount().getText().toString();

                if (label.isBlank() || amount.isBlank()) Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
                else {
                    Transaction newModelIncome = new Transaction(
                            label,
                            amount,
                            "",
                            "",
                            "",
                            Enums.TransactionType.MODELINCOME
                    );

                    if (!isNull(modelIncomeAdapter)){
                        modelIncomeAdapter.addTransaction(newModelIncome);
                    }

                    Toast.makeText(getContext(), "Elément rajouté avec succès", Toast.LENGTH_LONG).show();
                    popupContainer.closePopup();
                }
            });

            popupElementContent.getPopupContentElementBtnClose().setOnClickListener(v1 -> {
                popupContainer.closePopup();
            });
        });

        initObservers();

        return viewRoot;
    }

    private void initObservers(){
        budgetViewModel.getModelIncome().observe(getViewLifecycleOwner(), (List<Transaction> listModels) -> {
            fragmentModelIncomeLoading.setVisibility(View.GONE);
            listOfIncomes = listModels;
            if (!listLoaded && !isNull(listOfIncomes)) {
                updateLoadingValues(DatatypeLoaded.LIST);
                addDatasToView();
            }
        });

        budgetViewModel.getSettingsAccount().observe(getViewLifecycleOwner(), (SettingsTable setting) -> {
            settingsAccount = setting;
            if (!accountLoaded && !isNull(settingsAccount)) updateLoadingValues(DatatypeLoaded.ACCOUNT);
        });

        budgetViewModel.getPeriodSelected().observe(getViewLifecycleOwner(), (PeriodsTable period) -> {
            periodSelected = period;
            if (!periodLoaded && !isNull(periodSelected)) updateLoadingValues(DatatypeLoaded.PERIOD);
        });
    }

    private void addDatasToView(){
        modelIncomeAdapter = new ElementAdapter(requireContext(), listOfIncomes, budgetViewModel, null);
        fragmentModelIncomeListContainer.setAdapter(modelIncomeAdapter);
        fragmentModelIncomeListContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        double amount = 0;
        for (Transaction t : listOfIncomes)
            amount += parseDouble(t.getAmount());
        fragmentModelIncomeTotalTv.setText(Variables.decimalFormat.format(amount) + " €");
    }

    private void updateLoadingValues(DatatypeLoaded d){
        if (d == DatatypeLoaded.PERIOD) periodLoaded = true;
        if (d == DatatypeLoaded.LIST) listLoaded = true;
        if (d == DatatypeLoaded.ACCOUNT) accountLoaded = true;

        if (periodLoaded && listLoaded && accountLoaded) {
            fragmentModelIncomeLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
