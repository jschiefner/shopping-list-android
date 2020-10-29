package com.jschiefner.shoppinglist;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Category {
    private static final String DEFAULT = "Default";

    private String id;
    private String name;
    private List<String> rules;
    private Double position;

    public Category() {
        // needed for firestore
    }

    public Category(String name, List<String> rules) {
        this.name = name;
        this.rules = rules;
        this.position = Double.MAX_VALUE;
    }

    public Category(String name, String... rules) {
        this.name = name;
        this.rules = new ArrayList<>(rules.length);
        this.rules.addAll(Arrays.asList(rules));
        this.position = Double.MAX_VALUE;
    }

    public Category(String name) {
        this.name = name;
        this.rules = new ArrayList<>();
        this.position = Double.MAX_VALUE;
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

    public Double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    @Exclude
    public boolean isDefault() {
        return name.equals(DEFAULT);
    }
}
