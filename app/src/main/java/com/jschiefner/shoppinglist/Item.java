package com.jschiefner.shoppinglist;

public class Item {
    private String id;
    private String name;
    private boolean completed;

    public Item() {
        // needed for Firestore
    }

    public Item(String name, boolean completed) {
        this.name = name;
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}