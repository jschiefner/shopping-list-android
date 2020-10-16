package com.jschiefner.shoppinglist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAdapter extends FirestoreRecyclerAdapter<Item, ItemAdapter.ItemHolder> {
    private Category category;

    public ItemAdapter(@NonNull FirestoreRecyclerOptions<Item> options, Category category) {
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
        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> holder.item.getReference().update("completed", b));
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view, parent, false);
        return new ItemAdapter.ItemHolder(view);
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        Item item;
        EditText editText;
        CheckBox checkBox;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.item_edit_text);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
