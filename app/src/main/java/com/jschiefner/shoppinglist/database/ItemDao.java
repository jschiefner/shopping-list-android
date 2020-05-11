package com.jschiefner.shoppinglist.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemDao {
    @Query("select  * from item")
    LiveData<List<Item>> getAllItems();

    @Query("select * from item where id = :id")
    LiveData<Item> getItem(long id);

    @Query("select * from item where uuid = :uuid")
    Item getItem(String uuid);

    @Query("select * from item where categoryId is null")
    LiveData<List<Item>> getUncategorizedItems();

    @Query("select * from item where completed = 1")
    List<Item> getCompleted();

    @Query("delete from item where completed = 1")
    void deleteCompleted();

    @Insert
    long insert(Item item);

    @Update
    void update(Item item);

    @Query("update item set name = :name, completed = :completed, updatedAt = :updatedAt where uuid = :uuid")
    int update(String uuid, String name, boolean completed, long updatedAt);

    @Delete
    int delete(Item item);

    @Query("delete from item where uuid = :uuid")
    int delete(String uuid);
}
