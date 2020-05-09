package com.jschiefner.shoppinglist.database;

import org.json.JSONObject;

import java.util.Map;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Category implements com.jschiefner.shoppinglist.Entity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    public Category(String name) {
        this.name = name;
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
