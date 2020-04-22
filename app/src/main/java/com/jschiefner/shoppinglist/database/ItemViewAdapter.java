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
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;
import kotlin.jvm.internal.Ref;

public class ItemViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CategoryWithItems> categoriesWithItems = new ArrayList<>();
    private Object[] listStore = new Object[0];

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

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.header_text_view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setCategoriesWithItems(List<CategoryWithItems> categoriesWithItems) {
        this.categoriesWithItems = categoriesWithItems;
        this.listStore = new Object[getItemCount()];
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) { // position: 5
        // [OBST, [apfel, na, fe], GEMÜSE, [möhre, aro, kar]]
        //   0       1     2   3      4        5    6    7
        int temp = 0;
        for (CategoryWithItems categoryWithItems : categoriesWithItems) {
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
            holder.editText.setText(item.name);
            holder.item = item;
            holder.checkBox.setChecked(item.completed);
        }
    }

    @Override
    public int getItemCount() {
//        int size = categoriesWithItems.stream().mapToInt(categoryWithItems -> categoryWithItems.items.size()).sum();
        int size = categoriesWithItems.size();
        for (CategoryWithItems categoryWithItems : categoriesWithItems) size += categoryWithItems.items.size();
        return size;
    }
}
