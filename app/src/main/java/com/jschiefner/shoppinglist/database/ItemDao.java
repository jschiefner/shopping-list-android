package com.jschiefner.shoppinglist.database;

import com.jschiefner.shoppinglist.database.Item;

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

    @Query("select * from item where id = :uuid")
    LiveData<Item> getItem(String uuid);

    @Insert
    long insert(Item item);

    @Update
    int update(Item item);

    @Query("update item set name = :name, completed = :completed, updatedAt = :updatedAt where uuid = :uuid")
    int update(String uuid, String name, boolean completed, long updatedAt);

    @Delete
    int delete(Item item);

    @Query("delete from item where uuid = :uuid")
    int delete(String uuid);
}
