package com.jschiefner.shoppinglist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
import java.util.UUID;

@Dao
public interface ItemDao {
    @Query("select  * from item")
    LiveData<List<Item>> getAllItems();

    @Query("select * from item where id = :id")
    LiveData<Item> getItem(long id);

    @Insert
    long insert(Item item);

    @Delete
    int delete(Item item);
}
