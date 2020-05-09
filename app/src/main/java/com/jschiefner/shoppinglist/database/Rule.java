package com.jschiefner.shoppinglist.database;

import org.json.JSONObject;

import java.util.Map;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "categoryId", onDelete = ForeignKey.CASCADE))
public class Rule implements com.jschiefner.shoppinglist.Entity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    @ColumnInfo(index = true)
    public long categoryId;

    public Rule(String name, long categoryId) {
        this.name = name;
        this.categoryId = categoryId;
    }

    @Override
    public com.jschiefner.shoppinglist.Entity fromMap(Map<String, String> map) {
        return null;
    }

    @Override
    public JSONObject toJson(Action action) {
        return null;
    }
}
