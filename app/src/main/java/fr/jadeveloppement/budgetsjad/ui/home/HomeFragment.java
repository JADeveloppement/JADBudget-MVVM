package fr.jadeveloppement.budgetsjad.ui.home;

import static java.util.Objects.isNull;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.MenuIcon;
import fr.jadeveloppement.budgetsjad.components.popups.PopupAccountsContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContainer;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContentLogin;
import fr.jadeveloppement.budgetsjad.components.popups.PopupElementContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupModelContent;
import fr.jadeveloppement.budgetsjad.components.popups.PopupPeriodsContent;
import fr.jadeveloppement.budgetsjad.databinding.FragmentHomeBinding;
import fr.jadeveloppement.budgetsjad.functions.BudgetRequests;
import fr.jadeveloppement.budgetsjad.functions.BudgetRequestsInterface;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModel;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModelFactory;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.SettingsTable;

public class HomeFragment extends Fragment implements BudgetRequestsInterface {

    private FragmentHomeBinding binding;

    private final String TAG = "JadBudget";

    private FlexboxLayout menuAddElementContainer, menuModelsContainer, menuManageDatasContainer, menuManageImportExportContainer;
    private MenuIcon menuAddInvoice, menuAddIncome, menuAddExpense, menuModelIncome, menuModelInvoice, menuManageAccounts, menuManagePeriods,
    menuDownload, menuUpload;
    private View viewRoot;
    private BudgetViewModel budgetViewModel;
    private SettingsTable settingsAccount;
    private PeriodsTable periodSelected;

    private Functions functions;

    private List<Transaction> listModelInvoices, listModelIncomes;

    private PopupModelContent popupModelContent;

    private static final String REQUEST_TAG = "LoginRequest"; //tagging the request
    private RequestQueue requestQueue; // Declare RequestQueue as a field of the class.

    private enum TagRequest {
        EXPORT_DATA, IMPORT_DATA
    }

    private enum DataToRequest{
        INVOICE, INCOME, EXPENSE, MODELINVOICE, MODELINCOME
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        viewRoot = binding.getRoot();

        functions = new Functions(requireActivity());

        menuAddElementContainer = binding.menuAddElementContainer;
        menuModelsContainer = binding.menuModelsContainer;
        menuManageDatasContainer = binding.menuManageDatasContainer;
        menuManageImportExportContainer = binding.menuManageImportExportContainer;

        budgetViewModel = new ViewModelProvider(requireActivity(), new BudgetViewModelFactory(requireContext())).get(BudgetViewModel.class);

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

