package com.jschiefner.shoppinglist;

import android.content.Context;
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

    static class ItemHolder extends RecyclerView.ViewHolder implements TextView.OnEditorActionListener, View.OnFocusChangeListener, CompoundButton.OnCheckedChangeListener {
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
            InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
            String newName = editText.getText().toString();
            if (!item.getName().equals(newName)) {
                item.setName(newName);
                item.getReference().set(item);
            }
            editText.clearFocus();
            return true;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            item.getReference().update("completed", isChecked);
        }
    }
}