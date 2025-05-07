package fr.jadeveloppement.budgetsjad.models;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class BudgetViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public BudgetViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BudgetViewModel.class)) {
            return (T) new BudgetViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
