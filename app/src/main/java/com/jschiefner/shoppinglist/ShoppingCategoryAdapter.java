package com.jschiefner.shoppinglist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingCategoryAdapter extends FirestoreRecyclerAdapter<Category, ShoppingCategoryAdapter.CategoryHolder> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference itemsRef;
    private ItemAdapter adapter;

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
        adapter = new ItemAdapter(options);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.instance));
        holder.recyclerView.setAdapter(adapter);

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

    class CategoryHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        RecyclerView recyclerView;
        Category category;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.items_recycler_view);
            categoryName = itemView.findViewById(R.id.main_category_name);
        }
    }
}
