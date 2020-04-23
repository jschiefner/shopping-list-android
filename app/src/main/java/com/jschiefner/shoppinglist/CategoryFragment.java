package com.jschiefner.shoppinglist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jschiefner.shoppinglist.database.Category;
import com.jschiefner.shoppinglist.database.CategoryViewAdapter;
import com.jschiefner.shoppinglist.database.CategoryViewModel;
import com.jschiefner.shoppinglist.database.CategoryViewModelFactory;
import com.jschiefner.shoppinglist.database.Item;
import com.jschiefner.shoppinglist.database.ItemViewAdapter;

import java.util.List;

public class CategoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryViewModel categoryViewModel;
    private final CategoryViewAdapter adapter = new CategoryViewAdapter();
    private FloatingActionButton fab;
    public static CategoryFragment instance;

    private final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.category_fragment, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setActionBarTitle(R.string.category_fragment_label);

        recyclerView = rootView.findViewById(R.id.categories_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        categoryViewModel = ViewModelProviders.of(this, new CategoryViewModelFactory(getActivity().getApplication())).get(CategoryViewModel.class);
        categoryViewModel.getCategories().observe(this, categories -> adapter.setCategories(categories));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, swipeFlags) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                CategoryViewAdapter.CategoryViewHolder holder = (CategoryViewAdapter.CategoryViewHolder) viewHolder;
                Category category = holder.category;
                categoryViewModel.delete(category);
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
        final EditText categoryNameInput = view.findViewById(R.id.new_text_text_input);
        Button saveButton = view.findViewById(R.id.new_text_button);

        // set text in new Item Dialog
        topText.setText(R.string.new_category);
        saveButton.setText(R.string.button_save);

        // open Dialog
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        categoryNameInput.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        saveButton.setOnClickListener(view1 -> {
            addCategory(categoryNameInput.getText().toString());
            dialog.dismiss();
        });

        categoryNameInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            addCategory(categoryNameInput.getText().toString());
            dialog.dismiss();
            return true;
        });
    }

    private void addCategory(String name) {
        Category category = new Category(name);
        categoryViewModel.insert(category);
    }

}
