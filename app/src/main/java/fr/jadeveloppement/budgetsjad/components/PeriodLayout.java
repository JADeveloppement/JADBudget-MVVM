package fr.jadeveloppement.budgetsjad.components;

import static java.lang.Long.parseLong;
import static java.util.Objects.isNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;

public class PeriodLayout extends LinearLayout {

    private final Context context;
    private PeriodLayoutSelectionChanged listener;
    private View viewParent;
    private View periodLayout;

    private Functions functions;
    private String oldPeriod = "";

    public interface PeriodLayoutSelectionChanged{
        void periodChanged(String periodSelected);
    }

    public PeriodLayout(@NonNull Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
    }

    public PeriodLayout(@NonNull Context c, @NonNull View viewP, @Nullable PeriodLayoutSelectionChanged l){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.viewParent = viewP;
        this.periodLayout = LayoutInflater.from(context).inflate(R.layout.period_layout, (ViewGroup) viewParent, false);
        this.functions = new Functions(context);
        this.listener = l;
        this.oldPeriod = functions.convertStdDateToLocale(functions.getPeriodById(parseLong(functions.getSettingByLabel(Variables.settingPeriod).value)).label);

        initLayout();
    }

    private Spinner periodLayoutSpinner;
    private LinearLayout periodLayoutBtnAddPeriod;

    private void initLayout(){
        periodLayoutSpinner = periodLayout.findViewById(R.id.periodLayoutSpinner);
        periodLayoutBtnAddPeriod = periodLayout.findViewById(R.id.periodLayoutBtnAddPeriod);

        setSpinner();
    }

    private void setSpinner() {
        List<PeriodsTable> listOfPeriods = functions.getAllPeriods();
        List<String> listOfPeriodLabel = new ArrayList<>();
        for (PeriodsTable p : listOfPeriods)
            listOfPeriodLabel.add(functions.convertStdDateToLocale(p.label));

        ArrayAdapter<String> listPeriodAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, listOfPeriodLabel);
        listPeriodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodLayoutSpinner.setAdapter(listPeriodAdapter);

        int positionPeriodActive = listPeriodAdapter.getPosition(functions.convertStdDateToLocale(functions.getPeriodById(parseLong(functions.getSettingByLabel(Variables.settingPeriod).value)).label));
        periodLayoutSpinner.setSelection(positionPeriodActive);

        setPeriodLayout();
    }

    private void setPeriodLayout() {
        periodLayoutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDate = periodLayoutSpinner.getSelectedItem().toString();
                if (!oldPeriod.equalsIgnoreCase(selectedDate)){
                    if(!isNull(listener)) listener.periodChanged(selectedDate);
                    oldPeriod = selectedDate;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public Spinner getPeriodLayoutSpinner(){
        return periodLayoutSpinner;
    }

    public LinearLayout getPeriodLayoutBtnAddPeriod(){
        return periodLayoutBtnAddPeriod;
    }

    public LinearLayout getLayout(){
        return (LinearLayout) periodLayout;
    }
}
