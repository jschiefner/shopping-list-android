package com.jschiefner.shoppinglist.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = { Item.class, Category.class, Rule.class }, version = 6, exportSchema = false)
public abstract class ItemDatabase extends RoomDatabase {
    public static ItemDatabase instance;

    public abstract ItemDao itemDao();
    public abstract CategoryDao categoryDao();
    public abstract RuleDao ruleDao();

    public static synchronized ItemDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ItemDatabase.class, "itemDatabase")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public static synchronized ItemDatabase getInstance() {
        return instance;
    }
}