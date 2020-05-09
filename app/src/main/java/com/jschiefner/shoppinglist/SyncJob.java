package com.jschiefner.shoppinglist;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import com.jschiefner.shoppinglist.Entity.Action;

public class SyncJob implements Callback {

    private static SyncJob instance;
    private Runnable runnable;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final String BASE_URL = "https://1ed7b55.online-server.cloud/";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();

    private JSONArray entities;

    private static int counter = 0;

    public static SyncJob getInstance() {
        if (instance == null) {
            instance = new SyncJob();
            instance.entities = new JSONArray();
        }
        return instance;
    }

    public void create(Entity entity) {
        call(Action.create, entity);
    }

    public void update(Entity entity) {
        call(Action.update, entity);
    }

    public void delete(Entity entity) {
        call(Action.delete, entity);
    }


    public void call(Action action, Entity... newEntities) {
        Log.i("CUSTOM", "called times: " + counter++);

        for (Entity entity : newEntities) {
            if (entity != null) entities.put(entity.toJson(action));
        }

        // All database ojects implement Entity Interface
        // ezpz

        // add item with action to list

        // TODO: think about better ways as to start as few threads as possible
        handler.removeCallbacks(runnable);
        runnable = () -> {
            Log.i("CUSTOM", "triggered");
            // send data to server to server
            request();

            entities = new JSONArray();
        };
        int interval = 5000;
        handler.postAtTime(runnable, System.currentTimeMillis() + interval);
        handler.postDelayed(runnable, interval);
    }

    private void request() {
        // TODO: dont rely on the MainActivity to be open, it may be stopped while the app is paused
//        Request request = new Request.Builder()
//                .url(BASE_URL + "batch")
//                .header("Authorization", "Bearer " + PreferenceManager.getInstance(MainActivity.instance).getToken())
//                .put(new RequestBody() {
//                    @Nullable
//                    @Override
//                    public MediaType contentType() {
//                        return JSON;
//                    }
//
//                    @Override
//                    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
//                        bufferedSink.writeUtf8(entities.toString());
//                    }
//                }).build();
//        client.newCall(request).enqueue(this);
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
    }
}
