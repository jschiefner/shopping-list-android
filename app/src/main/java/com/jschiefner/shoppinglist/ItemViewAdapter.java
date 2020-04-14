package com.jschiefner.shoppinglist;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewAdapter extends RecyclerView.Adapter<ItemViewAdapter.ItemViewHolder> {
    private List<Item> items = new ArrayList<>();

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public EditText editText;
        public Item item;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.recycler_edit_text);
            this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    Log.i("CUSTOM", textView.getText().toString());
                    InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    if (!item.name.equals(editText.getText().toString())) {
                        Log.i("CUSTOM", "updated!");
                    }

                    return true;
                }
            });
            this.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) return;

                    EditText editText = (EditText) view;
                    editText.clearFocus();
                    Log.i("CUSTOM", editText.getText().toString());
                }
            });
        }
    }

    public void setItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item currentItem = items.get(position);
        holder.editText.setText(currentItem.name);
        holder.item = currentItem;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
