package fr.jadeveloppement.budgetsjad.sqlite.tables;

import static java.util.Objects.isNull;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(
        tableName = "transactions",
        foreignKeys = {
                @ForeignKey(
                        entity = AccountsTable.class,
                        parentColumns = "account_id",
                        childColumns = "account_id",
                        onDelete = ForeignKey.SET_NULL,
                        onUpdate = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = CategoryTable.class,
                        parentColumns = "category_id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.SET_NULL,
                        onUpdate = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = PeriodsTable.class,
                        parentColumns = "period_id",
                        childColumns = "period_id",
                        onDelete = ForeignKey.SET_NULL,
                        onUpdate = ForeignKey.CASCADE
                )
        })
public class TransactionsTable {
    @PrimaryKey(autoGenerate = true)
    public long transaction_id;

    public String label;
    public Double amount;
    public String paid;
    public String type;
    public Long account_id;
    public Long period_id;
    public Long category_id;

    public TransactionsTable(){}

    public TransactionsTable(String l, Double a, String p, String t, Long ac, Long pe, Long c){
        this.label = l;
        this.amount = a;
        this.paid = p;
        this.type = t;
        this.account_id = ac;
        this.period_id = pe;
        this.category_id = c;
    }

    public TransactionsTable(@NonNull String l, Double a, String p, @NonNull String t){
        this.label = l;
        this.amount = isNull(a) ? 0 : amount;
        this.paid = isNull(p) ? "0" : paid;
        this.type = t;
    }

    public TransactionsTable(@NonNull String l, Double a, @NonNull String t){
        this.label = l;
        this.amount = isNull(a) ? 0 : amount;
        this.paid = "0";
        this.type = t;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionsTable t =(TransactionsTable) o;
        return Objects.equals(label, t.label) &&
                Objects.equals(amount, t.amount) &&
                Objects.equals(paid, t.paid) &&
                Objects.equals(type, t.type) &&
                Objects.equals(account_id, t.account_id) &&
                Objects.equals(period_id, t.period_id) &&
                Objects.equals(category_id, t.category_id);
    }

    @NonNull
    @Override
    public String toString(){
        return "Transaction " + this.transaction_id + " > label : " + this.label + " amount : " + this.amount + " paid : " + this.paid + " type : " + this.type + " account_id " + this.account_id + " period_id : " + this.period_id + " category_id : " + this.category_id;
    }
}
