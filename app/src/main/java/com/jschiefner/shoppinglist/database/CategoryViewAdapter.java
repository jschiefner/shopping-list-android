package com.jschiefner.shoppinglist.database;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.jschiefner.shoppinglist.CategoryFragment;
import com.jschiefner.shoppinglist.MainActivity;
import com.jschiefner.shoppinglist.R;
import com.jschiefner.shoppinglist.ServerAPI;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.CategoryViewHolder> {
    private List<Category> categories = new ArrayList<>();

    public static class CategoryViewHolder extends RecyclerView.ViewHolder implements TextView.OnEditorActionListener, View.OnFocusChangeListener {
        private TextView textView;
        public Category category;

        public CategoryViewHolder(@NonNull View categoryView) {
            super(categoryView);

            textView = categoryView.findViewById(R.id.recycler_text_view);
            textView.setOnEditorActionListener(this);
            textView.setOnFocusChangeListener(this);
        }

        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            Log.i("CUSTOM", textView.getText().toString());
            // edited

            return true;
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) return;

            // focus changed
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
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("(CUSTOM", "clicked!");
                MainActivity mainActivity = (MainActivity) view.getContext();
                mainActivity.setCategoryId(holder.category.id);
                NavHostFragment.findNavController(CategoryFragment.instance).navigate(R.id.rules_fragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
