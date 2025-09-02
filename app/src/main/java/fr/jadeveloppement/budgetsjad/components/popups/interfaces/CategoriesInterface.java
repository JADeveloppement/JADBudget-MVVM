package fr.jadeveloppement.budgetsjad.components.popups.interfaces;

import fr.jadeveloppement.budgetsjad.sqlite.tables.CategoryTable;

public interface CategoriesInterface {
    void categoryAdded(CategoryTable c);
    void categoryDeleted(CategoryTable c);
}
