package com.jschiefner.shoppinglist.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Category {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    public Category(String name) {
        this.name = name;
    }
}
