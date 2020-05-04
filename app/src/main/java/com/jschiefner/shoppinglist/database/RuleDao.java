package com.jschiefner.shoppinglist.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface RuleDao {
    @Query("select * from rule")
    LiveData<List<Rule>> getRules();

    @Query("select * from rule where categoryId = :categoryId")
    LiveData<List<Rule>> getCategoryRules(long categoryId);

    @Query("select * from rule where name like '%' || :name || '%'")
    Rule getRuleByName(String name);

    @Transaction
    @Query("select * from rule where name like '%' || :name || '%'")
    RuleWithCategory getRuleWithCategory(String name);

    @Insert
    long insert(Rule rule);

    @Delete
    int delete(Rule rule);

    @Query("delete from rule where name like '%' || :name || '%'")
    void delete(String name);
}
