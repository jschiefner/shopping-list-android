package com.jschiefner.shoppinglist;
import android.os.AsyncTask;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public class CloudMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        Log.i("CUSTOM", "received item: " + data.toString());
        switch (data.get("action")) {
            case "add":
                add(data);
                break;
            case "update":
                update(data);
                break;
            case "delete":
                delete(data);
                break;
        }
    }

    private void add(Map<String, String> data) {
        ItemDatabase database = ItemDatabase.getInstance(getApplicationContext());
        database.itemDao().insert(new Item(data.get("name"), data.get("id"), data.get("createdAt"), data.get("updatedAt")));
    }

    private void update(final Map<String, String> data) {
        final ItemDatabase database = ItemDatabase.getInstance(getApplicationContext());
        long updatedAt = 0;
        try {
            updatedAt =  Item.dateFormat.parse(data.get("updatedAt")).getTime() / 1000L;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        database.itemDao().update(data.get("id"), data.get("name"), data.get("completed").equals("true"), updatedAt);
    }

    private void delete(Map<String, String> data) {
        ItemDatabase database = ItemDatabase.getInstance(getApplicationContext());
        int deleted = database.itemDao().delete(data.get("id"));
        Log.i("CUSTOM", "deleted with int return: " + deleted);
    }

    @Override
    public void onNewToken(String token) {
        Log.i("CUSTOM", "Refreshed token: " + token);
        PreferenceManager.getInstance(getApplicationContext()).storeToken(token);
    }
}