package fr.jadeveloppement.budgetsjad.components.popups;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
import static java.util.Objects.isNull;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.ChartElement;
import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.sqlite.tables.CategoryTable;
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
    private TextView popupDisplayChartTotalTv;

    private void initPopup(){
        popupDisplayChartTotalTv = popupLayout.findViewById(R.id.popupDisplayChartTotalTv);
        btnClose = popupLayout.findViewById(R.id.popupDisplayChartBtnClose);
        popupDisplayChartContentListContainer = popupLayout.findViewById(R.id.popupDisplayChartContentListContainer);

        List<ExpensesTable> listOfExpenses = functions.getAllExpenses();
        List<CategoryTable> allCategories = functions.getAllCategories();

        Map<Long, String> categoryIdToLabelMap = allCategories.stream()
                .filter(category -> !isNull(category.category_id))
                .collect(Collectors.toMap(
                        category -> category.category_id,
                        category -> category.label,
                        (existing, replacement) -> existing
                ));

        double totalAmount = 0;
        Map<Long, Double> aggregatedCategoryAmounts = new HashMap<>();

        for (ExpensesTable expense : listOfExpenses) {
            double currentAmount = parseDouble(expense.amount);
            totalAmount += currentAmount;
            aggregatedCategoryAmounts.merge(expense.category_id, currentAmount, Double::sum);
        }

        popupDisplayChartTotalTv.setText("Total : " + Variables.decimalFormat.format(totalAmount) + " €");
        popupDisplayChartContentListContainer.removeAllViews();

        for (Map.Entry<Long, Double> entry : aggregatedCategoryAmounts.entrySet()) {
            Long categoryId = entry.getKey();
            double amountForCategory = entry.getValue();

            String categoryLabel;
            if (categoryId == null) {
                categoryLabel = "Non catégorisé";
            } else {
                categoryLabel = categoryIdToLabelMap.get(categoryId);
                if (categoryLabel == null || categoryLabel.isBlank()) {
                    categoryLabel = "Catégorie inconnue";
                }
            }

            int percentage = 0;
            if (totalAmount > 0) {
                percentage = (int) ((amountForCategory / totalAmount) * 100);
            }

            ChartElement chartElement = new ChartElement(
                    context,
                    categoryLabel + " (" + Variables.decimalFormat.format(amountForCategory) + " €)",
                    percentage,
                    isNull(categoryId) ? null : categoryId
            );
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
