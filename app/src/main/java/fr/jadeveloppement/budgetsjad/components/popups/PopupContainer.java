package fr.jadeveloppement.budgetsjad.components.popups;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;

public class PopupContainer {
    private final Context context;
    private View viewParent, popupContainerView;
    private PopupWindow popupWindow;

    public PopupContainer(Context c){
        this.context = c.getApplicationContext();
    }

    public PopupContainer(Context c, View viewP){
        this.context = c.getApplicationContext();
        this.viewParent = viewP;
        this.popupContainerView = LayoutInflater.from(context).inflate(R.layout.popup_container_layout, (ViewGroup) viewParent, false);

        initPopup();
    }

    private LinearLayout popupContentContainer;

    private void initPopup(){
        popupContentContainer = popupContainerView.findViewById(R.id.popupContainerContentContainer);

        popupWindow = new PopupWindow(popupContainerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setOverlapAnchor(true);
        popupWindow.setInputMethodMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.showAsDropDown(MainActivity.getViewRoot(), 0, 0, Gravity.BOTTOM | Gravity.CENTER);
    }

    public LinearLayout getLayoutPopupContainer(){
        return popupContentContainer;
    }

    public void addContent(View v){
        popupContentContainer.removeAllViews();
        popupContentContainer.addView(v);
    }

    public void closePopup() {
        popupWindow.dismiss();
    }
}
