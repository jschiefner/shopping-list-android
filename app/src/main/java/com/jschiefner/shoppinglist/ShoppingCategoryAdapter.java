package com.jschiefner.shoppinglist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.api.Distribution;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.util.CustomClassMapper;

import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingCategoryAdapter extends FirestoreRecyclerAdapter<Category, ShoppingCategoryAdapter.CategoryHolder> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference itemsRef;
    private ItemAdapter adapter;

    private static final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

    public ShoppingCategoryAdapter(@NonNull FirestoreRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryHolder holder, int position, @NonNull Category model) {
        model.setId(getSnapshots().getSnapshot(position).getId());
        holder.category = model;
        holder.categoryName.setText(model.getName());

        // fill recyclerview with model
        itemsRef = db.collection("categories").document(model.getId()).collection("items");
        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(itemsRef, Item.class)
                .build();
        adapter = new ItemAdapter(options, model);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.instance));
        holder.recyclerView.setAdapter(adapter);

        adapter.getSnapshots().addChangeEventListener(holder);
        holder.hide();

        // add swipe helper
        new ItemTouchHelper(new ItemSwipeTouchHelper(ShoppingFragment.instance, adapter, 0, swipeFlags)).attachToRecyclerView(holder.recyclerView);

        // stop listening for all adapters when MainActivity exits
        adapter.startListening();
        MainActivity.instance.itemAdapters.add(adapter);
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_category_card, parent, false);
        return new CategoryHolder(view);
    }

    class CategoryHolder extends RecyclerView.ViewHolder implements ChangeEventListener {
        CardView shoppingCategoryCard;
        TextView categoryName;
        RecyclerView recyclerView;
        Category category;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            shoppingCategoryCard = itemView.findViewById(R.id.shopping_category_card);
            recyclerView = itemView.findViewById(R.id.items_recycler_view);
            categoryName = itemView.findViewById(R.id.main_category_name);

            // set default margins
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) shoppingCategoryCard.getLayoutParams();
            params.setMargins(0, -50, 0, 0);
            shoppingCategoryCard.setLayoutParams(params);
        }

        void show() {
            if (shoppingCategoryCard.getVisibility() == View.VISIBLE) return;

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) shoppingCategoryCard.getLayoutParams();
            params.setMargins(0, 16, 0, 0);
            shoppingCategoryCard.setLayoutParams(params);
            shoppingCategoryCard.setVisibility(View.VISIBLE);
        }

        void hide() {
            if (shoppingCategoryCard.getVisibility() == View.GONE) return;

            shoppingCategoryCard.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) shoppingCategoryCard.getLayoutParams();
            params.setMargins(0, -50, 0, 0);
            shoppingCategoryCard.setLayoutParams(params);
        }

        @Override
        public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
            switch (type) {
                case ADDED: {
                    show();
                    break;
                }
                case REMOVED: {
                    ItemAdapter adapter = (ItemAdapter) recyclerView.getAdapter();
                    if (adapter.isEmpty()) hide();
                    break;
                }
            }
        }

        @Override
        public void onDataChanged() {
            // pass
        }

        @Override
        public void onError(@NonNull FirebaseFirestoreException e) {
            // pass
        }
    }
}
