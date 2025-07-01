package fr.jadeveloppement.budgetsjad.components.popups;

import static java.util.Objects.isNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;
import fr.jadeveloppement.budgetsjad.sqlite.tables.CategoryTable;

public class PopupElementContent extends LinearLayout {

    private Context context;
    private View popupElementContent;
    private View viewParent;
    private Transaction element;

    public PopupElementContent(@NonNull Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
    }

    public PopupElementContent(@NonNull Context c, @NonNull View viewP, @Nullable Transaction el){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.viewParent = viewP;
        this.popupElementContent = LayoutInflater.from(context).inflate(R.layout.popup_content_element, (ViewGroup) viewParent, false);
        this.element = el;

        initPopupContent();
    }

    private TextView popupContentElementTitle, popupContentElementPeriodTv;
    private EditText popupContentElementLabel, popupContentElementAmount;
    private CheckBox popupContentElementIsPaid;
    private Button popupContentElementBtnSave, popupContentElementBtnDelete;
    private LinearLayout popupContentElementBtnClose, popupContentElementPeriodLayout, popupContentElementCategoryLayout;
    private Spinner popupContentElementCategorySpinner;

    private void initPopupContent() {
        popupContentElementTitle = popupElementContent.findViewById(R.id.popupContentElementTitle);
        popupContentElementPeriodTv = popupElementContent.findViewById(R.id.popupContentElementPeriodTv);
        popupContentElementLabel = popupElementContent.findViewById(R.id.popupContentElementLabel);
        popupContentElementAmount = popupElementContent.findViewById(R.id.popupContentElementAmount);
        popupContentElementIsPaid = popupElementContent.findViewById(R.id.popupContentElementIsPaid);
        popupContentElementBtnSave = popupElementContent.findViewById(R.id.popupContentElementBtnSave);
        popupContentElementBtnDelete = popupElementContent.findViewById(R.id.popupContentElementBtnDelete);
        popupContentElementBtnClose = popupElementContent.findViewById(R.id.popupContentElementBtnClose);
        popupContentElementPeriodLayout = popupElementContent.findViewById(R.id.popupContentElementPeriodLayout);
        popupContentElementCategorySpinner = popupElementContent.findViewById(R.id.popupContentElementCategorySpinner);
        popupContentElementCategoryLayout = popupElementContent.findViewById(R.id.popupContentElementCategoryLayout);

        initVisibility();
    }

    private void initVisibility() {
        popupContentElementBtnDelete.setVisibility(!isNull(element) ? View.VISIBLE : View.GONE);
        if (!isNull(element)){
            popupContentElementIsPaid.setVisibility((element.getType() != Enums.TransactionType.INVOICE) ? View.GONE : View.VISIBLE);
        }
        String useCategory = (new Functions(context)).getSettingByLabel(Variables.settingCategory).value;
        List<CategoryTable> categories = (new Functions(context)).getAllCategories();
        popupContentElementCategoryLayout.setVisibility(useCategory.equalsIgnoreCase("0") || categories.isEmpty() ? View.GONE : View.VISIBLE);
        if (useCategory.equalsIgnoreCase("1")){
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories.stream().map(category -> category.label).collect(Collectors.toList()));
            popupContentElementCategorySpinner.setAdapter(arrayAdapter);
        }
    }

    public LinearLayout getPopupContentElementCategoryLayout(){
        return popupContentElementCategoryLayout;
    }

    public LinearLayout getPopupContentElementPeriodLayout(){
        return popupContentElementPeriodLayout;
    }

    public TextView getPopupContentElementTitle(){
        return popupContentElementTitle;
    }

    public TextView getPopupContentElementPeriodTv(){
        return popupContentElementPeriodTv;
    }

    public EditText getPopupContentElementLabel(){
        return popupContentElementLabel;
    }

    public EditText getPopupContentElementAmount(){
        return popupContentElementAmount;
    }

    public CheckBox getPopupContentElementIsPaid(){
        return popupContentElementIsPaid;
    }

    public Button getPopupContentElementBtnSave(){
        return popupContentElementBtnSave;
    }

    public Button getPopupContentElementBtnDelete(){
        return popupContentElementBtnDelete;
    }

    public LinearLayout getPopupContentElementBtnClose(){
        return popupContentElementBtnClose;
    }

    public LinearLayout getLayout(){
        return (LinearLayout) popupElementContent;
    }
}
