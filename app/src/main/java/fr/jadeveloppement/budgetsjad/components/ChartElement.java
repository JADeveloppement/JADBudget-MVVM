package fr.jadeveloppement.budgetsjad.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContainer;
import fr.jadeveloppement.budgetsjad.components.popups.PopupDisplayElementsByCategory;
import fr.jadeveloppement.budgetsjad.components.popups.PopupDisplayTileContent;
import fr.jadeveloppement.budgetsjad.functions.Functions;

public class ChartElement extends LinearLayout {

    private final long categoryId;
    private Context context;
    private final String title;
    private final int progress;
    private final View chartLayout;

    public ChartElement(@NonNull Context c, @NonNull String t, int p, long catId) {
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.title = t;
        this.progress = p;
        this.chartLayout = LayoutInflater.from(context).inflate(R.layout.chart_element, (ViewGroup) MainActivity.getViewRoot(), false);
        this.categoryId = catId;

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
            PopupDisplayElementsByCategory popupDisplayElement = new PopupDisplayElementsByCategory(context, (new Functions(context)).getAllExpenses(), categoryId);
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
