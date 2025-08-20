package fr.jadeveloppement.budgetsjad.components;

import static java.util.Objects.isNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.functions.Enums;

public class ExternalDataTile extends LinearLayout {

    private final Context context;
    private final View tileLayout;
    private ExternalDataTileClicked callback;

    private TextView manageExternDataTileNbElement, manageExternDataTileTitle;
    private ImageButton manageExternDataTilePreviewElements, manageExternDataTileAddElements;
    private RecyclerView manageExternDataTileListElementsContainer;

    private Enums.TransactionType type;

    public interface ExternalDataTileClicked{
        void externalDataTileAddElementClicked(Enums.TransactionType type);
        void externalDataTilePreviewElementsClicked(ExternalDataTile tile);
    }

    public ExternalDataTile(@NonNull Context c){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.tileLayout = LayoutInflater.from(context).inflate(R.layout.manage_external_data_tile, (ViewGroup) MainActivity.getViewRoot(), false);
        this.type = Enums.TransactionType.UNDEFINED;

        initLayout();
    }

    public ExternalDataTile(@NonNull Context c, Enums.TransactionType type, ExternalDataTileClicked call){
        super(c.getApplicationContext());
        this.context = c.getApplicationContext();
        this.tileLayout = LayoutInflater.from(context).inflate(R.layout.manage_external_data_tile, (ViewGroup) MainActivity.getViewRoot(), false);
        this.type = type;
        this.callback = call;

        initLayout();
    }

    private void initLayout() {
        manageExternDataTileNbElement = tileLayout.findViewById(R.id.manageExternDataTileNbElement);
        manageExternDataTileTitle = tileLayout.findViewById(R.id.manageExternDataTileTitle);
        manageExternDataTilePreviewElements = tileLayout.findViewById(R.id.manageExternDataTilePreviewElements);
        manageExternDataTileAddElements = tileLayout.findViewById(R.id.manageExternDataTileAddElements);
        manageExternDataTileListElementsContainer = tileLayout.findViewById(R.id.manageExternDataTileListElementsContainer);

        setLayoutEvents();
    }

    private void setLayoutEvents(){
        manageExternDataTilePreviewElements.setOnClickListener(v -> {
            if (!isNull(callback)) callback.externalDataTilePreviewElementsClicked(this);
        });

        manageExternDataTileAddElements.setOnClickListener(v ->{
            if (!isNull(callback)) callback.externalDataTileAddElementClicked(type);
        });
    }

    public void setType(Enums.TransactionType t){
        this.type = t;
    }

    public void toggleRecyclerView(){
        if (!isNull(manageExternDataTileListElementsContainer)){
            manageExternDataTileListElementsContainer.setVisibility(manageExternDataTileListElementsContainer.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        }
    }

    public void setTitle(@NonNull String title){
        manageExternDataTileTitle.setText(String.valueOf(title));
    }

    public void setNbElements(@NonNull String nbElements){
        manageExternDataTileNbElement.setText(String.valueOf(nbElements));
    }

    public RecyclerView getManageExternDataTileListElementsContainer(){
        return manageExternDataTileListElementsContainer;
    }

    public ImageButton getManageExternDataTilePreviewElements(){
        return manageExternDataTilePreviewElements;
    }

    public ImageButton getManageExternDataTileAddElements(){
        return manageExternDataTileAddElements;
    }

    public LinearLayout getLayout(){
        return (LinearLayout) tileLayout;
    }

    public int getParentId() {
        return R.id.fragment_ManageExternalInvoiceTile;
    }
}
