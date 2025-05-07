package fr.jadeveloppement.budgetsjad.components.popups;

import static java.util.Objects.isNull;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.adapters.ElementAdapter;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModel;
import fr.jadeveloppement.budgetsjad.models.classes.BudgetData;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;

public class PopupModelContent extends LinearLayout {
    private final String TAG = "JADBudget";

    private final Context context;
    private Transaction.TransactionType modelType;
    private BudgetData budgetData;
    private List<Transaction> listOfElements;
    private View viewParent;
    private View popupLayout;

    private ElementAdapter listAdapter;

    public PopupModelContent(@NonNull Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
    }

    public PopupModelContent(@NonNull Context c, @NonNull View viewP, @NonNull Transaction.TransactionType type, @NonNull BudgetData bData){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.viewParent = viewP;
        this.popupLayout = LayoutInflater.from(context).inflate(R.layout.popup_content_model, (ViewGroup) viewParent, false);
        this.budgetData = bData;
        this.modelType = type;
        if (modelType == Transaction.TransactionType.MODELINCOME){
            Log.d(TAG, "PopupModelContent: INCOME budgetdata size : " + budgetData.getModelIncomeTransaction().size());
            this.listOfElements = isNull(budgetData.getModelIncomeTransaction()) ? Collections.emptyList() : budgetData.getModelIncomeTransaction();
        } else if (modelType == Transaction.TransactionType.MODELINVOICE){
            Log.d(TAG, "PopupModelContent: INVOICE budgetdata size : " + budgetData.getModelIncomeTransaction().size());
            this.listOfElements = isNull(budgetData.getModelInvoiceTransaction()) ? Collections.emptyList() : budgetData.getModelInvoiceTransaction();
        } else this.listOfElements = Collections.emptyList();

        initPopup();
    }

    private TextView popupContentModelTitle;
    private LinearLayout popupContentModelBtnClose;
    private RecyclerView popupContentModelListContainer;
    private Button popupContentModelBtnAdd;

    private void initPopup(){
        popupContentModelTitle = popupLayout.findViewById(R.id.popupContentModelTitle);
        popupContentModelBtnClose = popupLayout.findViewById(R.id.popupContentModelBtnClose);
        popupContentModelListContainer = popupLayout.findViewById(R.id.popupContentModelListContainer);
        popupContentModelBtnAdd = popupLayout.findViewById(R.id.popupContentModelBtnAdd);

        listAdapter = new ElementAdapter(context, listOfElements, null);
        popupContentModelListContainer.setAdapter(listAdapter);
        popupContentModelListContainer.setLayoutManager(new LinearLayoutManager(context));
    }

    public void setPopupTitle(String title){
        popupContentModelTitle.setText(title);
    }

    public TextView getPopupContentModelTitle(){
        return popupContentModelTitle;
    }

    public Button getPopupContentModelBtnAdd(){
        return popupContentModelBtnAdd;
    }

    public LinearLayout getPopupContentModelBtnClose(){
        return popupContentModelBtnClose;
    }

    public LinearLayout getLayout(){
        return (LinearLayout) popupLayout;
    }

    public void refeshList() {
        Log.d(TAG, "refeshList: ");
    }
}
