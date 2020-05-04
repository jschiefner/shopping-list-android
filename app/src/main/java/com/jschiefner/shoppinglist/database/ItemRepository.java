package com.jschiefner.shoppinglist.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ItemRepository {
    private ItemDao itemDao;
    private CategoryDao categoryDao;
    private RuleDao ruleDao;
    private LiveData<List<Item>> items;

    public ItemRepository(Application application) {
        ItemDatabase database = ItemDatabase.getInstance(application);
        itemDao = database.itemDao();
        categoryDao = database.categoryDao();
        ruleDao = database.ruleDao();
        items = itemDao.getAllItems();
    }

    public void insert(Item item) {
        new InsertItemAsyncTask(itemDao).execute(item);
    }

    public void insert(Item item, Category category, boolean deleteRule, boolean addRule) {
        new InsertItemWithOptionsTask(itemDao, categoryDao, ruleDao, item, category, deleteRule, addRule).execute();
    }

    public void update(Item item) {
        new UpdateItemAsyncTask(itemDao).execute(item);
    }

    public void delete(Item item) {
        new DeleteItemAsyncTask(itemDao).execute(item);
    }

    public void deleteCompleted() {
        new DeleteCompletedTask(itemDao).execute();
    }

    public LiveData<List<Item>> getItems() {
        return items;
    }

    public LiveData<Item> getItem(long id) {
        return itemDao.getItem(id);
    }

    public LiveData<List<Item>> getUncategorizedItems() {
        return itemDao.getUncategorizedItems();
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

    private static class InsertItemWithOptionsTask extends AsyncTask<Void, Void, Void> {
        private ItemDao itemDao;
        private CategoryDao categoryDao;
        private RuleDao ruleDao;
        private Item item;
        private Category category;
        private boolean deleteRule;
        private boolean addRule;

        private InsertItemWithOptionsTask(ItemDao itemDao, CategoryDao categoryDao, RuleDao ruleDao, Item item, Category category, boolean deleteRule, boolean addRule) {
            this.itemDao = itemDao;
            this.categoryDao = categoryDao;
            this.ruleDao = ruleDao;
            this.item = item;
            this.category = category;
            this.deleteRule = deleteRule;
            this.addRule = addRule;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Long categoryId = categoryDao.insert(category);
            item.categoryId = categoryId;
            itemDao.insert(item);
            if (deleteRule) ruleDao.delete(item.name);
            if (addRule) ruleDao.insert(new Rule(item.name, categoryId));
            return null;
        }
    }

    private static class UpdateItemAsyncTask extends AsyncTask<Item, Void, Void> {
        private ItemDao itemDao;

        private UpdateItemAsyncTask(ItemDao itemDao) {
            this.itemDao = itemDao;
        }

        @Override
        protected Void doInBackground(Item... items) {
            itemDao.update(items[0]);
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

    private static class DeleteCompletedTask extends AsyncTask<Void, Void, Void> {
        private ItemDao itemDao;

        private DeleteCompletedTask(ItemDao itemDao) {
            this.itemDao = itemDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            itemDao.deleteCompleted();
            return null;
        }
    }
}
