package com.jschiefner.shoppinglist.database;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class CategoryWithRules {
    @Embedded public Category category;

    @Relation(
            parentColumn = "id",
            entityColumn = "categoryId"
    )
    public List<Rule> rules;
}
