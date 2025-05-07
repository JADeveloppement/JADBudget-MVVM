package fr.jadeveloppement.budgetsjad.components.popups;

import static java.lang.Long.parseLong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

import java.util.List;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.AccountTile;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;

public class PopupAccountsContent extends LinearLayout {

    private final Context context;
    private View popupContent;
    private List<AccountsTable> listOfAccounts;
    private Functions functions;
    private LinearLayout popupContentManageAccountsListContainer, popupContentAccountsBtnClose;

    public PopupAccountsContent(@NonNull Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
    }

    public PopupAccountsContent(@NonNull Context c, @NonNull View viewP) {
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.functions = new Functions(context);
        this.listOfAccounts = functions.getAllAccounts();
        this.popupContent = LayoutInflater.from(context).inflate(R.layout.popup_accounts_layout, (ViewGroup) viewP, false);

        initViews();
        initPopupAccounts();
    }

    private void initViews(){
        popupContentManageAccountsListContainer = popupContent.findViewById(R.id.popupContentManageAccountsListContainer);
        popupContentAccountsBtnClose = popupContent.findViewById(R.id.popupContentAccountsBtnClose);
    }

    private void initPopupAccounts() {
        popupContentManageAccountsListContainer.removeAllViews();

        AccountsTable activeAccount = (new Functions(context)).getAccountById(parseLong((new Functions(context)).getSettingByLabel(Variables.settingAccount).value));
        for (AccountsTable a : listOfAccounts){
            AccountTile accountTile = new AccountTile(context, a);
            accountTile.setInactive();
            accountTile.setAccountLabelTv(a.label);
            accountTile.setAccountAmountTv(a.amount);

            LinearLayout.LayoutParams accountParams = new LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            accountParams.setMargins(
                    0,
                    (new Functions(context)).getDpInPx(8),
                    0,
                    (new Functions(context)).getDpInPx(8)
            );
            accountTile.getLayout().setLayoutParams(accountParams);
            if (a.account_id == activeAccount.account_id)
                accountTile.setActive();

            accountTile.getLayout().setOnClickListener(v -> {
                PopupContainer popupContainerEditAccount = new PopupContainer(context, MainActivity.getViewRoot());
                PopupAccountContent popupAccountContent = new PopupAccountContent(context, MainActivity.getViewRoot(), a);
                popupContainerEditAccount.addContent(popupAccountContent.getLayout());
                popupAccountContent.setPopupTitle("Gérer un compte");
                popupAccountContent.setPopupIcon(R.drawable.account_card);

                if (a.account_id == activeAccount.account_id || listOfAccounts.size() == 1)
                    popupAccountContent.getBtnDelete().setVisibility(View.GONE);

                popupAccountContent.getBtnSave().setOnClickListener(v2 -> {
                    String label = popupAccountContent.getPopupContentAccountLabel().getText().toString();
                    String amount = popupAccountContent.getPopupContentAccountAmount().getText().toString();
                    if (label.isBlank() || amount.isBlank()) Toast.makeText(context, "Veuillez renseigner tous les champs", Toast.LENGTH_LONG).show();
                    else {
                        a.label = label;
                        a.amount = amount;
                        functions.updateAccount(a);

                        listOfAccounts = functions.getAllAccounts();

                        initPopupAccounts();
                        popupContainerEditAccount.closePopup();
                    }
                });

                popupAccountContent.getBtnClose().setOnClickListener(v2 -> {
                    popupContainerEditAccount.closePopup();
                });
            });

            popupContentManageAccountsListContainer.addView(accountTile.getLayout());
        }
    }

    public LinearLayout getBtnClose(){
        return popupContentAccountsBtnClose;
    }

    public LinearLayout getLayout(){
        return (LinearLayout) popupContent;
    }
}
