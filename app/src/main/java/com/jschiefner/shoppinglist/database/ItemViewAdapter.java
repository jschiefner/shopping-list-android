package com.jschiefner.shoppinglist.database;

import android.content.Context;
import android.os.Build;
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

import com.jschiefner.shoppinglist.R;
import com.jschiefner.shoppinglist.ServerAPI;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewAdapter extends RecyclerView.Adapter<ItemViewAdapter.ItemViewHolder> {
    private List<CategoryWithItems> categoriesWithItems = new ArrayList<>();

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

    public void setCategoriesWithItems(List<CategoryWithItems> categoriesWithItems) {
        this.categoriesWithItems = categoriesWithItems;
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
        int sizeCount = 0;
        for (CategoryWithItems categoryWithItems : categoriesWithItems) {
            int current = position - sizeCount;
            if (current >= categoryWithItems.items.size()) {
                sizeCount += categoryWithItems.items.size();
                continue;
            }

            Item item = categoryWithItems.items.get(current);
            holder.editText.setText(item.name);
            holder.item = item;
            holder.checkBox.setChecked(item.completed);
            break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int getItemCount() {
        int size = categoriesWithItems.stream().mapToInt(categoryWithItems -> categoryWithItems.items.size()).sum();
        return size;
    }
}
