package fr.jadeveloppement.budgetsjad.components;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;

public class AccountTile extends LinearLayout {

    private final Context context;
    private LinearLayout layout;
    private Functions functions;
    private TextView accountLabelTv, accountAmountTv;
    private AccountsTable account;

    public AccountTile(Context c ){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
    }

    public AccountTile(@NonNull Context c, @NonNull AccountsTable a){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.layout = new LinearLayout(context);
        this.functions = new Functions(context);
        this.account = a;

        initLayout();
    }

    private void initLayout() {
        LinearLayout.LayoutParams layoutParams = functions.defaultLayoutParams();
        layoutParams.setMargins(
                0,
                0,
                functions.getDpInPx((int) context.getResources().getDimension(R.dimen.paddingS)),
                0
        );

        layout.setBackgroundResource(R.drawable.rounded_box);
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout accountsInfo = new LinearLayout(context);
        accountsInfo.setOrientation(LinearLayout.VERTICAL);
        accountsInfo.setLayoutParams(layoutParams);
        accountsInfo.setGravity(Gravity.CENTER_VERTICAL);

        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setPadding(
                functions.getDpInPx((int) context.getResources().getDimension(R.dimen.paddingS)),
                functions.getDpInPx((int) context.getResources().getDimension(R.dimen.paddingS)),
                functions.getDpInPx((int) context.getResources().getDimension(R.dimen.paddingS)),
                functions.getDpInPx((int) context.getResources().getDimension(R.dimen.paddingS))
        );

        accountLabelTv = new TextView(context);
        accountLabelTv.setLayoutParams(functions.defaultLayoutParams());
        accountLabelTv.setTextAppearance(android.R.style.TextAppearance_Medium);
        accountLabelTv.setTypeface(Typeface.DEFAULT_BOLD);
        accountLabelTv.setTextColor(context.getColor(R.color.orange1));
        accountLabelTv.setText(account.label);

        accountAmountTv = new TextView(context);
        accountAmountTv.setLayoutParams(functions.defaultLayoutParams());
        accountAmountTv.setTextColor(context.getColor(R.color.black));
        accountAmountTv.setText(account.amount + " €");

        accountsInfo.addView(accountLabelTv);
        accountsInfo.addView(accountAmountTv);
        layout.addView(accountsInfo);
    }

    public void setAccountLabelTv(String label){
        accountLabelTv.setText(label);
    }

    public void setAccountAmountTv(String amount){
        accountAmountTv.setText(amount);
    }

    public LinearLayout getLayout(){
        return layout;
    }

    public void setActive() {
        accountLabelTv.setTextColor(context.getColor(R.color.white));
        accountAmountTv.setTextColor(context.getColor(R.color.white));
        layout.setBackgroundResource(R.drawable.rounded_box_orange1);
    }

    public void setInactive(){
        accountLabelTv.setTextColor(context.getColor(R.color.orange1));
        accountAmountTv.setTextColor(context.getColor(R.color.black));
        layout.setBackgroundResource(R.drawable.rounded_box);
    }

    public AccountsTable getAccount() {
        return account;
    }
}
