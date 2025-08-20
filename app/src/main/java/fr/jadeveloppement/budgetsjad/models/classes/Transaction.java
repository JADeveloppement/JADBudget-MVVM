package fr.jadeveloppement.budgetsjad.models.classes;

import static java.util.Objects.isNull;

import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.sqlite.tables.ExpensesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.IncomesTable;
import fr.jadeveloppement.budgetsjad.sqlite.tables.InvoicesTable;

public class Transaction {
    private String transaction_id;
    private String label;
    private String amount;
    private String date;
    private String account;
    private String paid;
    private String category ;
    private Enums.TransactionType type;

    /**
     *
     * @param l : label
     * @param a : amount
     * @param d : date
     * @param ac : account_id
     * @param pd : paid
     * @param cat : category
     * @param t : type
     */
    public Transaction(String l, String a, String d, String ac, String pd, String cat, Enums.TransactionType t, String... id){
        this.transaction_id = id.length > 0 ? id[0] : "";
        this.label = l;
        this.amount = a;
        this.date = d;
        this.account = ac;
        this.paid = pd;
        this.category = isNull(cat) || cat.isBlank() ? "0" : cat;
        this.type = t;
    }

    public String getId(){
        return transaction_id.trim();
    }

    public String getLabel(){
        return label.trim();
    }

    public String getAmount(){
        return amount.trim();
    }

    public String getDate(){
        return date.trim();
    }

    public String getAccount(){
        return account.trim();
    }

    public String getPaid(){
        return paid.trim();
    }

    public String getCategory(){
        if (isNull(category) || category.trim().isBlank() || category.trim().equalsIgnoreCase("null") || category.trim().equalsIgnoreCase("-1"))
            return "0";
        else return category.trim();
    }

    public Enums.TransactionType getType(){
        return type;
    }

    public void setLabel(String l){
        label = l.trim();
    }

    public void setAmount(String a){
        amount = a.trim();
    }

    public void setDate(String d){
        date = d.trim();
    }

    public void setPaid(String p){
        paid = p.trim();
    }

    public void setCategory(String cat){
        category = cat.trim();
    }

    public void setType(Enums.TransactionType t){
        type = t;
    }

    public void setAccount(String a){
        account = a;
    }
}
