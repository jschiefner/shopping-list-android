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

    @Query("select category.id, category.name from category join rule on category.id = rule.categoryId where rule.name like '%' || :name || '%'")
    Category getCategoryByRuleName(String name);

    @Insert
    Long insert(Category category);

    @Delete
    void delete(Category category);

    @Transaction
    @Query("select * from category")
    List<CategoryWithRules> getCategoriesWithRules();

    @Transaction
    @Query("select * from category")
    LiveData<List<CategoryWithItems>> getCategoriesWithItems();
}