    private void makePopupAddElement(Transaction.TransactionType type){
        PopupContainer popupContainer = new PopupContainer(requireContext(), viewRoot);
        PopupElementContent popupElementContent = new PopupElementContent(requireContext(), viewRoot, null);
        popupElementContent.getPopupContentElementPeriodTv().setText(Functions.convertStdDateToLocale(periodSelected.label));
        popupElementContent.getPopupContentElementBtnDelete().setVisibility(View.GONE);
        popupContainer.addContent(popupElementContent.getLayout());

        String titlePopup = "Ajouter un élément";
        if (type == Transaction.TransactionType.EXPENSE) titlePopup = "Ajouter une dépense";
        else if (type == Transaction.TransactionType.INCOME) titlePopup = "Ajouter un revenu";
        else if (type == Transaction.TransactionType.INVOICE) titlePopup = "Ajouter un prélèvement";
        else if (type == Transaction.TransactionType.MODELINCOME) titlePopup = "Nouveau modèle de revenu";
        else if (type == Transaction.TransactionType.MODELINVOICE) titlePopup = "Nouveau modèle de prélèvement";

        popupElementContent.getPopupContentElementTitle().setText(titlePopup);

        if (type != Transaction.TransactionType.INVOICE)
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
                        type == Transaction.TransactionType.INVOICE ? (popupElementContent.getPopupContentElementIsPaid().isChecked() ? "1" : "0") : "0",
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
            makePopupAddElement(Transaction.TransactionType.INVOICE);
        });
        menuAddIncome.getLayout().setOnClickListener(v -> {
            makePopupAddElement(Transaction.TransactionType.INCOME);
        });
        menuAddExpense.getLayout().setOnClickListener(v -> {
            makePopupAddElement(Transaction.TransactionType.EXPENSE);
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

    // DONE
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

    private TagRequest TAG_REQUEST = null;
    private List<DataToRequest> dataToRequests = new ArrayList<>();
    private String login = "", password = "";

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
                TAG_REQUEST = TagRequest.IMPORT_DATA;

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
                TAG_REQUEST = TagRequest.EXPORT_DATA;
                dataToRequests = new ArrayList<>();

                if (popupContentLogin.getPopupContentLoginInvoiceCb().isChecked())
                    dataToRequests.add(DataToRequest.INVOICE);
                if (popupContentLogin.getPopupContentLoginIncomeCb().isChecked())
                    dataToRequests.add(DataToRequest.INCOME);
                if (popupContentLogin.getPopupContentLoginExpenseCb().isChecked())
                    dataToRequests.add(DataToRequest.EXPENSE);
                if (popupContentLogin.getPopupContentLoginModelInvoiceCb().isChecked())
                    dataToRequests.add(DataToRequest.MODELINVOICE);
                if (popupContentLogin.getPopupContentLoginModelIncomeCb().isChecked())
                    dataToRequests.add(DataToRequest.MODELINCOME);

                login = popupContentLogin.getLogin();
                password = popupContentLogin.getPassword();

                BudgetRequests budgetRequests = new BudgetRequests(requireContext(), login, password, this);
                budgetRequests.handleLogin();
            });

