package fr.jadeveloppement.budgetsjad.components.popups;

import static java.util.Objects.isNull;

import android.content.Context;
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
import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModel;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;

public class PopupModelContent extends LinearLayout {
    private final String TAG = "JADBudget";

    private final Context context;
    private List<Transaction> listOfElements;
    private View popupLayout;

    public PopupModelContent(@NonNull Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
    }

    public PopupModelContent(@NonNull Context c, @NonNull View viewP, @NonNull Enums.TransactionType type, @NonNull BudgetViewModel bModel){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.popupLayout = LayoutInflater.from(context).inflate(R.layout.popup_content_model, (ViewGroup) viewP, false);
        if (type == Enums.TransactionType.MODELINCOME){
            this.listOfElements = isNull(bModel.getModelIncome().getValue()) ? Collections.emptyList() : bModel.getModelIncome().getValue();
        } else if (type == Enums.TransactionType.MODELINVOICE){
            this.listOfElements = isNull(bModel.getModelInvoice().getValue()) ? Collections.emptyList() : bModel.getModelInvoice().getValue();
        } else this.listOfElements = Collections.emptyList();

        initPopup();
    }

    private TextView popupContentModelTitle;
    private LinearLayout popupContentModelBtnClose;
    private Button popupContentModelBtnAdd;

    private void initPopup(){
        popupContentModelTitle = popupLayout.findViewById(R.id.popupContentModelTitle);
        popupContentModelBtnClose = popupLayout.findViewById(R.id.popupContentModelBtnClose);
        RecyclerView popupContentModelListContainer = popupLayout.findViewById(R.id.popupContentModelListContainer);
        popupContentModelBtnAdd = popupLayout.findViewById(R.id.popupContentModelBtnAdd);

        ElementAdapter listAdapter = new ElementAdapter(context, listOfElements, null, null);
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
}
