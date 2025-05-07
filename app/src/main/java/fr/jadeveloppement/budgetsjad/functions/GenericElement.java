package fr.jadeveloppement.budgetsjad.functions;

public class GenericElement {
    public String id;
    public String label;
    public String amount;
    public String period;
    public String paid;
    public String account_id;
    public String typeOfElement;

    public GenericElement(String i, String l, String a, String p, String pd, String ac, String t){
        this.id = i;
        this.label = l;
        this.amount = a;
        this.period = p;
        this.paid = pd;
        this.account_id = ac;
        this.typeOfElement = t;
    }
}
