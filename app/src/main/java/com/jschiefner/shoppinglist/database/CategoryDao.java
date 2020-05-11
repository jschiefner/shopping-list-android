package com.jschiefner.shoppinglist.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public interface CategoryDao {
    @Query("select * from category")
    LiveData<List<Category>> getAllCategories();

    @Query("select category.id, category.uuid, category.name from category join rule on category.id = rule.categoryId where rule.name like '%' || :name || '%'")
    Category getCategoryByRuleName(String name);

    @Query("select * from category where id = :id")
    Category getCategory(Long id);

    @Query("select * from category where uuid = :uuid")
    Category getCategory(String uuid);

    @Query("delete from category where uuid = :uuid")
    void delete(String uuid);

    @Insert
    Long insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Transaction
    @Query("select * from category")
    List<CategoryWithRules> getCategoriesWithRules();

    @Transaction
    @Query("select * from category")
    LiveData<List<CategoryWithItems>> getCategoriesWithItems();
}
