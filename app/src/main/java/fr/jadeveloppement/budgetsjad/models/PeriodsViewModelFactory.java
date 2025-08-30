package fr.jadeveloppement.budgetsjad.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PeriodsViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public PeriodsViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PeriodsViewModel.class)) {
            return (T) new PeriodsViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
