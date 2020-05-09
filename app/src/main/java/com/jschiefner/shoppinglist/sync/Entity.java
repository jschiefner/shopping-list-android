package com.jschiefner.shoppinglist.sync;

import org.json.JSONObject;

import java.util.Map;

public interface Entity {
    enum Action {create, update, delete};

    public Entity fromMap(Map<String, String> map);

    public JSONObject toJson(Action action);
}
