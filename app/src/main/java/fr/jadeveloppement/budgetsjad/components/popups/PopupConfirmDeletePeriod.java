package fr.jadeveloppement.budgetsjad.components.popups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;

public class PopupConfirmDeletePeriod extends LinearLayout {

    private final Context context;
    private final View popupLayout;

    public PopupConfirmDeletePeriod(@NonNull Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.popupLayout = LayoutInflater.from(context).inflate(R.layout.popup_deleteconfirmation_content, (ViewGroup) MainActivity.getViewRoot(), false);

        initPopup();
    }

    private Button popupContentConfirmDeleteBtnConfirm,
            popupContentConfirmDeleteBtnCancel;

    private LinearLayout popupContentConfirmDeleteBtnClose;

    private void initPopup() {
        popupContentConfirmDeleteBtnClose = popupLayout.findViewById(R.id.popupContentConfirmDeleteBtnClose);
        popupContentConfirmDeleteBtnConfirm = popupLayout.findViewById(R.id.popupContentConfirmDeleteBtnConfirm);
        popupContentConfirmDeleteBtnCancel = popupLayout.findViewById(R.id.popupContentConfirmDeleteBtnCancel);
    }

    public LinearLayout getPopupContentConfirmDeleteBtnClose(){
        return popupContentConfirmDeleteBtnClose;
    }

    public Button getPopupContentConfirmDeleteBtnConfirm(){
        return popupContentConfirmDeleteBtnConfirm;
    }

    public Button getPopupContentConfirmDeleteBtnCancel(){
        return popupContentConfirmDeleteBtnCancel;
    }

    public LinearLayout getLayout(){
        return (LinearLayout) popupLayout;
    }
}