            popupContentLogin.getBtnClose().setOnClickListener(v1 -> popupContainer.closePopup());
        });

        menuManageImportExportContainer.addView(menuDownload.getLayout());
        menuManageImportExportContainer.addView(menuUpload.getLayout());
    }

    @Override
    public void loginOk(String t){
        if (isNull(TAG_REQUEST))
            return;

        if (TAG_REQUEST == TagRequest.EXPORT_DATA){
            if (dataToRequests.isEmpty()) {
                functions.makeToast("Veuillez cocher au moins une case SVP.");
                return;
            }

            StringBuilder datas = new StringBuilder("&datas=");

            if (dataToRequests.contains(DataToRequest.INVOICE)) datas.append("<n>").append(functions.convertListToDatas(functions.getAllInvoicesTransaction()));
            if (dataToRequests.contains(DataToRequest.INCOME)) datas.append("<n>").append(functions.convertListToDatas(functions.getAllIncomesTransaction()));
            if (dataToRequests.contains(DataToRequest.EXPENSE)) datas.append("<n>").append(functions.convertListToDatas(functions.getAllExpensesTransaction()));
            if (dataToRequests.contains(DataToRequest.MODELINCOME)) datas.append("<n>").append(functions.convertListToDatas(functions.getModelIncomeTransaction()));
            if (dataToRequests.contains(DataToRequest.MODELINVOICE)) datas.append("<n>").append(functions.convertListToDatas(functions.getModelInvoiceTransaction()));

            BudgetRequests budgetRequests = new BudgetRequests(requireContext(), login, password, this);
            budgetRequests.makeSaveDatas(t, datas.toString());

            resetTagAndLogins();
        } else if (TAG_REQUEST == TagRequest.IMPORT_DATA){
            BudgetRequests budgetRequests = new BudgetRequests(requireContext(), login, password, this);
            budgetRequests.makeImportDatas(t);

            resetTagAndLogins();
        }
    }

    private void resetTagAndLogins() {
        login = "";
        password = "";
        TAG_REQUEST = null;
    }

    @Override
    public void datasSaved(){
        functions.makeToast("Données sauvées avec succès.");
    }

    @Override
    public void datasImported(String r){
        functions.makeToast("Données importées avec succès.");
        Log.d(TAG, "datasImported: " + r);
    }

    //
    /*private void handleLogin(PopupContentLogin popup) {
        final String login = popup.getLogin().getText().toString().trim();
        final String password = popup.getPassword().getText().toString().trim();

        if (login.isEmpty() || password.isEmpty()) {
            functions.makeToast("Veuillez renseigner tous les champs.");
            return;
        }

        if (!popup.getPopupContentLoginInvoiceCb().isChecked() &&
                !popup.getPopupContentLoginIncomeCb().isChecked() &&
                !popup.getPopupContentLoginExpenseCb().isChecked() &&
                !popup.getPopupContentLoginModelInvoiceCb().isChecked() &&
                !popup.getPopupContentLoginModelIncomeCb().isChecked()) {
            functions.makeToast("Veuillez cocher au moins un élément.");
            return;
        }

        popup.getPopupContentLoginLoadingScreen().setVisibility(View.VISIBLE);
        final String url = "https://jadeveloppement.fr/api/login?login=" + login + "&password=" + password;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    popup.getPopupContentLoginLoadingScreen().setVisibility(View.GONE);
                    processLoginResponse(response, login, password);
                },
                error -> Functions.handleExceptions("PopupContentLogin > handleLogin : ", error)
        );

        jsonObjectRequest.setTag(REQUEST_TAG);
        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }

    private void processLoginResponse(JSONObject response, String login, String password) {
        try {
            if ("1".equals(response.getString("logged"))) {
                final String token = response.getString("token");
                final String datasBody = constructDataBody(login,password, token);

                JsonObjectRequest modelRequest = new JsonObjectRequest(Request.Method.GET, datasBody, null,
                        this::handleModelResponse,
                        this::handleModelError);
                modelRequest.setTag(REQUEST_TAG);
                Volley.newRequestQueue(requireContext()).add(modelRequest);

            } else {
                functions.makeToast("Mauvais identifiants");
                Log.d(TAG, "makeRequest: bad logins");
            }
        } catch (JSONException e) {
            functions.makeToast("Une erreur est survenue (-1).");
            Log.e(TAG, "processLoginResponse: JSONException: " + e.getMessage());
        }
    }

    private String constructDataBody(String login, String password, String token) throws JSONException{
        StringBuilder datasBody = new StringBuilder("&datas=");
        if (popup.getPopupContentLoginInvoiceCb().isChecked()) {
            datasBody.append(functions.convertListToDatas(functions.getAllInvoicesTransaction()));
        }
        if (popupContentLoginIncomeCb.isChecked()) {
            datasBody.append("<n>").append(functions.convertListToDatas(functions.getAllIncomesTransaction()));
        }
        if (popupContentLoginExpenseCb.isChecked()) {
            datasBody.append("<n>").append(functions.convertListToDatas(functions.getAllExpensesTransaction()));
        }
        if (popupContentLoginModelIncomeCb.isChecked()) {
            datasBody.append("<n>").append(functions.convertListToDatas(functions.getModelIncomeTransaction()));
        }
        if (popupContentLoginModelInvoiceCb.isChecked()) {
            datasBody.append("<n>").append(functions.convertListToDatas(functions.getModelInvoiceTransaction()));
        }
        final String loginBody = "login=" + login + "&password=" + password + "&token=" + token;
        return  "https://jadeveloppement.fr/api/updateModelInvoice?" + loginBody + datasBody.toString();
    }

    private void handleModelResponse(JSONObject modelResponse) {
        popupContentLoginLoadingScreen.setVisibility(View.GONE);
        Log.d(TAG, "makeRequest: " + modelResponse);
    }

    private void handleModelError(VolleyError error) {
        popupContentLoginLoadingScreen.setVisibility(View.GONE);
        functions.makeToast("Une erreur est survenue (-2).");
        Log.e(TAG, "BudgetRequests > makeRequest > onErrorResponse: " + error.toString()); // Use Log.e for errors.
    } */
    //

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}