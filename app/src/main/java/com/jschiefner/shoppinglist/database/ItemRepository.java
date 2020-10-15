package com.jschiefner.shoppinglist.database;

import android.app.Application;
import android.os.AsyncTask;

import com.jschiefner.shoppinglist.sync.Entity;
import com.jschiefner.shoppinglist.sync.SyncJob;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
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

    public void insert(Item item, Category category, Rule ruleToDelete, boolean deleteRule, boolean addRule) {
        new InsertItemWithOptionsTask(itemDao, categoryDao, ruleDao, item, category, ruleToDelete, deleteRule, addRule).execute();
        SyncJob.getInstance().perform();
    }

    public void update(Item item) {
        new UpdateItemAsyncTask(itemDao, categoryDao).execute(item);
        SyncJob.getInstance().perform();
    }

    public void update(Item item, Category category, Rule ruleToDelete, boolean deleteRule, boolean addRule) {
        new UpdateItemWithOptionsTask(itemDao, categoryDao, ruleDao, item, category, ruleToDelete, deleteRule, addRule).execute();
        SyncJob.getInstance().perform();
    }

    public void delete(Item item) {
        new DeleteItemAsyncTask(itemDao).execute(item);
        SyncJob.getInstance().delete(item).perform();
    }

    public void deleteCompleted() {
        new DeleteCompletedTask(itemDao).execute();
        SyncJob.getInstance().perform();
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

    private static class InsertItemWithOptionsTask extends AsyncTask<Void, Void, Void> {
        private ItemDao itemDao;
        private CategoryDao categoryDao;
        private RuleDao ruleDao;
        private Item item;
        private Category category;
        private Rule ruleToDelete;
        private boolean deleteRule;
        private boolean addRule;

        private InsertItemWithOptionsTask(ItemDao itemDao, CategoryDao categoryDao, RuleDao ruleDao, Item item, Category category, Rule ruleToDelete, boolean deleteRule, boolean addRule) {
            this.itemDao = itemDao;
            this.categoryDao = categoryDao;
            this.ruleDao = ruleDao;
            this.item = item;
            this.category = category;
            this.ruleToDelete = ruleToDelete;
            this.deleteRule = deleteRule;
            this.addRule = addRule;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SyncJob syncJob = SyncJob.getInstance();
            if (category != null) {
                Long categoryId = categoryDao.insert(category);
                item.categoryId = categoryId;
                syncJob.create(category).create(item, category.uuid);
            } else if (item.isCategorized()) {
                Category existingCategory = categoryDao.getCategory(item.categoryId);
                category = existingCategory;
                syncJob.create(item, existingCategory.uuid);
            } else syncJob.create(item);
            itemDao.insert(item);
            if (ruleToDelete != null && deleteRule) {
                ruleDao.delete(ruleToDelete);
                syncJob.delete(ruleToDelete);
            }
            if (addRule) {
                Rule ruleToAdd = new Rule(item.name, item.categoryId);
                syncJob.create(ruleToAdd, category.uuid);
                ruleDao.insert(ruleToAdd);
            }
            return null;
        }
    }

    private static class UpdateItemWithOptionsTask extends AsyncTask<Void, Void, Void> {
        private ItemDao itemDao;
        private CategoryDao categoryDao;
        private RuleDao ruleDao;
        private Item item;
        private Category category;
        private Rule ruleToDelete;
        private boolean deleteRule;
        private boolean addRule;

        private UpdateItemWithOptionsTask(ItemDao itemDao, CategoryDao categoryDao, RuleDao ruleDao, Item item, Category category, Rule ruleToDelete, boolean deleteRule, boolean addRule) {
            this.itemDao = itemDao;
            this.categoryDao = categoryDao;
            this.ruleDao = ruleDao;
            this.item = item;
            this.category = category;
            this.ruleToDelete = ruleToDelete;
            this.deleteRule = deleteRule;
            this.addRule = addRule;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SyncJob syncJob = SyncJob.getInstance();
            if (category != null) {
                Long categoryId = categoryDao.insert(category);
                item.categoryId = categoryId;
                syncJob.create(category).update(item, category.uuid);
            } else if (item.isCategorized()) {
                Category existingCategory = categoryDao.getCategory(item.categoryId);
                category = existingCategory;
                syncJob.update(item, existingCategory.uuid);
            } else syncJob.update(item);
            itemDao.update(item);
            if (ruleToDelete != null && deleteRule) {
                ruleDao.delete(ruleToDelete);
                syncJob.delete(ruleToDelete);
            }
            if (addRule) {
                Rule ruleToAdd = new Rule(item.name, item.categoryId);
                syncJob.create(ruleToAdd, category.uuid);
                ruleDao.insert(new Rule(item.name, item.categoryId));
            }
            return null;
        }
    }

    private static class UpdateItemAsyncTask extends AsyncTask<Item, Void, Void> {
        private ItemDao itemDao;
        private CategoryDao categoryDao;

        private UpdateItemAsyncTask(ItemDao itemDao, CategoryDao categoryDao) {
            this.itemDao = itemDao;
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Item... items) {
            Item item = items[0];
            Category category = categoryDao.getCategory(item.categoryId);
            SyncJob syncJob = SyncJob.getInstance();
            if (category != null) syncJob.update(item, category.uuid);
            else syncJob.update(item);
            itemDao.update(item);
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
            List<Item> completed = itemDao.getCompleted();
            SyncJob syncJob = SyncJob.getInstance();
            for (Item item : completed) syncJob.delete(item);
            itemDao.deleteCompleted();
            return null;
        }
    }
}
