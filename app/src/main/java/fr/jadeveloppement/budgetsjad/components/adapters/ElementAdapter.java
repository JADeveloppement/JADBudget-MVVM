package fr.jadeveloppement.budgetsjad.components.adapters;

import static java.lang.Double.parseDouble;
import static java.util.Objects.isNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContainer;
import fr.jadeveloppement.budgetsjad.components.popups.PopupElementContent;
import fr.jadeveloppement.budgetsjad.functions.Enums;
import fr.jadeveloppement.budgetsjad.functions.Functions;
import fr.jadeveloppement.budgetsjad.functions.Variables;
import fr.jadeveloppement.budgetsjad.models.BudgetViewModel;
import fr.jadeveloppement.budgetsjad.models.classes.Transaction;

public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.ViewHolder>{

    private final String TAG = "BudgetJAD";
    private final Context context;
    private final List<Transaction> itemList;
    private final Functions functions;
    private final BudgetViewModel budgetViewModel;

    public ElementAdapter(@NonNull Context c, @Nullable List<Transaction> listOfElements, @Nullable BudgetViewModel vModel){
        this.context = c.getApplicationContext();
        this.itemList = listOfElements;
        this.functions = new Functions(context);
        this.budgetViewModel = vModel;
    }

    @NonNull
    @Override
    public ElementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.budget_element_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementAdapter.ViewHolder holder, int position) {
        Transaction currentItem = itemList.get(position);

        holder.budgetElementLayoutLabel.setText(currentItem.getLabel());
        holder.budgetElementLayoutAmount.setText(Variables.decimalFormat.format(parseDouble(currentItem.getAmount())) + " €");
        holder.budgetElementLayoutPaid.setBackgroundResource(currentItem.getPaid().equalsIgnoreCase("1") ? R.drawable.check : R.drawable.wait);
        holder.budgetElementLayoutPaid.setVisibility(currentItem.getType() == Enums.TransactionType.INVOICE ? View.VISIBLE : View.GONE);
        holder.label = currentItem.getLabel();

        if (!isNull(budgetViewModel)){
            holder.budgetElementLayoutDelete.setOnClickListener(v -> {
                int finalPosition = holder.getAdapterPosition();
                if (finalPosition != RecyclerView.NO_POSITION && finalPosition < itemList.size()) {
                    itemList.remove(finalPosition);
                    notifyItemRemoved(finalPosition);
                    budgetViewModel.deleteTransaction(currentItem);
                }
            });

            holder.budgetElementLayoutPaid.setOnClickListener(v -> {
                int finalPosition = holder.getAdapterPosition();
                currentItem.setPaid(currentItem.getPaid().equalsIgnoreCase("1") ? "0" : "1");
                budgetViewModel.updateTransaction(currentItem);
                holder.isClicked = !holder.isClicked;
                notifyItemChanged(finalPosition);
            });

            holder.budgetElementLayoutEdit.setOnClickListener(v -> {
                int finalPosition = holder.getAdapterPosition();
                PopupContainer popupContainer = new PopupContainer(context, MainActivity.getViewRoot());
                PopupElementContent popupElementContent = new PopupElementContent(context, MainActivity.getViewRoot(), currentItem);

                popupContainer.addContent(popupElementContent.getLayout());

                popupElementContent.getPopupContentElementPeriodTv().setText(Functions.convertStdDateToLocale(currentItem.getDate()));
                String typeStr = "";
                if (currentItem.getType() == Enums.TransactionType.INVOICE){
                    typeStr = "un prélèvement";
                } else if (currentItem.getType() == Enums.TransactionType.INCOME){
                    typeStr = "un revenu";
                } else if (currentItem.getType() == Enums.TransactionType.EXPENSE){
                    typeStr = "une dépense";
                } else if (currentItem.getType() == Enums.TransactionType.MODELINCOME ||
                        currentItem.getType() == Enums.TransactionType.MODELINVOICE){
                    typeStr = "un modèle";
                }

                popupElementContent.getPopupContentElementTitle().setText("Editer " + typeStr);
                popupElementContent.getPopupContentElementLabel().setText(currentItem.getLabel());
                popupElementContent.getPopupContentElementAmount().setText(currentItem.getAmount());

                if (currentItem.getType() == Enums.TransactionType.INVOICE)
                    popupElementContent.getPopupContentElementIsPaid().setChecked(currentItem.getPaid().equalsIgnoreCase("1"));

                popupElementContent.getPopupContentElementBtnClose().setOnClickListener(v1 -> {
                    popupContainer.closePopup();
                });

                popupElementContent.getPopupContentElementBtnSave().setOnClickListener(v1 -> {
                    String editedLabel = popupElementContent.getPopupContentElementLabel().getText().toString();
                    String editedAmount = popupElementContent.getPopupContentElementAmount().getText().toString();
                    String editedPaid = popupElementContent.getPopupContentElementIsPaid().isChecked() ? "1" : "0";
                    if (editedLabel.isBlank() || editedAmount.isBlank()) Toast.makeText(context, "Veuillez renseigner tous les champs SVP.", Toast.LENGTH_LONG).show();
                    else {
                        currentItem.setLabel(editedLabel);
                        currentItem.setAmount(editedAmount);
                        currentItem.setPaid(editedPaid);
                        budgetViewModel.updateTransaction(currentItem);
                        notifyItemChanged(finalPosition);
                        popupContainer.closePopup();
                    }
                });

                popupElementContent.getPopupContentElementBtnDelete().setOnClickListener(v2 -> {
                    if (finalPosition != RecyclerView.NO_POSITION && finalPosition < itemList.size()) {
                        itemList.remove(finalPosition);
                        notifyItemRemoved(finalPosition);
                        budgetViewModel.deleteTransaction(currentItem);
                        popupContainer.closePopup();
                    }
                });
            });

            holder.elementLayoutLabelContainer.setOnClickListener(v -> {
                if (!isNull(budgetViewModel)) toggleElementActionsButtons(holder);
            });
        }
    }

    private void toggleElementActionsButtons(ViewHolder holder){
        holder.isClicked = !holder.isClicked;
        holder.budgetElementLayoutEdit.setVisibility(holder.isClicked ? View.VISIBLE : View.GONE);
        holder.budgetElementLayoutDelete.setVisibility(holder.isClicked ? View.VISIBLE : View.GONE);
        holder.elementLayoutContainer.setBackgroundColor(context.getColor(holder.isClicked ? R.color.orange4 : R.color.white));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void addTransaction(Transaction newModelIncome) {
        budgetViewModel.addTransaction(newModelIncome);
        itemList.add(newModelIncome);
        notifyItemInserted(itemList.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView budgetElementLayoutLabel, budgetElementLayoutAmount;
        protected ImageButton budgetElementLayoutPaid, budgetElementLayoutDelete, budgetElementLayoutEdit;
        protected boolean isClicked = false;
        protected LinearLayout elementLayoutLabelContainer, elementLayoutContainer;
        protected String label = "";

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            budgetElementLayoutLabel = itemView.findViewById(R.id.budgetElementLayoutLabel);
            budgetElementLayoutAmount = itemView.findViewById(R.id.budgetElementLayoutAmount);
            budgetElementLayoutPaid = itemView.findViewById(R.id.budgetElementLayoutPaid);
            budgetElementLayoutDelete = itemView.findViewById(R.id.budgetElementLayoutDelete);
            budgetElementLayoutEdit = itemView.findViewById(R.id.budgetElementLayoutEdit);
            elementLayoutLabelContainer = itemView.findViewById(R.id.elementLayoutLabelContainer);
            elementLayoutContainer = itemView.findViewById(R.id.elementLayoutContainer);

            budgetElementLayoutEdit.setVisibility(View.GONE);
            budgetElementLayoutDelete.setVisibility(View.GONE);
        }
    }
}
