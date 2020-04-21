package com.jschiefner.shoppinglist.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ItemRepository {
    private ItemDao itemDao;
    private LiveData<List<Item>> items;

    public ItemRepository(Application application) {
        ItemDatabase database = ItemDatabase.getInstance(application);
        itemDao = database.itemDao();
        items = itemDao.getAllItems();
    }

    public void insert(Item item) {
        new InsertItemAsyncTask(itemDao).execute(item);
    }

    public void delete(Item item) {
        new DeleteItemAsyncTask(itemDao).execute(item);
    }

    public LiveData<List<Item>> getItems() {
        return items;
    }

    public LiveData<Item> getItem(long id) {
        return itemDao.getItem(id);
    }

    private static class InsertItemAsyncTask extends AsyncTask<Item, Void, Void> {
        private ItemDao itemDao;

        private InsertItemAsyncTask(ItemDao itemDao) {
            this.itemDao = itemDao;
        }

        @Override
        protected Void doInBackground(Item... items) {
            Item item = items[0];
            Rule rule = ItemDatabase.getInstance().ruleDao().getRuleByName(item.name);
            if (rule != null) item.categoryId = rule.categoryId;
            itemDao.insert(item);
            return null;
        }
    }

    private static class DeleteItemAsyncTask extends AsyncTask<Item, Void, Void> {
        private ItemDao itemDao;

        private DeleteItemAsyncTask(ItemDao itemDao) {
            this.itemDao = itemDao;
        }

        @Override
        protected Void doInBackground(Item... items) {
            itemDao.delete(items[0]);
            return null;
        }
    }
}
