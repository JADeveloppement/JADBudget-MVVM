package fr.jadeveloppement.budgetsjad.components.popups;

import static java.util.Objects.isNull;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.CategoryLayout;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.sqlite.tables.CategoryTable;

public class PopupManageCategories extends LinearLayout {

    private final String TAG = "JADBudget";

    private final Context context;
    private final View popupLayout;
    private final LinearLayout btnClose;
    private final Button btnAddCategory;
    private final LinearLayout layoutAddCategory;
    private final EditText layoutAddCategoryLabel;
    private final Button layoutAddCategorySaveBtn;
    private final LinearLayout popupManageCategoriesListContainer;
    private List<CategoryTable> listOfCategories;
    private final Functions functions;

    public PopupManageCategories(@NonNull Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.popupLayout = LayoutInflater.from(context).inflate(R.layout.popup_manage_categories, (ViewGroup) MainActivity.getViewRoot(), false);
        this.btnClose = popupLayout.findViewById(R.id.popupManageCategoriesBtnClose);
        this.btnAddCategory = popupLayout.findViewById(R.id.popupManageCategoriesBtnAdd);
        this.layoutAddCategory = popupLayout.findViewById(R.id.popupManageCategoriesAddCategoryLayout);
        this.layoutAddCategoryLabel = popupLayout.findViewById(R.id.popupManageCategoriesAddCategoryLabel);
        this.layoutAddCategorySaveBtn = popupLayout.findViewById(R.id.popupManageCategoriesAddCategoryBtnSave);
        this.popupManageCategoriesListContainer = popupLayout.findViewById(R.id.popupManageCategoriesListContainer);
        this.functions = new Functions(context);
    }

    public PopupManageCategories(@NonNull Context c, List<CategoryTable> catList){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.popupLayout = LayoutInflater.from(context).inflate(R.layout.popup_manage_categories, (ViewGroup) MainActivity.getViewRoot(), false);
        this.btnClose = popupLayout.findViewById(R.id.popupManageCategoriesBtnClose);
        this.btnAddCategory = popupLayout.findViewById(R.id.popupManageCategoriesBtnAdd);
        this.layoutAddCategory = popupLayout.findViewById(R.id.popupManageCategoriesAddCategoryLayout);
        this.layoutAddCategoryLabel = popupLayout.findViewById(R.id.popupManageCategoriesAddCategoryLabel);
        this.layoutAddCategorySaveBtn = popupLayout.findViewById(R.id.popupManageCategoriesAddCategoryBtnSave);
        this.popupManageCategoriesListContainer = popupLayout.findViewById(R.id.popupManageCategoriesListContainer);
        this.listOfCategories = isNull(catList) ? Collections.emptyList() : catList;
        this.functions = new Functions(context);

        initPopup();
    }

    private void initPopup(){
        if (!listOfCategories.isEmpty()){
            for(CategoryTable c : listOfCategories){
                CategoryLayout catLayout = new CategoryLayout(context, c);

                popupManageCategoriesListContainer.addView(catLayout.getLayout());

                catLayout.getCategoryDeleteBtn().setOnClickListener(v -> {
                    functions.deleteCategory(c);
                    popupManageCategoriesListContainer.removeView(catLayout.getLayout());
                });

                catLayout.getCategoryEditBtn().setOnClickListener(v -> {
                    Log.d(TAG, "PopupManageCategory > initPopup: edit category > " + c.label);
                });
            }
        }
    }

    public void toggleLayoutAddCategory(){
        layoutAddCategory.setVisibility(layoutAddCategory.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    public Button getBtnAddCategory(){
        return btnAddCategory;
    }

    public EditText getLayoutAddCategoryLabel(){
        return layoutAddCategoryLabel;
    }

    public Button getLayoutAddCategorySaveBtn(){
        return layoutAddCategorySaveBtn;
    }

    public LinearLayout getLayout(){
        return (LinearLayout) popupLayout;
    }

    public LinearLayout getBtnClose(){
        return btnClose;
    }
}
