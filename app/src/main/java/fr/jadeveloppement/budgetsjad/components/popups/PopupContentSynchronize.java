package fr.jadeveloppement.budgetsjad.components.popups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;

public class PopupContentSynchronize extends LinearLayout {

    private Context context;
    private View popupLayout;
    private LinearLayout popupContentSynchronizeBtnClose, popupContentSynchronizeLoadingScreen;
    private TextView popupContentSynchronizeTitle;
    private Button popupContentSynchronizeBtnSave;

    private CheckBox popupContentSynchronizeInvoiceCb, popupContentSynchronizeIncomeCb, popupContentSynchronizeExpenseCb,
            popupContentSynchronizeModelInvoiceCb, popupContentSynchronizeModelIncomeCb;

    public PopupContentSynchronize(@NonNull Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.popupLayout = LayoutInflater.from(context).inflate(R.layout.popup_content_synchronize, (ViewGroup) MainActivity.getViewRoot(), false);

        initPopupContent();
    }

    public void initPopupContent(){
        popupContentSynchronizeTitle = popupLayout.findViewById(R.id.popupContentSynchronizeTitle);
        popupContentSynchronizeBtnClose = popupLayout.findViewById(R.id.popupContentSynchronizeBtnClose);
        popupContentSynchronizeBtnSave = popupLayout.findViewById(R.id.popupContentSynchronizeBtnSave);
        popupContentSynchronizeInvoiceCb = popupLayout.findViewById(R.id.popupContentSynchronizeInvoiceCb);
        popupContentSynchronizeIncomeCb = popupLayout.findViewById(R.id.popupContentSynchronizeIncomeCb);
        popupContentSynchronizeExpenseCb = popupLayout.findViewById(R.id.popupContentSynchronizeExpenseCb);
        popupContentSynchronizeModelInvoiceCb = popupLayout.findViewById(R.id.popupContentSynchronizeModelInvoiceCb);
        popupContentSynchronizeModelIncomeCb = popupLayout.findViewById(R.id.popupContentSynchronizeModelIncomeCb);
        popupContentSynchronizeLoadingScreen = popupLayout.findViewById(R.id.popupContentSynchronizeLoadingScreen);
    }

    public CheckBox getPopupContentSynchronizeInvoiceCb(){
        return popupContentSynchronizeInvoiceCb;
    }

    public CheckBox getPopupContentSynchronizeIncomeCb(){
        return popupContentSynchronizeIncomeCb;
    }

    public CheckBox getPopupContentSynchronizeExpenseCb(){
        return popupContentSynchronizeExpenseCb;
    }

    public CheckBox getPopupContentSynchronizeModelInvoiceCb(){
        return popupContentSynchronizeModelInvoiceCb;
    }

    public CheckBox getPopupContentSynchronizeModelIncomeCb(){
        return popupContentSynchronizeModelIncomeCb;
    }

    public void setTitle(String title){
        popupContentSynchronizeTitle.setText(title);
    }

    public LinearLayout getBtnClose(){
        return popupContentSynchronizeBtnClose;
    }

    public void setBtnSaveLabel(String label){
        popupContentSynchronizeBtnSave.setText(label);
    }

    public Button getBtnSave(){
        return popupContentSynchronizeBtnSave;
    }

    public ConstraintLayout getLayout(){
        return (ConstraintLayout) popupLayout;
    }

    public LinearLayout getLoadingScreen() {
        return popupContentSynchronizeLoadingScreen;
    }
}
