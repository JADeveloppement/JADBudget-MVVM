package fr.jadeveloppement.budgetsjad.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;

public class PeriodElement extends LinearLayout {

    private final Context context;
    private final View layoutElement;
    private LinearLayout periodElementDeleteBtn;
    private TextView periodElementLabel;
    private PeriodsTable periodsTable;

    public PeriodElement(@NonNull Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.layoutElement = LayoutInflater.from(context).inflate(R.layout.period_element, this, true);
    }

    public PeriodElement(@NonNull Context c, View viewP, PeriodsTable p){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.layoutElement = LayoutInflater.from(context).inflate(R.layout.period_element, this, true);
        this.periodsTable = p;

        initElement();
    }

    private void initElement(){
        periodElementDeleteBtn = layoutElement.findViewById(R.id.periodElementDeleteBtn);
        periodElementLabel = layoutElement.findViewById(R.id.periodElementLabel);

        periodElementLabel.setText(Functions.convertStdDateToLocale(periodsTable.label));
    }

    public LinearLayout getDeleteBtn(){
        return periodElementDeleteBtn;
    }

    public LinearLayout getLayout(){
        return (LinearLayout) layoutElement;
    }
}
