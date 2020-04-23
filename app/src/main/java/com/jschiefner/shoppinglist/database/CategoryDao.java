package com.jschiefner.shoppinglist.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface CategoryDao {
    @Query("select * from category")
    LiveData<List<Category>> getAllCategories();

    @Insert
    long insert(Category category);

    @Delete
    void delete(Category category);

    @Transaction
    @Query("select * from category")
    List<CategoryWithRules> getCategoriesWithRules();

    @Transaction
    @Query("select * from category")
    LiveData<List<CategoryWithItems>> getCategoriesWithItems();
}
