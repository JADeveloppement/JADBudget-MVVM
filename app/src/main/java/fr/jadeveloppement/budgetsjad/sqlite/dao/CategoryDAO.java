package fr.jadeveloppement.budgetsjad.sqlite.dao;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.jadeveloppement.budgetsjad.sqlite.tables.CategoryTable;

@Dao
public interface CategoryDAO {
    @Query("SELECT * FROM category")
    List<CategoryTable> getAllCategories();

    @Query("SELECT * FROM category WHERE category_id = :id")
    CategoryTable getCategoryById(long id);

    @Insert
    long insertCategory(CategoryTable categoryTable);

    @Update
    void updateCategory(CategoryTable categoryTable);

    @Delete
    void deleteCategory(CategoryTable categoryTable);

    @Query("SELECT * FROM category WHERE label = :label")
    CategoryTable getCategoryByLabel(@NonNull String label);
}
