package com.jschiefner.shoppinglist.database;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.jschiefner.shoppinglist.R;
import com.jschiefner.shoppinglist.ServerAPI;
import com.jschiefner.shoppinglist.ShoppingFragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends RecyclerView.ViewHolder implements TextView.OnEditorActionListener, View.OnFocusChangeListener, CompoundButton.OnCheckedChangeListener {
    public CheckBox checkBox;
    public EditText editText;
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
            ShoppingFragment.instance.itemViewModel.update(item);
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
            ShoppingFragment.instance.itemViewModel.update(item);
            ServerAPI.getInstance().update(item, editText.getContext());
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
        if (!compoundButton.isPressed()) return;
        Log.i("CUSTOM", "checked: " + item + value);
        item.toggle(value);
        ShoppingFragment.instance.itemViewModel.update(item);
        ServerAPI.getInstance().update(item, compoundButton.getContext());
    }
}