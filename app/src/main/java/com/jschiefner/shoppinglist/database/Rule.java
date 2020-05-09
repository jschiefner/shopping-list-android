package com.jschiefner.shoppinglist.database;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(foreignKeys = @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "categoryId", onDelete = ForeignKey.CASCADE))
@TypeConverters(UUIDConverter.class)
public class Rule implements com.jschiefner.shoppinglist.sync.Entity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public UUID uuid;

    public String name;

    @ColumnInfo(index = true)
    public long categoryId;

    public Rule(String name, long categoryId) {
        this.name = name;
        this.categoryId = categoryId;
        this.uuid = UUID.randomUUID();
    }

    @Override
    public com.jschiefner.shoppinglist.sync.Entity fromMap(Map<String, String> map) {
        return null;
    }

    @Override
    public JSONObject toJson(Action action) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", "rule");
            json.put("action", action.toString());
            json.put("id", uuid);
            json.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
