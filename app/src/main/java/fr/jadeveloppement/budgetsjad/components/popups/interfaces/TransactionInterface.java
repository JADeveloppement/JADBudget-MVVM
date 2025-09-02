package fr.jadeveloppement.budgetsjad.components.popups.interfaces;

import fr.jadeveloppement.budgetsjad.sqlite.tables.TransactionsTable;

public interface TransactionInterface {
    void popupTransactionAdded(TransactionsTable t);
    void popupTransactionEdited(TransactionsTable t);
    void popupTransactionDeleted(TransactionsTable t);
}
