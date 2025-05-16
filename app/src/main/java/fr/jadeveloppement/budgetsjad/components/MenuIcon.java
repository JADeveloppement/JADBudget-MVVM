package fr.jadeveloppement.budgetsjad.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import fr.jadeveloppement.budgetsjad.R;

public class MenuIcon extends LinearLayout {

    private final Context context;
    private View viewParent;
    private View menuLayout;
    private String menuLabel;
    private int resourceId;

    public MenuIcon(Context c){
        super(c);
        this.context = c;
    }

    public MenuIcon(@NonNull Context c, @NonNull View viewP, @NonNull String label, @NonNull int img){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.menuLabel = label;
        this.resourceId = img;
        this.viewParent = viewP;
        this.menuLayout = LayoutInflater.from(context).inflate(R.layout.menu_icon_layout, (ViewGroup) viewParent, false);

        initLayout();
    }

    private ImageView menuIconImg;
    private TextView menuIconLabel;

    private void initLayout(){
        menuIconImg = menuLayout.findViewById(R.id.menuIconImg);
        menuIconLabel = menuLayout.findViewById(R.id.menuIconLabel);

        menuIconImg.setBackgroundResource(resourceId);
        menuIconLabel.setText(menuLabel);
    }

    public void setMenuLabel(String label){
        menuIconLabel.setText(label);
    }

    public void setMenuIconImg(int res){
        menuIconImg.setBackgroundResource(res);
    }

    public LinearLayout getLayout(){
        return (LinearLayout) menuLayout;
    }
}
