package fr.jadeveloppement.budgetsjad.components;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.LinearLayoutCompat;

import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.functions.Functions;

public class AddAccountTile extends LinearLayout {

    private final Context context;
    private final LinearLayout layout;
    private final Functions functions;

    public AddAccountTile(Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.layout = new LinearLayout(context);
        this.functions = new Functions(context);

        initLayout();
    }

    private void initLayout(){
        layout.setBackgroundResource(R.drawable.rounded_box);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        layout.setPadding(
                functions.getDpInPx(8),
                functions.getDpInPx(8),
                functions.getDpInPx(8),
                functions.getDpInPx(8)
        );
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        ImageView iconAdd = new ImageView(context);
        iconAdd.setBackgroundResource(R.drawable.plus);
        LinearLayout.LayoutParams iconParams = new LinearLayoutCompat.LayoutParams(
                functions.getDpInPx(32),
                functions.getDpInPx(32)
        );
        iconAdd.setLayoutParams(iconParams);
        iconAdd.setClickable(false);

        layout.addView(iconAdd);
    }

    public LinearLayout getLayout(){
        return layout;
    }
}
