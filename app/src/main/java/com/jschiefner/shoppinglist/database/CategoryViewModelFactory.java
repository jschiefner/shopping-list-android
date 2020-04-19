package com.jschiefner.shoppinglist.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CategoryViewModelFactory implements ViewModelProvider.Factory {
    private Application application;

    public CategoryViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CategoryViewModel(application);
    }
}
