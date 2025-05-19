package fr.jadeveloppement.budgetsjad.components;

import static java.util.Objects.isNull;

import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.functions.Functions;

public class DashboardTile extends LinearLayout {

    private final Context context;
    private View layout, viewParent;
    private Functions functions;
    private DashboardTileAddElementClickedInterface listener;

    public interface DashboardTileAddElementClickedInterface{
        void tileAddElementClicked(Enums.TransactionType type);
    }

    public DashboardTile(Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
    }

    public DashboardTile(@NonNull Context c, @NonNull View viewP, @Nullable DashboardTileAddElementClickedInterface l){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.layout = LayoutInflater.from(context).inflate(R.layout.dashboard_tile, (ViewGroup) viewParent, false);
        this.functions = new Functions(context);
        this.listener = l;
        this.viewParent = viewP;

        initLayout();
    }

    private ImageView dashboardTileIcon;
    private TextView dashboardTileTitleTv, dashboardTileAmountTv, dashboardTileProgressbarTv, dashboardTileLastAddedElementTxt;
    private ProgressBar dashboardTileProgressbar;
    private ConstraintLayout dashboardTileProgressbarContainer;
    private LinearLayout dashboardTileLastElementContainer, dashboardTileAddElementContainer, dashboardTileLoading;
    private Enums.TransactionType type = null;

    private void initLayout() {
        dashboardTileIcon = layout.findViewById(R.id.dashboardTileIcon);
        dashboardTileTitleTv = layout.findViewById(R.id.dashboardTileTitleTv);
        dashboardTileAmountTv = layout.findViewById(R.id.dashboardTileAmountTv);
        dashboardTileProgressbar = layout.findViewById(R.id.dashboardTileProgressbar);
        dashboardTileProgressbarContainer = layout.findViewById(R.id.dashboardTileProgressbarContainer);
        dashboardTileLastElementContainer = layout.findViewById(R.id.dashboardTileLastElementContainer);
        dashboardTileAddElementContainer = layout.findViewById(R.id.dashboardTileAddElementContainer);
        dashboardTileProgressbarTv = layout.findViewById(R.id.dashboardTileProgressbarTv);
        dashboardTileLastAddedElementTxt = layout.findViewById(R.id.dashboardTileLastAddedElementTxt);
        dashboardTileLoading = layout.findViewById(R.id.dashboardTileLoading);

        dashboardTileAddElementContainer.setOnClickListener(v -> {
            if (!isNull(listener)) listener.tileAddElementClicked(type);
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(
                0,
                0,
                0,
                functions.getDpInPx((int) context.getResources().getDimension(R.dimen.paddingS))
        );

        layout.setLayoutParams(layoutParams);
    }

    public void setDashboardTileProgressbarProgress(int progress){
        dashboardTileProgressbar.setProgress(progress);
    }

    public void setProgressBarVisible(Boolean visible){
        dashboardTileProgressbarContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setLastElementVisible(Boolean visible){
        dashboardTileLastElementContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setLastElementLabel(String label){
        dashboardTileLastAddedElementTxt.setText(label);
    }

    public void setBtnAddElementVisible(Boolean visible){
        dashboardTileAddElementContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setIcon(int resId){
        dashboardTileIcon.setBackgroundResource(resId);
    }

    public void setTileTitle(String title){
        dashboardTileTitleTv.setText(title);
    }

    public void setTileAmount(String amount){
        dashboardTileAmountTv.setText(amount);
    }

    public View getLayout(){
        return layout;
    }

    public void setTypeTile(Enums.TransactionType t){
        type = t;
    }

    public void setProgressBarText(String s) {
        dashboardTileProgressbarTv.setText(s);
    }

    public LinearLayout getDashboardTileLoading(){
        return dashboardTileLoading;
    }

    public LinearLayout getBtnAddElement(){
        return dashboardTileAddElementContainer;
    }
}
