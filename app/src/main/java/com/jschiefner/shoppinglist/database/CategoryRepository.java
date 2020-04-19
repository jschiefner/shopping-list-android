package com.jschiefner.shoppinglist.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CategoryRepository {
    private CategoryDao categoryDao;
    private LiveData<List<Category>> categories;

    public CategoryRepository(Application application) {
        ItemDatabase database = ItemDatabase.getInstance(application);
        categoryDao = database.categoryDao();
        categories = categoryDao.getAllCategories();
    }

    public void insert(Category category) {
        new InsertCategoryTask(categoryDao).execute(category);
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
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
}