package com.jschiefner.shoppinglist.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "categoryId", onDelete = ForeignKey.CASCADE))
public class Rule {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    @ColumnInfo(index = true)
    public long categoryId;
}
