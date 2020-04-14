package com.jschiefner.shoppinglist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingFragment extends Fragment {
    private RecyclerView recyclerView;
    private ItemViewModel itemViewModel;
    final ItemViewAdapter adapter = new ItemViewAdapter();
    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.shopping_fragment, container, false);

        recyclerView = rootView.findViewById(R.id.items_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        // Setup Trip List
        itemViewModel = ViewModelProviders.of(this, new ItemViewModelFactory(getActivity().getApplication())).get(ItemViewModel.class);
        itemViewModel.getItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                adapter.setItems(items);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                ItemViewAdapter.ItemViewHolder holder = (ItemViewAdapter.ItemViewHolder) viewHolder;
                Item item = holder.item;
                itemViewModel.delete(item);
            }
        }).attachToRecyclerView(recyclerView);

        fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFabClick();
            }
        });

        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void handleFabClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.new_dialog_with_text, null);
        TextView topText = view.findViewById(R.id.new_text_top_description);
        final EditText itemNameInput = view.findViewById(R.id.new_text_text_input);
        Button saveButton = view.findViewById(R.id.new_text_button);

        // set text in Thought Dialog
        topText.setText(R.string.new_item);
        saveButton.setText(R.string.button_save);

        // open Dialog
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        itemNameInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) itemNameInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(itemNameInput.getWindowToken(), 0);
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item item = new Item(itemNameInput.getText().toString());
                itemViewModel.insert(item);
                dialog.dismiss();
            }
        });

        itemNameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Item item = new Item(itemNameInput.getText().toString());
                itemViewModel.insert(item);
                dialog.dismiss();
                return true;
            }
        });
    }
}
