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

public class ChartElement extends LinearLayout {

    private Context context;
    private final String title;
    private final int progress;
    private final View chartLayout;

    public ChartElement(@NonNull Context c, @NonNull String t, int p) {
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.title = t;
        this.progress = p;
        this.chartLayout = LayoutInflater.from(context).inflate(R.layout.chart_element, (ViewGroup) MainActivity.getViewRoot(), false);

        initChartElement();
    }

    private TextView chartTitle;
    private ProgressBar chartProgressbar;

    private void initChartElement(){
        chartTitle = chartLayout.findViewById(R.id.chartElementTitle);
        chartProgressbar = chartLayout.findViewById(R.id.chartElementProgressbar);

        chartTitle.setText(this.title);
        chartProgressbar.setProgress(this.progress);
    }

    public LinearLayout getLayout(){
        return (LinearLayout) chartLayout;
    }
}
