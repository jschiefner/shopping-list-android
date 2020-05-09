package com.jschiefner.shoppinglist;

import org.json.JSONObject;

import java.util.Map;

import androidx.annotation.NonNull;

public interface Entity {
    enum Action {create, update, delete};

    public Entity fromMap(Map<String, String> map);

    public JSONObject toJson(Action action);
}
