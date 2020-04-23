package com.jschiefner.shoppinglist.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class RuleViewModel extends AndroidViewModel {
    private RuleRepository repository;
    private LiveData<List<Rule>> categoryRules;

    public RuleViewModel(@NonNull Application application, long categoryId) {
        super(application);
        repository = new RuleRepository(application, categoryId);
        categoryRules = repository.getCategoryRules();
    }

    public void insert(Rule rule) {
        repository.insert(rule);
    }

    public void delete(Rule rule) {
        repository.delete(rule);
    }

    public LiveData<List<Rule>> getCategoryRules() {
        return categoryRules;
    }
}
