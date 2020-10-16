package com.jschiefner.shoppinglist;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

public class Item {
    private String id;
    private DocumentReference reference;
    private String name;
    private boolean completed;

    public Item() {
        // needed for Firestore
    }

    public Item(String name, boolean completed) {
        this.name = name;
        this.completed = completed;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public DocumentReference getReference() {
        return reference;
    }

    public void setReference(DocumentReference reference) {
        this.reference = reference;
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