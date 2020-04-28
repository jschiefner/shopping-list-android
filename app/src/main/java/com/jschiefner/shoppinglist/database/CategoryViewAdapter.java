package com.jschiefner.shoppinglist.database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jschiefner.shoppinglist.CategoryFragment;
import com.jschiefner.shoppinglist.MainActivity;
import com.jschiefner.shoppinglist.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.CategoryViewHolder> {
    private List<Category> categories = new ArrayList<>();

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        public Category category;

        public CategoryViewHolder(@NonNull View categoryView) {
            super(categoryView);

            textView = categoryView.findViewById(R.id.recycler_text_view);
        }
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_recycler_view, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, int position) {
        Category currentCategory = categories.get(position);
        holder.textView.setText(currentCategory.name);
        holder.category = currentCategory;
        holder.textView.setOnClickListener(view -> {
            MainActivity mainActivity = (MainActivity) view.getContext();
            mainActivity.setCategoryId(holder.category.id);
            NavHostFragment.findNavController(CategoryFragment.instance).navigate(R.id.rules_fragment);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
