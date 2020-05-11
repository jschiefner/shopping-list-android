package com.jschiefner.shoppinglist;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jschiefner.shoppinglist.database.Category;
import com.jschiefner.shoppinglist.database.Item;
import com.jschiefner.shoppinglist.database.ItemDatabase;
import com.jschiefner.shoppinglist.database.Rule;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class CloudMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String dataString = data.get("data");
        Log.i("CUSTOM", "received item: " + dataString);
        try {
            handle(dataString);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("CUSTOM", "failed parsing json");
        }
    }

    @Override
    public void onNewToken(@NotNull String token) {
        Log.i("CUSTOM", "Refreshed token: " + token);
        PreferenceManager.getInstance(getApplicationContext()).storeToken(token);
    }

    private void handle(String data) throws JSONException {
        JSONArray jsonArray = new JSONArray(data);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            String type = json.getString("type");
            switch (type) {
                case "alexa":
                    handleAlexa(json);
                    break;
                case "item":
                    handleItem(json);
                    break;
                case "category":
                    handleCategory(json);
                    break;
                case "rule":
                    handleRule(json);
                    break;
            }
        }
    }

    private void handleAlexa(JSONObject json) throws JSONException {
        ItemDatabase db = ItemDatabase.getInstance(getApplicationContext());
        Category category = db.categoryDao().getCategoryByRuleName(json.getString("name"));
        Long categoryId = null;
        if (category != null) categoryId = category.id;
        Item item = new Item(json.getString("name"), json.getString("id"), categoryId, false, json.getString("createdAt"), json.getString("updatedAt"));
        db.itemDao().insert(item);
    }

    private void handleItem(JSONObject json) throws JSONException {
        ItemDatabase db = ItemDatabase.getInstance(getApplicationContext());
        String action = json.getString("action");
        switch (action) {
            case "create": {
                Long categoryId = null;
                if (json.has("categoryId")) {
                    Category category = db.categoryDao().getCategory(json.getString("categoryId"));
                    categoryId = category.id;
                }
                Item item = new Item(json.getString("name"), json.getString("id"), categoryId, json.getBoolean("completed"), json.getString("createdAt"), json.getString("updatedAt"));
                db.itemDao().insert(item);
                break;
            }
            case "update": {
                Long categoryId = null;
                if (json.has("categoryId")) {
                    String categoryUUID = json.getString("categoryId");
                    Category category = db.categoryDao().getCategory(categoryUUID);
                    categoryId = category.id;
                }
                Item item = db.itemDao().getItem(json.getString("id"));
                item.update(json.getString("name"), categoryId, json.getBoolean("completed"), json.getString("createdAt"), json.getString("updatedAt"));
                db.itemDao().update(item);
                break;
            }
            case "delete": {
                db.itemDao().delete(json.getString("id"));
                break;
            }
        }
    }

    private void handleCategory(JSONObject json) throws JSONException {
        ItemDatabase db = ItemDatabase.getInstance(getApplicationContext());
        String action = json.getString("action");
        switch (action) {
            case "create": {
                Category category = new Category(json.getString("name"), json.getString("id"));
                db.categoryDao().insert(category);
                break;
            }
            case "delete": {
                db.categoryDao().delete(json.getString("id"));
                break;
            }
        }
    }

    private void handleRule(JSONObject json) throws JSONException {
        ItemDatabase db = ItemDatabase.getInstance(getApplicationContext());
        String action = json.getString("action");
        switch (action) {
            case "create": {
                Category category = db.categoryDao().getCategory(json.getString("categoryId"));
                Rule rule = new Rule(json.getString("name"), category.id, json.getString("id"));
                db.ruleDao().insert(rule);
                break;
            }
            case "delete": {
                db.ruleDao().delete(json.getString("id"));
                break;
            }
        }
    }
}