package com.jschiefner.shoppinglist.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class RuleViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private long categoryId;

    public RuleViewModelFactory(Application application, long categoryId) {
        this.application = application;
        this.categoryId = categoryId;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RuleViewModel(application, categoryId);
    }
}
