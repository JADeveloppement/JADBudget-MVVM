package fr.jadeveloppement.budgetsjad.components.popups;

import static java.lang.Long.parseLong;

import android.content.Context;
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
    private BudgetViewModel budgetViewModel;
    private LiveData<List<Transaction>> elementsToDisplay;
    private View viewParent, popupDisplayElementContent;

    public PopupDisplayTileContent(@NonNull Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
    }

    public PopupDisplayTileContent(@NonNull Context c, @NonNull View viewP, @NonNull LiveData<List<Transaction>> elements, @NonNull BudgetViewModel vModel){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.viewParent = viewP;
        this.popupDisplayElementContent = LayoutInflater.from(context).inflate(R.layout.popup_display_tile_content, (ViewGroup) viewParent, false);
        this.elementsToDisplay = elements;
        this.budgetViewModel = vModel;

        initPopupDisplayContent();
    }

    private ImageView popupDisplayTileContentTitleIcon;
    private TextView popupDisplayTileContentTitleTv, popupDisplayTileContentPeriodTv;
    private LinearLayout popupDisplayTileContentBtnClose;
    private RecyclerView popupDisplayTileContentListContainer;

    private void initPopupDisplayContent() {
        popupDisplayTileContentTitleIcon = popupDisplayElementContent.findViewById(R.id.popupDisplayTileContentTitleIcon);
        popupDisplayTileContentTitleTv = popupDisplayElementContent.findViewById(R.id.popupDisplayTileContentTitleTv);
        popupDisplayTileContentPeriodTv = popupDisplayElementContent.findViewById(R.id.popupDisplayTileContentPeriodTv);
        popupDisplayTileContentBtnClose = popupDisplayElementContent.findViewById(R.id.popupDisplayTileContentBtnClose);
        popupDisplayTileContentListContainer = popupDisplayElementContent.findViewById(R.id.popupDisplayTileContentListContainer);

        ElementAdapter elementAdapter = new ElementAdapter(context, elementsToDisplay.getValue(), budgetViewModel);
        popupDisplayTileContentListContainer.setAdapter(elementAdapter);
        popupDisplayTileContentListContainer.setLayoutManager(new LinearLayoutManager(context));
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
