package fr.jadeveloppement.budgetsjad.components.popups;

import static java.lang.Long.parseLong;
import static java.util.Objects.isNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ExpensesTable;

public class PopupDisplayElementsByCategory {

    private final Context context;
    private final List<Transaction> listOfTransaction;
    private final Long categoryId;
    private final View popupLayout;
    private final Functions functions;
    private final String titlePopup;

    public PopupDisplayElementsByCategory(@NonNull Context c, List<Transaction> list, Long category){
        this.context = c.getApplicationContext();
        this.listOfTransaction = list;
        this.categoryId = category;
        this.popupLayout = LayoutInflater.from(context).inflate(R.layout.popup_display_elementsbycategory, (ViewGroup) MainActivity.getViewRoot(), false);
        this.functions = new Functions(context);
        this.titlePopup = isNull(categoryId) || categoryId <= 0 ? "Catégorie inconnue" : functions.getCategoryById(categoryId).label;

        initPopup();
    }

    private void initPopup() {
        ((TextView) popupLayout.findViewById(R.id.popupDisplayElementsByCategoryTitle)).setText(titlePopup);
        LinearLayout popupDisplayElementsByCategoryListContainer = popupLayout.findViewById(R.id.popupDisplayElementsByCategoryListContainer);

        popupDisplayElementsByCategoryListContainer.removeAllViews();
        for (Transaction t : listOfTransaction){
            if (!isNull(t.getCategory()) && parseLong(t.getCategory()) == categoryId){
                TextView label = new TextView(context);

                LinearLayout.LayoutParams tParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

                tParams.setMargins(
                        0,
                        functions.getDpInPx(8),
                        0,
                        functions.getDpInPx(8)
                );

                label.setLayoutParams(tParams);

                label.setText(t.getLabel() + " - " + t.getAmount() + " €");
                popupDisplayElementsByCategoryListContainer.addView(label);
            }
        }
    }

    public LinearLayout btnClose(){
        return popupLayout.findViewById(R.id.popupDisplayElementsByCategoryBtnClose);
    }

    public View getLayout() {
        return popupLayout;
    }
}
