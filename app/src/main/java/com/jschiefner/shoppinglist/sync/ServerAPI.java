package com.jschiefner.shoppinglist.sync;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class ServerAPI implements Callback {
    private static final String BASE_URL = "https://1ed7b55.online-server.cloud/";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();
    private static ServerAPI instance;

//    public static ServerAPI getInstance() {
//        if (instance == null) instance = new ServerAPI();
//        return instance;
//    }
//
//    public void addItem(final Item item, Context context) {
//        Log.i("CUSTOM", "sending item: " + item);
//        final String url = BASE_URL + "shopping";
//        Request request = new Request.Builder()
//                .url(url)
//                .header("Authorization", "Bearer " + PreferenceManager.getInstance(context).getToken())
//                .post(new RequestBody() {
//                    @Nullable
//                    @Override
//                    public MediaType contentType() {
//                        return JSON;
//                    }
//
//                    @Override
//                    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
//                        bufferedSink.writeUtf8(item.toJson(Entity.Action.create).toString());
//                    }
//                }).build();
//        client.newCall(request).enqueue(this);
//    }
//
//    public void update(final Item item, Context context) {
//        Log.i("CUSTOM", "updating item: " + item);
//        final String url = BASE_URL + "shopping";
//        Request request = new Request.Builder()
//                .url(url)
//                .header("Authorization", "Bearer " + PreferenceManager.getInstance(context).getToken())
//                .put(new RequestBody() {
//                    @Nullable
//                    @Override
//                    public MediaType contentType() {
//                        return JSON;
//                    }
//
//                    @Override
//                    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
//                        bufferedSink.writeUtf8(item.toJson(Entity.Action.update).toString());
//                    }
//                }).build();
//        client.newCall(request).enqueue(this);
//    }
//
//    public void delete(Item item, Context context) {
//        Log.i("CUSTOM", "deleting item: " + item);
//        final String url = BASE_URL + "shopping/" + item.uuid;
//        Request request = new Request.Builder()
//                .url(url)
//                .header("Authorization", "Bearer " + PreferenceManager.getInstance(context).getToken())
//                .delete()
//                .build();
//        client.newCall(request).enqueue(this);
//    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        Log.i("CUSTOM", "failure for " + call.request().url() + ": " + e.getMessage());
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        Log.i("CUSTOM", "response code for " + call.request().url() + ": " + response.code());
    }
}
