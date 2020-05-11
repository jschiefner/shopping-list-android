package com.jschiefner.shoppinglist.database;

import android.app.Application;
import android.os.AsyncTask;

import com.jschiefner.shoppinglist.sync.SyncJob;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CategoryRepository {
    private CategoryDao categoryDao;
    private LiveData<List<Category>> categories;
    private LiveData<List<CategoryWithItems>> categoriesWithItems;

    public CategoryRepository(Application application) {
        ItemDatabase database = ItemDatabase.getInstance(application);
        categoryDao = database.categoryDao();
        categories = categoryDao.getAllCategories();
        categoriesWithItems = categoryDao.getCategoriesWithItems();
    }

    public void insert(Category category) {
        new InsertCategoryTask(categoryDao).execute(category);
        SyncJob.getInstance().create(category).perform();
    }

    public void delete(Category category) {
        new DeleteCategoryTask(categoryDao).execute(category);
        SyncJob.getInstance().delete(category).perform();
    }

    public void getCategoryByRuleName(String name, QueryHandler handler) {
        new GetCategoryByRuleNameTask(categoryDao, name, handler).execute();
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<List<CategoryWithItems>> getCategoriesWithItems() {
        return categoriesWithItems;
    }

    private static class InsertCategoryTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao categoryDao;

        private InsertCategoryTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            categoryDao.insert(categories[0]);
            return null;
        }
    }

    private static class DeleteCategoryTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao categoryDao;

        private DeleteCategoryTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            Category category = categories[0];
            categoryDao.delete(category);
            return null;
        }
    }

    private static class GetCategoryByRuleNameTask extends AsyncTask<Void, Void, Void> {
        private CategoryDao categoryDao;
        private String name;
        private QueryHandler handler;
        private Category category;

        private GetCategoryByRuleNameTask(CategoryDao categoryDao, String name, QueryHandler handler) {
            this.categoryDao = categoryDao;
            this.name = name;
            this.handler = handler;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            category = categoryDao.getCategoryByRuleName(name);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            handler.handle(category);
        }
    }
}
