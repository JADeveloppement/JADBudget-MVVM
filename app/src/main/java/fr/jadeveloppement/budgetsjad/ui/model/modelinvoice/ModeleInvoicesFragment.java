package fr.jadeveloppement.budgetsjad.ui.model.modelinvoice;

import static java.lang.Double.parseDouble;
import static java.util.Objects.isNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import fr.jadeveloppement.budgetsjad.databinding.FragmentModeleinvoicesBinding;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModel;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModelFactory;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class ModeleInvoicesFragment extends Fragment {
    private FragmentModeleinvoicesBinding binding;
    private View viewRoot;

    private Button fragmentModelInvoiceAddElement;
    private LinearLayout fragmentModelInvoiceLoading;
    private RecyclerView fragmentModelInvoiceListContainer;
    private TextView fragmentModelInvoiceTotalTv;

    private BudgetViewModel budgetViewModel;
    private PeriodsTable periodSelected;
    private SettingsTable settingsAccount;
    private ElementAdapter modelInvoiceAdapter;

    private List<Transaction> listOfInvoices;
    private boolean listLoaded = false, accountLoaded = false, periodLoaded = false;

    private enum DatatypeLoaded {
        LIST, ACCOUNT, PERIOD
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentModeleinvoicesBinding.inflate(inflater, container, false);
        viewRoot = binding.getRoot();

        budgetViewModel = new ViewModelProvider(requireActivity(), new BudgetViewModelFactory(requireContext())).get(BudgetViewModel.class);

        fragmentModelInvoiceAddElement = binding.fragmentModelInvoiceAddElement;
        fragmentModelInvoiceLoading = binding.fragmentModelInvoiceLoading;
        fragmentModelInvoiceListContainer = binding.fragmentModelInvoiceListContainer;
        fragmentModelInvoiceTotalTv = binding.fragmentModelInvoiceTotalTv;

        fragmentModelInvoiceAddElement.setClickable(false);

        fragmentModelInvoiceAddElement.setOnClickListener(v -> {
            if (!periodLoaded || !accountLoaded || !listLoaded) return;

            PopupContainer popupContainer = new PopupContainer(requireContext(), viewRoot);
            PopupElementContent popupElementContent = new PopupElementContent(requireContext(), viewRoot, null);
            popupElementContent.getPopupContentElementIsPaid().setVisibility(View.GONE);
            popupElementContent.getPopupContentElementBtnDelete().setVisibility(View.GONE);
            popupElementContent.getPopupContentElementPeriodLayout().setVisibility(View.GONE);
            popupElementContent.getPopupContentElementTitle().setText("Ajouter un modèle de revenu");

            popupContainer.addContent(popupElementContent.getLayout());

            popupElementContent.getPopupContentElementBtnSave().setOnClickListener(v1 -> {
                String label = popupElementContent.getPopupContentElementLabel().getText().toString();
                String amount = popupElementContent.getPopupContentElementAmount().getText().toString();

                if (label.isBlank() || amount.isBlank()) Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
                else {
                    Transaction newModelInvoice = new Transaction(
                            label,
                            amount,
                            "",
                            "",
                            "",
                            Transaction.TransactionType.MODELINVOICE
                    );

                    if (!isNull(modelInvoiceAdapter)){
                        modelInvoiceAdapter.addTransaction(newModelInvoice);
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
        budgetViewModel.getModelInvoice().observe(getViewLifecycleOwner(), (List<Transaction> listModels) -> {
            fragmentModelInvoiceLoading.setVisibility(View.GONE);
            listOfInvoices = listModels;
            if (!listLoaded && !isNull(listOfInvoices)) {
                updateLoadingValues(ModeleInvoicesFragment.DatatypeLoaded.LIST);
                addDatasToView();
            }
        });

        budgetViewModel.getSettingsAccount().observe(getViewLifecycleOwner(), (SettingsTable setting) -> {
            settingsAccount = setting;
            if (!accountLoaded && !isNull(settingsAccount)) updateLoadingValues(ModeleInvoicesFragment.DatatypeLoaded.ACCOUNT);
        });

        budgetViewModel.getPeriodSelected().observe(getViewLifecycleOwner(), (PeriodsTable period) -> {
            periodSelected = period;
            if (!periodLoaded && !isNull(periodSelected)) updateLoadingValues(ModeleInvoicesFragment.DatatypeLoaded.PERIOD);
        });
    }

    private void addDatasToView(){
        modelInvoiceAdapter = new ElementAdapter(requireContext(), listOfInvoices, budgetViewModel);
        fragmentModelInvoiceListContainer.setAdapter(modelInvoiceAdapter);
        fragmentModelInvoiceListContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        double amount = 0;
        for (Transaction t : listOfInvoices)
            amount += parseDouble(t.getAmount());
        fragmentModelInvoiceTotalTv.setText(Variables.decimalFormat.format(amount) + " €");
    }

    private void updateLoadingValues(ModeleInvoicesFragment.DatatypeLoaded d){
        if (d == ModeleInvoicesFragment.DatatypeLoaded.PERIOD) periodLoaded = true;
        if (d == ModeleInvoicesFragment.DatatypeLoaded.LIST) listLoaded = true;
        if (d == ModeleInvoicesFragment.DatatypeLoaded.ACCOUNT) accountLoaded = true;

        if (periodLoaded && listLoaded && accountLoaded) {
            fragmentModelInvoiceLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
