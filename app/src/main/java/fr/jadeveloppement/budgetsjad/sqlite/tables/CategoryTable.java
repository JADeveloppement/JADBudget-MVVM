package fr.jadeveloppement.budgetsjad.sqlite.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category")
public class CategoryTable {
    @PrimaryKey(autoGenerate = true)
    public long category_id;

    @NonNull
    public String label;

    public CategoryTable(@NonNull String label) {
        this.label = label;
    }

    public long getCategoryId() {
        return category_id;
    }

    public void setCategoryId(int categoryId) {
        this.category_id = categoryId;
    }

    @NonNull
    public String getLabel() {
        return label;
    }

    public void setLabel(@NonNull String label) {
        this.label = label;
    }
}