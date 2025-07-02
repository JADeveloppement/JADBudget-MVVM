package fr.jadeveloppement.budgetsjad.components.popups;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.adapters.ElementAdapter;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModel;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;

public class PopupDisplayTileContent extends LinearLayout {

    private final String TAG = "BudgetJAD";

    private final Context context;
    private boolean isExternal;
    private BudgetViewModel budgetViewModel;
    private LiveData<List<Transaction>> elementsToDisplay;
    private View popupDisplayElementContent;

    public PopupDisplayTileContent(@NonNull Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
    }

    public PopupDisplayTileContent(@NonNull Context c, @NonNull View viewP, @NonNull LiveData<List<Transaction>> elements, @NonNull BudgetViewModel vModel, @NonNull boolean... isEx){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.popupDisplayElementContent = LayoutInflater.from(context).inflate(R.layout.popup_display_tile_content, (ViewGroup) viewP, false);
        this.elementsToDisplay = elements;
        this.budgetViewModel = vModel;
        this.isExternal = isEx.length > 0 && isEx[0];

        initPopupDisplayContent();
    }

    private ImageView popupDisplayTileContentTitleIcon;
    private TextView popupDisplayTileContentTitleTv, popupDisplayTileContentPeriodTv;
    private LinearLayout popupDisplayTileContentBtnClose, popupDisplayTileContentBtnChart;

    private void initPopupDisplayContent() {
        popupDisplayTileContentTitleIcon = popupDisplayElementContent.findViewById(R.id.popupDisplayTileContentTitleIcon);
        popupDisplayTileContentTitleTv = popupDisplayElementContent.findViewById(R.id.popupDisplayTileContentTitleTv);
        popupDisplayTileContentPeriodTv = popupDisplayElementContent.findViewById(R.id.popupDisplayTileContentPeriodTv);
        popupDisplayTileContentBtnClose = popupDisplayElementContent.findViewById(R.id.popupDisplayTileContentBtnClose);
        popupDisplayTileContentBtnChart = popupDisplayElementContent.findViewById(R.id.popupDisplayTileContentBtnChart);
        RecyclerView popupDisplayTileContentListContainer = popupDisplayElementContent.findViewById(R.id.popupDisplayTileContentListContainer);

        ElementAdapter elementAdapter = new ElementAdapter(context, elementsToDisplay.getValue(), isExternal ? null : budgetViewModel, null);
        popupDisplayTileContentListContainer.setAdapter(elementAdapter);
        popupDisplayTileContentListContainer.setLayoutManager(new LinearLayoutManager(context));
    }

    public LinearLayout getPopupDisplayTileContentBtnChart(){
        return popupDisplayTileContentBtnChart;
    }

    public void setPopupDisplayTileContentPeriodTv(String period){
        popupDisplayTileContentPeriodTv.setText(period);
    }

    public void setPopupDisplayContentTitleIcon(int resource){
        popupDisplayTileContentTitleIcon.setBackgroundResource(resource);
    }

    public void setPopupDisplayContentTitle(String title){
        popupDisplayTileContentTitleTv.setText(title);
    }

    public ImageView getPopupDisplayTileContentTitleIcon(){
        return popupDisplayTileContentTitleIcon;
    }

    public TextView getPopupDisplayTileContentTitleTv(){
        return popupDisplayTileContentTitleTv;
    }

    public LinearLayout getPopupDisplayTileContentBtnClose(){
        return popupDisplayTileContentBtnClose;
    }

    public TextView getPopupDisplayTileContentPeriodTv(){
        return popupDisplayTileContentPeriodTv;
    }

    public View getLayout(){
        return popupDisplayElementContent;
    }
}
