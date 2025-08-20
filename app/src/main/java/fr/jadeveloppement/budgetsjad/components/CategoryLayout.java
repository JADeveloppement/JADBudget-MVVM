package fr.jadeveloppement.budgetsjad.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.sqlite.tables.CategoryTable;

public class CategoryLayout {

    private final Context context;
    private final View categoryLayout;
    private final CategoryTable categoryTable;
    private final TextView categoryLabelTv;
    private final ImageButton categoryDeleteBtn;
    private final ImageButton categoryEditBtn;

    public CategoryLayout(@NonNull Context c, @NonNull CategoryTable cat){
        this.context = c.getApplicationContext();
        this.categoryTable = cat;
        this.categoryLayout = LayoutInflater.from(context).inflate(R.layout.category_layout, (ViewGroup) MainActivity.getViewRoot(), false);
        
        this.categoryLabelTv = categoryLayout.findViewById(R.id.categoryLayoutLabel);
        this.categoryDeleteBtn = categoryLayout.findViewById(R.id.categoryLayoutBtnDelete);
        this.categoryEditBtn = categoryLayout.findViewById(R.id.categoryLayoutBtnEdit);
        
        initLayout();
    }
    
    private void initLayout(){
        categoryLabelTv.setText(categoryTable.label);
    }

    public LinearLayout getLayout(){
        return (LinearLayout) categoryLayout;
    }

    public long getCategoryId(){
        return categoryTable.category_id;
    }

    public String getCategoryLabel(){
        return categoryTable.label;
    }

    public ImageButton getCategoryDeleteBtn(){
        return categoryDeleteBtn;
    }

    public ImageButton getCategoryEditBtn(){
        return categoryEditBtn;
    }
    
    
}
