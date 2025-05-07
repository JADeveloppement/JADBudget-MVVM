package fr.jadeveloppement.budgetsjad.components.popups;

import static java.util.Objects.isNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;

public class PopupAccountContent extends LinearLayout {
    private final Context context;
    private View viewParent;
    private View popupAccountContent;
    private AccountsTable account;

    private LinearLayout popupContentAccountBtnClose;
    private TextView popupContentAccountTitle;
    private EditText popupContentAccountLabel, popupContentAccountAmount;
    private Button popupContentAccountBtnSave, popupContentAccountBtnDelete;
    private ImageView popupContentAccountIconTitle;

    public PopupAccountContent(Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
    }

    public PopupAccountContent(Context c, View viewP, AccountsTable a){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.viewParent = viewP;
        this.popupAccountContent = LayoutInflater.from(context).inflate(R.layout.popup_content_account, (ViewGroup) viewParent, false);
        this.account = a;

        initPopupContent();
    }

    private void initPopupContent(){
        popupContentAccountBtnClose = popupAccountContent.findViewById(R.id.popupContentAccountBtnClose);
        popupContentAccountTitle = popupAccountContent.findViewById(R.id.popupContentAccountTitle);
        popupContentAccountLabel = popupAccountContent.findViewById(R.id.popupContentAccountLabel);
        popupContentAccountAmount = popupAccountContent.findViewById(R.id.popupContentAccountAmount);
        popupContentAccountBtnSave = popupAccountContent.findViewById(R.id.popupContentAccountBtnSave);
        popupContentAccountBtnDelete = popupAccountContent.findViewById(R.id.popupContentAccountBtnDelete);
        popupContentAccountIconTitle = popupAccountContent.findViewById(R.id.popupContentAccountIconTitle);

        if (!isNull(account)) setContentValues();
        else popupContentAccountBtnDelete.setVisibility(View.GONE);
    }

    private void setContentValues(){
        popupContentAccountLabel.setText(account.label);
        popupContentAccountAmount.setText(account.amount);
    }

    public void setPopupTitle(String title){
        popupContentAccountTitle.setText(title);
    }

    public void setPopupIcon(int resource){
        popupContentAccountIconTitle.setBackgroundResource(resource);
    }

    public LinearLayout getBtnClose(){
        return popupContentAccountBtnClose;
    }

    public Button getBtnSave(){
        return popupContentAccountBtnSave;
    }

    public Button getBtnDelete(){
        return popupContentAccountBtnDelete;
    }

    public EditText getPopupContentAccountLabel(){
        return popupContentAccountLabel;
    }

    public EditText getPopupContentAccountAmount(){
        return popupContentAccountAmount;
    }

    public LinearLayout getLayout(){
        return (LinearLayout) popupAccountContent;
    }
}
