package fr.jadeveloppement.budgetsjad.components;

import static java.util.Objects.isNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContainer;
import fr.jadeveloppement.budgetsjad.components.popups.PopupDisplayElementsByCategory;
import fr.jadeveloppement.budgetsjad.components.popups.PopupDisplayTileContent;
import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;

public class ChartElement extends LinearLayout {

    private final long categoryId;
    private final Enums.TransactionType type;
    private final Functions functions;
    private Context context;
    private final String title;
    private final int progress;
    private final View chartLayout;

    public ChartElement(@NonNull Context c, @NonNull String t, int p, long catId, Enums.TransactionType tp) {
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.title = t;
        this.progress = p;
        this.chartLayout = LayoutInflater.from(context).inflate(R.layout.chart_element, (ViewGroup) MainActivity.getViewRoot(), false);
        this.categoryId = catId;
        this.type = isNull(tp) ? Enums.TransactionType.EXPENSE : tp;
        this.functions = new Functions(context);

        initChartElement();
    }

    private TextView chartTitle;
    private ProgressBar chartProgressbar;

    private LinearLayout chartElementPreviewCategory;

    private void initChartElement(){
        chartTitle = chartLayout.findViewById(R.id.chartElementTitle);
        chartProgressbar = chartLayout.findViewById(R.id.chartElementProgressbar);
        chartElementPreviewCategory = chartLayout.findViewById(R.id.chartElementPreviewCategory);

        chartTitle.setText(this.title);
        chartProgressbar.setProgress(this.progress);

        chartElementPreviewCategory.setOnClickListener(v -> {
            PopupContainer popupContainer = new PopupContainer(context, MainActivity.getViewRoot());
            List<Transaction> listOfElements = Collections.emptyList();
            listOfElements = functions.getAllTransactionByType(type);

            PopupDisplayElementsByCategory popupDisplayElement = new PopupDisplayElementsByCategory(context, listOfElements, categoryId);
            popupContainer.addContent(popupDisplayElement.getLayout());

            popupDisplayElement.btnClose().setOnClickListener(v1 -> {
                popupContainer.closePopup();
            });
        });
    }

    private LinearLayout getChartElementPreviewCategory(){
        return chartElementPreviewCategory;
    }

    public LinearLayout getLayout(){
        return (LinearLayout) chartLayout;
    }
}
