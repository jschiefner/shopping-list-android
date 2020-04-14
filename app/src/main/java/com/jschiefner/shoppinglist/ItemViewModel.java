package com.jschiefner.shoppinglist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;

public class ItemViewModel extends AndroidViewModel {
    private ItemRepository repository;
    private LiveData<List<Item>> items;

    public ItemViewModel(@NonNull Application application) {
        super(application);
        repository = new ItemRepository(application);
        items = repository.getItems();
    }

    public void insert(Item item) {
        repository.insert(item);
    }

    public void delete(Item item) {
        repository.delete(item);
    }

    public LiveData<List<Item>> getItems() {
        return items;
    }

    public LiveData<Item> getItem(long id) {
        return repository.getItem(id);
    }
}
