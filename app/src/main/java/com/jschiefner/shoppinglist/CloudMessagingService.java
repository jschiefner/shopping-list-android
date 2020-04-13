package com.jschiefner.shoppinglist;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
public class CloudMessagingService extends FirebaseMessagingService {
    private static final String TAG = "CloudMessagingService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        Log.d(TAG, String.format("received item. id: %s, name: %s, created: %s, updated: %s", data.get("id"), data.get("name"), data.get("createdAt"), data.get("updatedAt")));
    }
    @Override
    public void onNewToken(String token) {
//        super.onNewToken(token);

        Log.d(TAG, "Refreshed token: " + token);
//        sendRegistrationToServer(token);


    }

}