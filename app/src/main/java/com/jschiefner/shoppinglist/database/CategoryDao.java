package com.jschiefner.shoppinglist.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface CategoryDao {
    @Query("select * from category")
    LiveData<List<Category>> getAllCategories();

    @Transaction
    @Query("select * from category")
    List<CategoryWithRules> getCategoriesWithRules();
}
