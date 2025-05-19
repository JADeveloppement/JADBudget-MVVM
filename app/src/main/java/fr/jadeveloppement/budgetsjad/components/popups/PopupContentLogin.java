package fr.jadeveloppement.budgetsjad.components.popups;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import fr.jadeveloppement.budgetsjad.MainActivity;
import fr.jadeveloppement.budgetsjad.R;
import fr.jadeveloppement.budgetsjad.functions.Functions;

public class PopupContentLogin extends LinearLayout {

    private final String TAG = "JADABudget";


    private final Context context;
    private final View viewParent;
    private final View popupLayout;
    private final int typeOfAction;
    private TextView popupContentLoginTitleTv;
    private Button manageloginConnectBtn;
    private Functions functions;

    private LinearLayout popupContentLoginClose, popupContentLoginLoadingScreen;

    private static final String REQUEST_TAG = "LoginRequest"; //tagging the request
    private RequestQueue requestQueue; // Declare RequestQueue as a field of the class.

    public PopupContentLogin(@NonNull Context c){
        super(c);
        this.context = c.getApplicationContext();
        this.viewParent = MainActivity.getViewRoot();
        this.popupLayout = LayoutInflater.from(context).inflate(R.layout.popup_managelogin_layout, (ViewGroup) viewParent, false);
        this.functions = new Functions(context);
        this.typeOfAction = 0;

        initPopup();
    }

    public PopupContentLogin(@NonNull Context c, int t){
        super(c);
        this.context = c.getApplicationContext();
        this.viewParent = MainActivity.getViewRoot();
        this.popupLayout = LayoutInflater.from(context).inflate(R.layout.popup_managelogin_layout, (ViewGroup) viewParent, false);
        this.functions = new Functions(context);
        this.typeOfAction = t;

        initPopup();
    }

    private EditText manageloginLogin, manageloginPassword;
    private CheckBox popupContentLoginInvoiceCb, popupContentLoginIncomeCb, popupContentLoginExpenseCb, popupContentLoginModelInvoiceCb, popupContentLoginModelIncomeCb;

    private void initPopup() {
        popupContentLoginClose = popupLayout.findViewById(R.id.popupContentLoginClose);
        manageloginLogin = popupLayout.findViewById(R.id.manageloginLogin);
        manageloginPassword = popupLayout.findViewById(R.id.manageloginPassword);
        manageloginConnectBtn = popupLayout.findViewById(R.id.manageloginConnectBtn);
        popupContentLoginTitleTv = popupLayout.findViewById(R.id.popupContentLoginTitleTv);
        popupContentLoginLoadingScreen = popupLayout.findViewById(R.id.popupContentLoginLoadingScreen);
        popupContentLoginInvoiceCb = popupLayout.findViewById(R.id.popupContentLoginInvoiceCb);
        popupContentLoginIncomeCb = popupLayout.findViewById(R.id.popupContentLoginIncomeCb);
        popupContentLoginExpenseCb = popupLayout.findViewById(R.id.popupContentLoginExpenseCb);
        popupContentLoginModelInvoiceCb = popupLayout.findViewById(R.id.popupContentLoginModelInvoiceCb);
        popupContentLoginModelIncomeCb = popupLayout.findViewById(R.id.popupContentLoginModelIncomeCb);
    }

    public LinearLayout getPopupContentLoginLoadingScreen(){
        return popupContentLoginLoadingScreen;
    }

    public String getLogin(){
        return manageloginLogin.getText().toString().trim();
    }

    public String getPassword(){
        return manageloginPassword.getText().toString().trim();
    }

    public CheckBox getPopupContentLoginInvoiceCb(){
        return popupContentLoginInvoiceCb;
    }

    public CheckBox getPopupContentLoginIncomeCb(){
        return popupContentLoginIncomeCb;
    }

    public CheckBox getPopupContentLoginExpenseCb(){
        return popupContentLoginExpenseCb;
    }

    public CheckBox getPopupContentLoginModelInvoiceCb(){
        return popupContentLoginModelInvoiceCb;
    }

    public CheckBox getPopupContentLoginModelIncomeCb(){
        return popupContentLoginModelIncomeCb;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUEST_TAG);
        }
    }

    public void setPopupTitle(String title){
        popupContentLoginTitleTv.setText(title);
    }

    public void setPopupBtnText(String text){
        manageloginConnectBtn.setText(text);
    }

    public LinearLayout getBtnClose(){
        return popupContentLoginClose;
    }

    public LinearLayout getLayout(){
        return (LinearLayout) popupLayout;
    }

    public Button getBtnSave() {
        return manageloginConnectBtn;
    }
}
