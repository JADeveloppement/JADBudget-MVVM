package fr.jadeveloppement.budgetsjad.components.popups.interfaces;

import fr.jadeveloppement.budgetsjad.sqlite.tables.AccountsTable;

public interface AccountsInterface {
    void accountAdded(AccountsTable a);
    void accountEdited(AccountsTable a);
    void accountDeleted(AccountsTable a);
}
