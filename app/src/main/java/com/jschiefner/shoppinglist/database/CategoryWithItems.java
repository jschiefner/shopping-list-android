package com.jschiefner.shoppinglist.database;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class CategoryWithItems {
    @Embedded public Category category;

    @Relation(
            parentColumn = "id",
            entityColumn = "categoryId"
    )
    public List<Item> items;
}
