package com.jschiefner.shoppinglist.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

public class CategoryViewModel extends AndroidViewModel {
    private CategoryRepository repository;
    private LiveData<List<Category>> categories;
    private LiveData<List<CategoryWithItems>> categoriesWithItems;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repository = new CategoryRepository(application);
        categories = repository.getCategories();
        categoriesWithItems = repository.getCategoriesWithItems();
    }

    public void insert(Category category) {
        repository.insert(category);
    }

    public void delete(Category category) {
        repository.delete(category);
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<List<CategoryWithItems>> getCategoriesWithItems() {
        return categoriesWithItems;
    }
}
