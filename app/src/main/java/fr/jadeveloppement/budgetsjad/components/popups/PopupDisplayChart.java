package fr.jadeveloppement.budgetsjad.components.popups;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.ChartElement;
import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ExpensesTable;

public class PopupDisplayChart extends LinearLayout {

    private final String TAG = "JADBudget";

    private final Context context;
    private final View popupLayout;
    private final Functions functions;

    public PopupDisplayChart(@NonNull Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.popupLayout = LayoutInflater.from(context).inflate(R.layout.popup_display_chart, (ViewGroup) MainActivity.getViewRoot(), false);
        this.functions = new Functions(context);

        initPopup();
    }

    private LinearLayout btnClose, popupDisplayChartContentListContainer;

    private void initPopup(){
        btnClose = popupLayout.findViewById(R.id.popupDisplayChartBtnClose);
        popupDisplayChartContentListContainer = popupLayout.findViewById(R.id.popupDisplayChartContentListContainer);
        List<ExpensesTable> listOfExpenses = functions.getAllExpenses();
        Set<Long> listOfCategory = listOfExpenses.stream().map(expense -> expense.category_id)
                .filter(category_id -> category_id != null)
                .collect(Collectors.toSet());

        popupDisplayChartContentListContainer.removeAllViews();

        for (Long cat : listOfCategory){
            ChartElement chartElement = new ChartElement(context, functions.getCategoryById(cat).label, 10);
            popupDisplayChartContentListContainer.addView(chartElement.getLayout());
        }
    }

    public LinearLayout btnClose(){
        return btnClose;
    }

    public LinearLayout getLayout() {
        return (LinearLayout) popupLayout;
    }
}
