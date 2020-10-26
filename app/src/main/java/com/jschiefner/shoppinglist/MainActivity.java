package com.jschiefner.shoppinglist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;

import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static MainActivity instance;
    private Category category;
    public List<ItemAdapter> itemAdapters = new ArrayList<>();

    private final CollectionReference categoriesRef = FirebaseFirestore.getInstance().collection("categories");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
                if (ShoppingFragment.instance != null) NavHostFragment.findNavController(ShoppingFragment.instance).navigate(R.id.category_fragment);
                return true;
            case R.id.action_delete_completed:
                deleteCompleted();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        instance = this;
    }

    @Override
    public void onPause() {
        super.onPause();
        instance = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        itemAdapters.forEach(FirestoreRecyclerAdapter::stopListening);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setActionBarTitle(int resId) {
        getSupportActionBar().setTitle(resId);
    }

    private void deleteCompleted() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        categoriesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            queryDocumentSnapshots.forEach(documentSnapshot -> {
                documentSnapshot.getReference()
                        .collection("items")
                        .whereEqualTo("completed", true)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots1 -> {
                            if (queryDocumentSnapshots.isEmpty()) return;
                            WriteBatch batch = db.batch(); // TODO: batch everything together instead of batching for each category
                            queryDocumentSnapshots1.forEach(documentSnapshot1 -> batch.delete(documentSnapshot1.getReference()));
                            batch.commit();
                        });
            });
        });
    }
}
