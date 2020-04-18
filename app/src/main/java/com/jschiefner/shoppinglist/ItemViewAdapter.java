package com.jschiefner.shoppinglist;

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
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewAdapter extends RecyclerView.Adapter<ItemViewAdapter.ItemViewHolder> {
    private List<Item> items = new ArrayList<>();

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements TextView.OnEditorActionListener, View.OnFocusChangeListener, CompoundButton.OnCheckedChangeListener {
        private CheckBox checkBox;
        private EditText editText;
        public Item item;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(this);
            editText = itemView.findViewById(R.id.recycler_edit_text);
            editText.setOnEditorActionListener(this);
            editText.setOnFocusChangeListener(this);
        }

        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            Log.i("CUSTOM", textView.getText().toString());
            InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
            if (!item.name.equals(editText.getText().toString())) {
                Log.i("CUSTOM", "updated (pressed done): " + item);
                item.update(editText.getText().toString());
                ServerAPI.getInstance().update(item, editText.getContext());
            }

            return true;
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) return;

            if (!item.name.equals(editText.getText().toString())) {
                Log.i("CUSTOM", "updated (changed focus): " + item);
                item.update(editText.getText().toString());
                ServerAPI.getInstance().update(item, editText.getContext());
            }

        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
            if (!compoundButton.isPressed()) return;
            Log.i("CUSTOM", "checked: " + item + value);
            item.toggle(value);
            ServerAPI.getInstance().update(item, compoundButton.getContext());
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
        holder.checkBox.setChecked(currentItem.completed);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
