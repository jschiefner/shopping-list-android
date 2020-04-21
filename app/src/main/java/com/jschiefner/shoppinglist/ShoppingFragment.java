package com.jschiefner.shoppinglist;

import android.content.Context;
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
import com.jschiefner.shoppinglist.database.CategoryViewModel;
import com.jschiefner.shoppinglist.database.CategoryViewModelFactory;
import com.jschiefner.shoppinglist.database.CategoryWithItems;
import com.jschiefner.shoppinglist.database.Item;
import com.jschiefner.shoppinglist.database.ItemViewAdapter;
import com.jschiefner.shoppinglist.database.ItemViewModel;
import com.jschiefner.shoppinglist.database.ItemViewModelFactory;

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
    private CategoryViewModel categoryViewModel;
    private final ItemViewAdapter adapter = new ItemViewAdapter();
    private FloatingActionButton fab;
    public static ShoppingFragment instance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.shopping_fragment, container, false);

        recyclerView = rootView.findViewById(R.id.items_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        itemViewModel = ViewModelProviders.of(this, new ItemViewModelFactory(getActivity().getApplication())).get(ItemViewModel.class);

        // Setup Item List
        categoryViewModel = ViewModelProviders.of(this, new CategoryViewModelFactory(getActivity().getApplication())).get(CategoryViewModel.class);
        categoryViewModel.getCategoriesWithItems().observe(this, adapter::setCategoriesWithItems);

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
                ServerAPI.getInstance().delete(item, getContext());
            }
        }).attachToRecyclerView(recyclerView);

        fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(view -> handleFabClick());

        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("CUSTOM", "onresume");
        instance = this;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("CUSTOM", "onpause");
        instance = null;
    }

    public void handleFabClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.new_dialog_with_text, null);
        TextView topText = view.findViewById(R.id.new_text_top_description);
        final EditText itemNameInput = view.findViewById(R.id.new_text_text_input);
        Button saveButton = view.findViewById(R.id.new_text_button);

        // set text in new Item Dialog
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
                addItem(itemNameInput.getText().toString());
                dialog.dismiss();
            }
        });

        itemNameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                addItem(itemNameInput.getText().toString());
                dialog.dismiss();
                return true;
            }
        });
    }

    private void addItem(String name) {
        Item item = new Item(name);
        itemViewModel.insert(item);
        ServerAPI.getInstance().addItem(item, getContext());
    }
}
