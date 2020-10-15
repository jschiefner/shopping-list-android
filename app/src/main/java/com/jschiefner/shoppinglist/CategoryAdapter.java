package com.jschiefner.shoppinglist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.concurrent.CancellationException;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends FirestoreRecyclerAdapter<Category, CategoryAdapter.CategoryHolder> {
    public CategoryAdapter(@NonNull FirestoreRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryHolder holder, int position, @NonNull Category model) {
        // get id using getSnapshots().getSnapshot(position).getId()
        model.setId(getSnapshots().getSnapshot(position).getId());
        holder.recylerCategoryButton.setText(model.getName());
        holder.category = model;
        holder.recylerCategoryButton.setOnClickListener(view -> {
            MainActivity.instance.setCategory(model);
            NavHostFragment.findNavController(CategoryFragment.instance).navigate(R.id.rules_fragment);
        });
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_recycler_view, parent, false);
        return new CategoryHolder(view);
    }

    class CategoryHolder extends RecyclerView.ViewHolder {
        Button recylerCategoryButton;
        Category category;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            recylerCategoryButton = itemView.findViewById(R.id.recycler_category_button);
        }
    }
}
