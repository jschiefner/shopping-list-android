package com.jschiefner.shoppinglist;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

public class Item {
    private String id;

    // TODO: use category to build reference from id and categoryId, woudnt necessarily need the categoy anymore
    private DocumentReference reference;
    private Category category;

    private String name;
    private boolean completed;

    public Item() {
        // needed for Firestore
    }

    public Item(String id, String name, boolean completed) {
        this.id = id;
        this.name = name;
        this.completed = completed;
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

    @Exclude
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public boolean isNew() {
        return name.isEmpty();
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}