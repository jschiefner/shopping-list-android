package com.jschiefner.shoppinglist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CategoryAdapter extends IgnoreChangesFirestoreRecyclerAdapter<Category, CategoryAdapter.CategoryHolder> {
    CategoryAdapter(@NonNull FirestoreRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryHolder holder, int position, @NonNull Category model) {
        model.setId(getSnapshots().getSnapshot(position).getId());
        holder.recylerCategoryButton.setText(model.getName());
        holder.category = model;
        holder.recylerCategoryButton.setOnClickListener(view -> {
            if (model.isDefault()) {
                Toast.makeText(MainActivity.instance, "The Default category cannot have any rules", Toast.LENGTH_SHORT).show();
            } else {
                MainActivity.instance.setCategory(model);
                NavHostFragment.findNavController(CategoryFragment.instance).navigate(R.id.rules_fragment);
            }
        });
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_recycler_view, parent, false);
        return new CategoryHolder(view);
    }

    static class CategoryHolder extends RecyclerView.ViewHolder {
        Button recylerCategoryButton;
        Category category;

        CategoryHolder(@NonNull View itemView) {
            super(itemView);
            recylerCategoryButton = itemView.findViewById(R.id.recycler_category_button);
        }
    }
}
