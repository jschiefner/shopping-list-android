package com.jschiefner.shoppinglist.database;

import android.annotation.SuppressLint;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Entity(foreignKeys = @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "categoryId"))
@TypeConverters({DateConverter.class, UUIDConverter.class})
public class Item {
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("y/M/d H:m:s");

    @PrimaryKey(autoGenerate = true)
    public long id;
    public UUID uuid;

    @ColumnInfo(index = true)
    public Long categoryId;

    public String name;
    public boolean completed;
    public Date createdAt;
    public Date updatedAt;

    public Item(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();
        this.completed = false;

        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public Item(String name, String uuid, String createdAt, String updatedAt) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
        try {
            this.createdAt = dateFormat.parse(createdAt);
            this.updatedAt = dateFormat.parse(updatedAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void toggle(boolean completed) {
        this.completed = completed;
        this.updatedAt = new Date();
        ItemDatabase.getInstance().itemDao().update(this);
    }

    public void update(String newName) {
        this.name = newName;
        this.updatedAt = new Date();
        ItemDatabase.getInstance().itemDao().update(this);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", uuid);
            json.put("name", name);
            json.put("completed", completed);
            json.put("createdAt", dateFormat.format(createdAt));
            json.put("updatedAt", dateFormat.format(updatedAt));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("<Item> name: %s, id: %s", name, uuid);
    }
}
