package com.jschiefner.shoppinglist.database;

import androidx.room.Embedded;
import androidx.room.Relation;

public class RuleWithCategory {
    @Embedded public Rule rule;
    @Relation(
            parentColumn = "categoryId",
            entityColumn = "id"
    )
    public Category category;
}
