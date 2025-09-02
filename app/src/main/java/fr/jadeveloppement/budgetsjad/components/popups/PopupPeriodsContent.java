package fr.jadeveloppement.budgetsjad.components.popups;

import static java.util.Objects.isNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.List;

import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.PeriodElement;
import fr.jadeveloppement.budgetsjad.components.popups.interfaces.PeriodsInterface;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;

public class PopupPeriodsContent extends LinearLayout {
    private final Context context;
    private View viewParent;
    private View popupContent;
    private Functions functions;
    private List<PeriodsTable> listOfPeriods;

    private PeriodsInterface periodsInterface;

    private LinearLayout popupContentPeriodsBtnClose, popupContentPeriodsListContainer;

    public PopupPeriodsContent(@NonNull Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
    }

    public PopupPeriodsContent(@NonNull Context c, @NonNull View viewP, PeriodsInterface periodListener){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.functions = new Functions(context);
        this.listOfPeriods = functions.getAllPeriods();
        this.viewParent = viewP;
        this.popupContent = LayoutInflater.from(context).inflate(R.layout.popup_periods_content, (ViewGroup) viewParent, false);
        this.periodsInterface = periodListener;

        initViews();
        initContent();
    }

    private void initViews(){
        popupContentPeriodsBtnClose = popupContent.findViewById(R.id.popupContentPeriodsBtnClose);
        popupContentPeriodsListContainer = popupContent.findViewById(R.id.popupContentPeriodsListContainer);
    }

    private void initContent(){
        popupContentPeriodsListContainer.removeAllViews();
        for(PeriodsTable p : listOfPeriods){
            PeriodElement periodElement = new PeriodElement(context, popupContentPeriodsListContainer, p);
            popupContentPeriodsListContainer.addView(periodElement.getLayout());

            periodElement.getDeleteBtn().setOnClickListener(v -> {
                PopupContainer popupContainer = new PopupContainer(context, viewParent);
                PopupConfirmDeletePeriod popupConfirmDeletePeriod = new PopupConfirmDeletePeriod(context);
                popupContainer.addContent(popupConfirmDeletePeriod.getLayout());

                popupConfirmDeletePeriod.getPopupContentConfirmDeleteBtnConfirm().setOnClickListener(v1 -> {
                    if (!isNull(periodsInterface)) periodsInterface.periodDeleted(p);
                    listOfPeriods = functions.getAllPeriods();
                    popupContainer.closePopup();
                    initContent();
                });

                popupConfirmDeletePeriod.getPopupContentConfirmDeleteBtnCancel().setOnClickListener(v2 -> {
                    popupContainer.closePopup();
                });

                popupConfirmDeletePeriod.getPopupContentConfirmDeleteBtnClose().setOnClickListener(v2 -> {
                    popupContainer.closePopup();
                });
            });
        }
    }

    public LinearLayout getListContainer(){
        return popupContentPeriodsListContainer;
    }

    public LinearLayout getBtnClose(){
        return popupContentPeriodsBtnClose;
    }

    public LinearLayout getLayout(){
        return (LinearLayout) popupContent;
    }
}
