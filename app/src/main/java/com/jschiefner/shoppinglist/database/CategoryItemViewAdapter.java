package com.jschiefner.shoppinglist.database;

import android.content.res.ColorStateList;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jschiefner.shoppinglist.R;
import com.jschiefner.shoppinglist.ShoppingFragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryItemViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Object[] listStore = new Object[0];

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.header_text_view);
        }
    }

    public void update() {
        this.listStore = new Object[getItemCount()];
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        int temp = 0;
        for (CategoryWithItems categoryWithItems : ShoppingFragment.instance.categoriesWithItems) {
            if (categoryWithItems.items.isEmpty()) continue;
            int current = position - temp;
            int next = temp + categoryWithItems.items.size() + 1;
            if (position >= next) {
                temp = next;
                continue;
            }

            if (current == 0) {
                Log.i("CUSTOM", "position: " + position + " header");
                listStore[position] = categoryWithItems.category;
                return R.layout.header_recycler_view;
            } else {
                Log.i("CUSTOM", "position: " + position + " item");
                listStore[position] = categoryWithItems.items.get(current-1);
                return R.layout.item_recycler_view;
            }
         }
        Log.i("CUSTOM", "something went wrong!");
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == R.layout.header_recycler_view) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_recycler_view, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() == R.layout.header_recycler_view) {
            // Header View
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            Category category = (Category) listStore[position];
            holder.textView.setText(category.name);
        } else {
            // Item View
            ItemViewHolder holder = (ItemViewHolder) viewHolder;
            Item item = (Item) listStore[position];
            if (item.completed) {
                SpannableString spannableString = new SpannableString(item.name);
                spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), 0);
                holder.editText.setText(spannableString);
                holder.editText.setTextColor(holder.editText.getContext().getResources().getColor(R.color.gray));
                holder.checkBox.setButtonTintList(ColorStateList.valueOf(holder.editText.getContext().getResources().getColor(R.color.green_gray_checkbox)));
                holder.editText.setBackgroundTintList(ColorStateList.valueOf(holder.editText.getContext().getResources().getColor(R.color.green_gray_checkbox)));
            } else {
                holder.editText.setText(item.name);
                holder.editText.setTextColor(holder.editText.getContext().getResources().getColor(R.color.gray_default_edittext));
                holder.checkBox.setButtonTintList(ColorStateList.valueOf(holder.editText.getContext().getResources().getColor(R.color.gray_default_checkbox)));
                holder.editText.setBackgroundTintList(ColorStateList.valueOf(holder.editText.getContext().getResources().getColor(R.color.colorPrimary)));
            }
            holder.item = item;
            holder.checkBox.setChecked(item.completed);
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        for (CategoryWithItems categoryWithItems : ShoppingFragment.instance.categoriesWithItems) {
            if (categoryWithItems.items.isEmpty()) continue;
            size += 1 + categoryWithItems.items.size();
        }
        return size;
    }
}
