package fr.jadeveloppement.budgetsjad.components.popups.interfaces;

import fr.jadeveloppement.budgetsjad.sqlite.tables.PeriodsTable;

public interface PeriodsInterface {
    void periodAdded(PeriodsTable p, boolean hasModelInvoice, boolean hasModelIncome);
    void periodDeleted(PeriodsTable p);
}
