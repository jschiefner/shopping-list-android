package com.jschiefner.shoppinglist;
import android.content.Intent;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import androidx.room.Room;

public class CloudMessagingService extends FirebaseMessagingService {
    private static final String TAG = "CloudMessagingService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        Log.d(TAG, String.format("received item. id: %s, name: %s, created: %s, updated: %s", data.get("id"), data.get("name"), data.get("createdAt"), data.get("updatedAt")));
        ItemDatabase database = ItemDatabase.getInstance(getApplicationContext());
        database.itemDao().insert(new Item(data.get("name"), data.get("id"), data.get("createdAt"), data.get("updatedAt")));
//        sendBroadcast(new Intent(ShoppingFragment.NOTIFY_SHOPPING_FRAGMENT));
    }
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        PreferenceManager.getInstance(getApplicationContext()).storeToken(token);
    }
}