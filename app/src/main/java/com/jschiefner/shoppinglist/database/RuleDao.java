package com.jschiefner.shoppinglist.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface RuleDao {
    @Query("select * from rule")
    LiveData<List<Rule>> getRules();
}
