package fr.jadeveloppement.budgetsjad.components.adapters;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
import static java.util.Objects.isNull;

import android.content.Context;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.components.popups.PopupContainer;
import fr.jadeveloppement.budgetsjad.components.popups.PopupElementContent;
import fr.jadeveloppement.budgetsjad.functions.BudgetRequests;
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
    private final boolean isExternal;
    private final ElementAdapterDeleteClickListener callback;

    public interface ElementAdapterDeleteClickListener{
        void elementAdapterDeleteClicked(Transaction t);
    }

    public ElementAdapter(@NonNull Context c, @NonNull List<Transaction> listOfElements, @Nullable BudgetViewModel vModel, @Nullable ElementAdapterDeleteClickListener call, @Nullable Boolean... isExt){
        this.context = c.getApplicationContext();
        this.itemList = listOfElements.isEmpty() ? new ArrayList<>() : listOfElements;
        this.functions = new Functions(context);
        this.budgetViewModel = vModel;
        this.isExternal = !isNull(isExt) && isExt.length > 0 && isExt[0];
        this.callback = call;
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
        String amountValueTxt = Variables.decimalFormat.format(parseDouble(currentItem.getAmount())) + " €";
        if (functions.getSettingByLabel(Variables.settingCategory).value.equalsIgnoreCase("1") && !isNull(currentItem.getCategory()) && !currentItem.getCategory().equalsIgnoreCase("0")){
            amountValueTxt +=  " / " + functions.getCategoryById(parseLong(currentItem.getCategory())).label;
        }
        holder.budgetElementLayoutAmount.setText(amountValueTxt);
        holder.budgetElementLayoutPaid.setBackgroundResource(currentItem.getPaid().equalsIgnoreCase("1") ? R.drawable.check : R.drawable.wait);
        holder.budgetElementLayoutPaid.setVisibility(currentItem.getType() == Enums.TransactionType.INVOICE ? View.VISIBLE : View.GONE);
        holder.label = currentItem.getLabel();

        if (!isNull(budgetViewModel) && !isExternal){
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
                if (currentItem.getType() == Enums.TransactionType.INVOICE) typeStr = "un prélèvement";
                else if (currentItem.getType() == Enums.TransactionType.INCOME) typeStr = "un revenu";
                else if (currentItem.getType() == Enums.TransactionType.EXPENSE) typeStr = "une dépense";
                else if (currentItem.getType() == Enums.TransactionType.MODELINCOME ||
                        currentItem.getType() == Enums.TransactionType.MODELINVOICE){
                    typeStr = "un modèle";
                    popupElementContent.getPopupContentElementCategoryLayout().setVisibility(View.GONE);
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

                        if (functions.getSettingByLabel(Variables.settingCategory).value.equalsIgnoreCase("1") && popupElementContent.getPopupContentElementUseCategory().isChecked()){
                            currentItem.setCategory(popupElementContent.getSelectedCategoryId());
                        } else if (!popupElementContent.getPopupContentElementUseCategory().isChecked()) {
                            currentItem.setCategory("0");
                        }

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
        else if (isExternal){
            holder.budgetElementLayoutDelete.setOnClickListener(v -> {
                if (!isNull(callback)) callback.elementAdapterDeleteClicked(currentItem);
                else Log.d(TAG, "ElementAdapter > onBindViewHolder: interface not implemented");
            });

            holder.elementLayoutLabelContainer.setOnClickListener(v -> {
                toggleElementActionsButtons(holder);
            });
        }
    }

    private void toggleElementActionsButtons(ViewHolder holder){
        holder.isClicked = !holder.isClicked;
        holder.budgetElementLayoutEdit.setVisibility(holder.isClicked && !isExternal ? View.VISIBLE : View.GONE);
        holder.budgetElementLayoutDelete.setVisibility(holder.isClicked ? View.VISIBLE : View.GONE);
        holder.elementLayoutContainer.setBackgroundColor(context.getColor(holder.isClicked ? R.color.orange4 : R.color.white));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void addTransaction(Transaction newModelIncome) {
        budgetViewModel.addTransaction(newModelIncome);
        Log.d(TAG, "addTransaction: size itemList : " + itemList.size());
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
