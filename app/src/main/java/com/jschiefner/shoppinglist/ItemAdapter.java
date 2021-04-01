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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAdapter extends FirestoreRecyclerAdapter<Item, ItemAdapter.ItemHolder> {
    private Category category;

    ItemAdapter(@NonNull FirestoreRecyclerOptions<Item> options, Category category) {
        super(options);
        this.category = category;
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull Item model) {
        model.setId(getSnapshots().getSnapshot(position).getId());
        model.setReference(getSnapshots().getSnapshot(position).getReference());
        model.setCategory(category);
        holder.item = model;
        holder.editText.setText(model.getName());
        holder.checkBox.setChecked(holder.item.getCompleted());
        if (MainActivity.instance.isEditEnterPressed() && model.isNew()) {
            holder.editText.requestFocus();
            MainActivity.instance.setEditEnterPressed(false);
        }
    }
    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view, parent, false);
        return new ItemHolder(view);
    }

    boolean isEmpty() {
        return getSnapshots().size() == 0;
    }

    class ItemHolder extends RecyclerView.ViewHolder implements TextView.OnEditorActionListener, View.OnFocusChangeListener, CompoundButton.OnCheckedChangeListener {
        Item item;
        BackClickableEditText editText;
        CheckBox checkBox;

        ItemHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.item_edit_text);
            editText.setOnEditorActionListener(this);
            editText.setOnFocusChangeListener(this);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                ShoppingFragment.focusedEditText = (BackClickableEditText) view;
                return;
            }

            String newName = editText.getText().toString();
            if (!item.getName().equals(newName)) {
                item.setName(newName);
                item.getReference().set(item);
            }
        }

        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            // called when enter is pressed when editing an item in the main table layout
//            InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

            // Save current item
            String newName = editText.getText().toString();
            if (!item.getName().equals(newName)) {
                item.setName(newName);
                item.getReference().set(item);
            }

            // add new empty item
            item.getReference().getParent().add(new Item("", false));
            MainActivity.instance.setEditEnterPressed(true);

            // editText.clearFocus(); // suppress for now when using enter-newline feature
            return true;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            item.getReference().update("completed", isChecked);
        }
    }
}