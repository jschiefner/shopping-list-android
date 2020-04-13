package com.jschiefner.shoppinglist;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.UUID;

@Entity
@TypeConverters(DateConverter.class)
public class Item {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String uuid;

    public String name;
    public Date createdAt;
    public Date updatedAt;

    public Item(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID().toString();

        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
    }
}
