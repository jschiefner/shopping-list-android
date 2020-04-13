package com.jschiefner.shoppinglist;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.UUID;

@Entity
@TypeConverters({DateConverter.class, UUIDConverter.class})
public class Item {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public UUID uuid;

    public String name;
    public Date createdAt;
    public Date updatedAt;

    public Item(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();

        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
    }
}
