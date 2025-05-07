package fr.jadeveloppement.budgetsjad.components.popups;

import static java.util.Objects.isNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.functions.Functions;

public class PopupPeriodContent extends LinearLayout {

    private final Context context;
    private View viewParent, popupPeriodContent;
    private TextView popupContentPeriodPeriodPreview, popupContentPeriodPeriodExistsError;
    private Button popupContentPeriodSaveBtn;
    private CalendarView popupContentPeriodCalendar;
    private LinearLayout popupContentElementBtnClose;
    private Functions functions;

    private CheckBox popupContentPeriodUseModelInvoice, popupContentPeriodUseModelIncome;
    private LinearLayout popupContentPeriodPreviewModelInvoice, popupContentPeriodPreviewModelIncome;

    public PopupPeriodContent(@NonNull Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
    }

    public PopupPeriodContent(@NonNull Context c, View viewP){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.viewParent = viewP;
        this.popupPeriodContent = LayoutInflater.from(context).inflate(R.layout.popup_content_period, (ViewGroup) viewP, false);
        this.functions = new Functions(context);

        initPopupContent();
    }

    private void initPopupContent(){
        popupContentPeriodPeriodPreview = popupPeriodContent.findViewById(R.id.popupContentPeriodPeriodPreview);
        popupContentPeriodSaveBtn = popupPeriodContent.findViewById(R.id.popupContentPeriodSaveBtn);
        popupContentPeriodCalendar = popupPeriodContent.findViewById(R.id.popupContentPeriodCalendar);
        popupContentElementBtnClose = popupPeriodContent.findViewById(R.id.popupContentElementBtnClose);
        popupContentPeriodCalendar = popupPeriodContent.findViewById(R.id.popupContentPeriodCalendar);
        popupContentPeriodUseModelInvoice = popupPeriodContent.findViewById(R.id.popupContentPeriodUseModelInvoice);
        popupContentPeriodUseModelIncome = popupPeriodContent.findViewById(R.id.popupContentPeriodUseModelIncome);
        popupContentPeriodPreviewModelInvoice = popupPeriodContent.findViewById(R.id.popupContentPeriodPreviewModelInvoice);
        popupContentPeriodPreviewModelIncome = popupPeriodContent.findViewById(R.id.popupContentPeriodPreviewModelIncome);

        popupContentPeriodPeriodExistsError = popupPeriodContent.findViewById(R.id.popupContentPeriodPeriodExistsError);
        popupContentPeriodPeriodExistsError.setVisibility(View.GONE);

        String selectedDate = functions.convertStdDateToLocale(Functions.getTodayDate());

        popupContentPeriodPeriodPreview.setText(selectedDate);

        if (!isNull(functions.getPeriodByLabel(functions.convertLocaleDateToStd(selectedDate)))){
            popupContentPeriodPeriodExistsError.setVisibility(View.VISIBLE);
            popupContentPeriodSaveBtn.setAlpha(0.5f);
            popupContentPeriodSaveBtn.setVisibility(View.GONE);
        } else {
            popupContentPeriodPeriodExistsError.setVisibility(View.GONE);
            popupContentPeriodSaveBtn.setAlpha(1f);
            popupContentPeriodSaveBtn.setVisibility(View.VISIBLE);
        }

        setPopupPeriodEvent();
    }

    private void setPopupPeriodEvent() {
        popupContentPeriodCalendar.setOnDateChangeListener((@NonNull CalendarView view, int y, int monthOfYear, int dayOfMonth) -> {
            monthOfYear++;
            String day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
            String month = monthOfYear < 10 ? "0" + monthOfYear : String.valueOf(monthOfYear);
            String year = String.valueOf(y);

            String selectedDate = day + "/" + month + "/" + year;
            if (!isNull(functions.getPeriodByLabel(functions.convertLocaleDateToStd(selectedDate)))){
                popupContentPeriodPeriodExistsError.setVisibility(View.VISIBLE);
                popupContentPeriodSaveBtn.setAlpha(0.5f);
                popupContentPeriodSaveBtn.setVisibility(View.GONE);
            } else {
                popupContentPeriodPeriodExistsError.setVisibility(View.GONE);
                popupContentPeriodSaveBtn.setAlpha(1f);
                popupContentPeriodSaveBtn.setVisibility(View.VISIBLE);
            }
            popupContentPeriodPeriodPreview.setText(selectedDate);
        });
    }

    public CheckBox getPopupContentPeriodUseModelInvoice(){
        return popupContentPeriodUseModelInvoice;
    }

    public CheckBox getPopupContentPeriodUseModelIncome(){
        return popupContentPeriodUseModelIncome;
    }

    public LinearLayout getPopupContentPeriodPreviewModelInvoice(){
        return popupContentPeriodPreviewModelInvoice;
    }

    public LinearLayout getPopupContentPeriodPreviewModelIncome(){
        return popupContentPeriodPreviewModelIncome;
    }

    public LinearLayout getPopupContentElementBtnClose(){
        return popupContentElementBtnClose;
    }

    public TextView getPopupContentPeriodPeriodPreview(){
        return popupContentPeriodPeriodPreview;
    }

    public Button getPopupContentPeriodSaveBtn(){
        return popupContentPeriodSaveBtn;
    }

    public CalendarView getPopupContentPeriodCalendar(){
        return popupContentPeriodCalendar;
    }

    public LinearLayout getLayout(){
        return (LinearLayout) popupPeriodContent;
    }
}
