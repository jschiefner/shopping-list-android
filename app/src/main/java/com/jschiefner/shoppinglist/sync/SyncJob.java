package com.jschiefner.shoppinglist.sync;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import com.jschiefner.shoppinglist.MainActivity;
import com.jschiefner.shoppinglist.PreferenceManager;
import com.jschiefner.shoppinglist.sync.Entity.Action;

public class SyncJob implements Callback {

    private static SyncJob instance;
    private Runnable runnable;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final String BASE_URL = "https://1ed7b55.online-server.cloud/";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();
    public static final int INTERVAL = 1000 * 10;

    private volatile JSONArray entities;

    private static int counter = 0;

    public static SyncJob getInstance() {
        if (instance == null) {
            instance = new SyncJob();
            instance.entities = new JSONArray();
        }
        return instance;
    }

    public SyncJob create(Entity entity) {
        entities.put(entity.toJson(Action.create));
        return getInstance();
    }

    public SyncJob create(Entity entity, UUID categoryId) {
        JSONObject json = entity.toJson(Action.create);
        try {
            json.put("categoryId", categoryId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        entities.put(json);
        return getInstance();
    }

    public SyncJob update(Entity entity) {
        entities.put(entity.toJson(Action.update));
        return getInstance();
    }

    public SyncJob update(Entity entity, UUID categoryId) {
        JSONObject json = entity.toJson(Action.update);
        try {
            json.put("categoryId", categoryId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        entities.put(json);
        return getInstance();
    }

    public SyncJob delete(Entity entity) {
        if (entity != null) entities.put(entity.toJson(Action.delete));
        return getInstance();
    }

    public void perform() {
        Log.i("CUSTOM", "called perform times: " + counter++);

        // TODO: think about better ways as to start as few threads as possible
        handler.removeCallbacks(runnable);
        runnable = () -> {
            Log.i("CUSTOM", "job got triggered");
            // send data to server to server
            request();
        };
        handler.postAtTime(runnable, System.currentTimeMillis() + INTERVAL);
        handler.postDelayed(runnable, INTERVAL);
    }

    private void request() {
        // TODO: don't rely on the MainActivity to be open, it may be stopped while the app is paused
        Request request = new Request.Builder()
                .url(BASE_URL + "shopping/batch")
                .header("Authorization", "Bearer " + PreferenceManager.getInstance(MainActivity.instance).getToken())
                .post(new RequestBody() {
                    @Nullable
                    @Override
                    public MediaType contentType() {
                        return JSON;
                    }

                    @Override
                    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
                        bufferedSink.writeUtf8(entities.toString());
                    }
                }).build();
        client.newCall(request).enqueue(this);
        Log.i("CUSTOM", "request: to /batch");
        Log.i("CUSTOM", entities.toString());
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        Log.i("CUSTOM", "failure for " + call.request().url() + ": " + e.getMessage());
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        Log.i("CUSTOM", "response code for " + call.request().url() + ": " + response.code());
        entities = new JSONArray();
    }
}