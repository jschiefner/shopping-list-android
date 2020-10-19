package com.jschiefner.shoppinglist;

import com.google.firebase.firestore.Exclude;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Category {
    private String id;
    private String name;
    private List<String> rules;

    public Category() {
        // needed for firestore
    }

    public Category(String name, List<String> rules) {
        this.name = name;
        this.rules = rules;
    }

    public Category(String name, String... rules) {
        this.name = name;
        this.rules = new ArrayList<String>(rules.length);
        this.rules.addAll(Arrays.asList(rules));
    }

    public Category(String name) {
        this.name = name;
        this.rules = new ArrayList<>();
    }

    @Exclude
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

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public void addRule(String rule) {
        rules.add(rule);
    }

    public void deleteRule(String rule) {
        rules.remove(rule);
    }
}
