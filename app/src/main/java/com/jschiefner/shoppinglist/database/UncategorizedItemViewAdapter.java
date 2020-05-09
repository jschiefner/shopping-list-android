package com.jschiefner.shoppinglist.database;

import android.content.res.ColorStateList;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jschiefner.shoppinglist.R;
import com.jschiefner.shoppinglist.ShoppingFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UncategorizedItemViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = ShoppingFragment.instance.uncategorizedItems.get(position);
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

    @Override
    public int getItemCount() {
        return ShoppingFragment.instance.uncategorizedItems.size();
    }
}