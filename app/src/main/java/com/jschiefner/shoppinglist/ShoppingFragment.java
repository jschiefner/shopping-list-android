package com.jschiefner.shoppinglist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jschiefner.shoppinglist.database.Category;
import com.jschiefner.shoppinglist.database.CategoryViewModel;
import com.jschiefner.shoppinglist.database.CategoryViewModelFactory;
import com.jschiefner.shoppinglist.database.CategoryWithItems;
import com.jschiefner.shoppinglist.database.Item;
import com.jschiefner.shoppinglist.database.CategoryItemViewAdapter;
import com.jschiefner.shoppinglist.database.ItemViewModel;
import com.jschiefner.shoppinglist.database.ItemViewModelFactory;
import com.jschiefner.shoppinglist.database.ItemSwipeTouchHelper;
import com.jschiefner.shoppinglist.database.QueryHandler;
import com.jschiefner.shoppinglist.database.Rule;
import com.jschiefner.shoppinglist.database.RuleViewModel;
import com.jschiefner.shoppinglist.database.RuleViewModelFactory;
import com.jschiefner.shoppinglist.database.UncategorizedItemViewAdapter;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingFragment extends Fragment {
    private RecyclerView categorizedRecyclerView;
    private RecyclerView uncategorizedRecyclerView;
    public ItemViewModel itemViewModel;
    private CategoryViewModel categoryViewModel;
    private RuleViewModel ruleViewModel;
    private final CategoryItemViewAdapter categorizedItemsAdapter = new CategoryItemViewAdapter();
    private UncategorizedItemViewAdapter uncategorizedItemViewAdapter = new UncategorizedItemViewAdapter();
    private FloatingActionButton fab;
    public static ShoppingFragment instance;
    private final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.shopping_fragment, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setActionBarTitle(R.string.shopping_fragment_label);

        categorizedRecyclerView = rootView.findViewById(R.id.items_recycler_view);
        categorizedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categorizedRecyclerView.setHasFixedSize(true);
        categorizedRecyclerView.setAdapter(categorizedItemsAdapter);

        uncategorizedRecyclerView = rootView.findViewById(R.id.uncategorized_recycler_view);
        uncategorizedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        uncategorizedRecyclerView.setHasFixedSize(true);
        uncategorizedRecyclerView.setAdapter(uncategorizedItemViewAdapter);

        itemViewModel = ViewModelProviders.of(this, new ItemViewModelFactory(getActivity().getApplication())).get(ItemViewModel.class);
        ruleViewModel = ViewModelProviders.of(this, new RuleViewModelFactory(getActivity().getApplication(), -1L)).get(RuleViewModel.class);

        // Setup Item List
        categoryViewModel = ViewModelProviders.of(this, new CategoryViewModelFactory(getActivity().getApplication())).get(CategoryViewModel.class);
        categoryViewModel.getCategoriesWithItems().observe(this, categorizedItemsAdapter::setCategoriesWithItems);
        itemViewModel.getUncategorizedItems().observe(this, uncategorizedItemViewAdapter::setItems);

        new ItemTouchHelper(new ItemSwipeTouchHelper(this, categorizedItemsAdapter, 0, swipeFlags)).attachToRecyclerView(categorizedRecyclerView);
        new ItemTouchHelper(new ItemSwipeTouchHelper(this, uncategorizedItemViewAdapter, 0, swipeFlags)).attachToRecyclerView(uncategorizedRecyclerView);

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
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        saveButton.setOnClickListener(view1 -> {
            addItem(itemNameInput.getText().toString());
            dialog.dismiss();
        });

        itemNameInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            addItem(itemNameInput.getText().toString());
            dialog.dismiss();
            return true;
        });
    }

    private void addItem(String name) {
        Item item = new Item(name);
        itemViewModel.insert(item);
        ServerAPI.getInstance().addItem(item, getContext());
    }

    public void editItemDialog(Item item) {
        // create editItem dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.edit_item, null);

        EditText itemEditText = view.findViewById(R.id.item_name_edit_text);
        itemEditText.setText(item.name);
        Button saveButton = view.findViewById(R.id.save_button);
        Spinner spinner = view.findViewById(R.id.category_spinner);
        Category category;
        int position = -1;
        List<String> categories = new ArrayList<>();
        for (int i = 0; i < categorizedItemsAdapter.categoriesWithItems.size(); i++) {
            CategoryWithItems categoryWithItems = categorizedItemsAdapter.categoriesWithItems.get(i);
            categories.add(categoryWithItems.category.name);
            if (categoryWithItems.items.contains(item)) {
                position = i;
                category = categoryWithItems.category;
            }
        }
        categories.add("None");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, categories);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (position >= 0) spinner.setSelection(position);
        else spinner.setSelection(categories.size()-1);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        saveButton.setOnClickListener(view1 -> {
            item.name = itemEditText.getText().toString();
            Category selectedCategory = getCategoryByPosition(spinner.getSelectedItemPosition());
            if (selectedCategory != null) item.categoryId = selectedCategory.id;
            else item.categoryId = null;
            itemViewModel.update(item);
            dialog.dismiss();
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean firstTime = true;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (firstTime) {
                    firstTime = false;
                    return;
                }
                Category selectedCategory = getCategoryByPosition(position);
                if (selectedCategory != null) {
                    ruleViewModel.getRuleWithCategory(item.name, category, object -> {
                        Rule rule = (Rule) object;
                        Log.i("CUSTOM", "found rule: " + (rule != null));
                        // TODO: propose to delete old rule (from name and old category)
                        // TODO: propose to add new rule
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i("CUSTOM", "nothing selected");
            }
        });
    }

    private Category getCategoryByPosition(int position) {
        if (position != categorizedItemsAdapter.categoriesWithItems.size()) return categorizedItemsAdapter.categoriesWithItems.get(position).category;
        else return null;
    }
}
