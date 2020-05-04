package com.jschiefner.shoppinglist;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.jschiefner.shoppinglist.database.Rule;
import com.jschiefner.shoppinglist.database.RuleViewModel;
import com.jschiefner.shoppinglist.database.RuleViewModelFactory;
import com.jschiefner.shoppinglist.database.RuleWithCategory;
import com.jschiefner.shoppinglist.database.UncategorizedItemViewAdapter;

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
        View view = getLayoutInflater().inflate(R.layout.new_item_dialog, null);
        final EditText itemNameInput = view.findViewById(R.id.item_name_edit_text);
        final TextView newCategoryText = view.findViewById(R.id.new_category_text_view);
        final EditText newCategoryEdit = view.findViewById(R.id.new_category_edit_text);
        final Spinner spinner = view.findViewById(R.id.category_spinner);
        final Button saveButton = view.findViewById(R.id.save_button);
        final CheckBox ruleDeleteCheckbox = view.findViewById(R.id.checkbox_rule_to_delete);
        final TextView ruleDeleteText = view.findViewById(R.id.text_rule_to_delete);
        final CheckBox ruleAddCheckbox = view.findViewById(R.id.checkbox_rule_to_add);
        final TextView ruleAddText = view.findViewById(R.id.text_rule_to_add);

        List<String> categories = getCategoryStringList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, categories);
        spinner.setAdapter(adapter);
        spinner.setSelection(categories.size()-2);

        // open Dialog
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        itemNameInput.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        saveButton.setOnClickListener(view1 -> {
            // addItem(itemNameInput.getText().toString());
            String newItemName = itemNameInput.getText().toString();
            if (newItemName.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out the Item Name", Toast.LENGTH_SHORT).show();
                return;
            }
            int size = categories.size();
            int position = spinner.getSelectedItemPosition();
            if (position == size-1) { // New
                String newCategoryName = newCategoryEdit.getText().toString();
                if (newCategoryName.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill out the new Category or select another Category Option", Toast.LENGTH_SHORT).show();
                    return;
                }
                itemViewModel.insert(new Item(newItemName), new Category(newCategoryName), ruleDeleteCheckbox.isChecked(), ruleAddCheckbox.isChecked());
            } else if (position == size-2) { // None
                itemViewModel.insert(new Item(newItemName));
            } else { // existing Category
                Category category = getCategoryByPosition(position);
                itemViewModel.insert(new Item(newItemName, category.id));
                if (ruleDeleteCheckbox.isChecked()) ruleViewModel.delete(newItemName);
            }
            dialog.dismiss();
        });

        itemNameInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            categoryViewModel.getCategoryByRuleName(itemNameInput.getText().toString(), object -> {
                if (object == null) {
                    spinner.setSelection(categories.size()-2);
                    return;
                }
                Category category = (Category) object;
                newCategoryEdit.getText().toString();
                spinner.setSelection(categories.indexOf(category.name));
            });
            return true;
        });

        newCategoryEdit.setOnEditorActionListener((textView, i, keyEvent) -> {
            String newItemName = itemNameInput.getText().toString();
            String newCategoryName = newCategoryEdit.getText().toString();
            ruleAddCheckbox.setVisibility(View.VISIBLE);
            ruleAddCheckbox.setChecked(true);
            ruleAddText.setVisibility(View.VISIBLE);
            ruleAddText.setText(String.format("%s > %s", newItemName, newCategoryName));
            ruleViewModel.getRuleWithCategory(newItemName, object -> {
                RuleWithCategory ruleWithCategory = (RuleWithCategory) object;
                if (ruleWithCategory == null) return;
                Rule rule = ruleWithCategory.rule;
                Category category = ruleWithCategory.category;

                ruleDeleteCheckbox.setChecked(true);
                ruleDeleteCheckbox.setVisibility(View.VISIBLE);
                SpannableString spannableString = new SpannableString(String.format("%s -> %s", rule.name, category.name));
                spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), 0);
                ruleDeleteText.setText(spannableString);
                ruleDeleteText.setVisibility(View.VISIBLE);

            });
            return true;
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String newItemName = itemNameInput.getText().toString();
                if (newItemName.isEmpty()) return;
                int size = categories.size();
                if (position == size-1) { // New
                    newCategoryText.setVisibility(View.VISIBLE);
                    newCategoryEdit.setText("");
                    newCategoryEdit.setVisibility(View.VISIBLE);
                    ruleAddCheckbox.setVisibility(View.GONE);
                    ruleAddText.setVisibility(View.GONE);

                } else if (position == size-2){ // None
                    newCategoryText.setVisibility(View.GONE);
                    newCategoryEdit.setVisibility(View.GONE);
                    ruleAddCheckbox.setVisibility(View.GONE);
                    ruleAddText.setVisibility(View.GONE);
                    ruleViewModel.getRuleWithCategory(newItemName, object -> {
                        RuleWithCategory ruleWithCategory = (RuleWithCategory) object;
                        if (ruleWithCategory == null) return;
                        Rule rule = ruleWithCategory.rule;
                        Category category = ruleWithCategory.category;

                        ruleDeleteCheckbox.setVisibility(View.VISIBLE);
                        ruleDeleteCheckbox.setChecked(true);
                        SpannableString spannableString = new SpannableString(String.format("%s -> %s", rule.name, category.name));
                        spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), 0);
                        ruleDeleteText.setVisibility(View.VISIBLE);
                        ruleDeleteText.setText(spannableString);
                    });
                } else { // existing category
                    newCategoryText.setVisibility(View.GONE);
                    newCategoryEdit.setVisibility(View.GONE);

                    ruleViewModel.getRuleWithCategory(newItemName, object -> {
                        RuleWithCategory ruleWithCategory = (RuleWithCategory) object;
                        Category categorySelected = getCategoryByPosition(position);

                        if (ruleWithCategory == null) {
                            ruleDeleteCheckbox.setVisibility(View.GONE);
                            ruleDeleteText.setVisibility(View.GONE);
                            ruleAddCheckbox.setChecked(true);
                            ruleAddCheckbox.setVisibility(View.VISIBLE);
                            ruleAddText.setText(String.format("%s > %s", newItemName, categorySelected.name));
                            ruleAddText.setVisibility(View.VISIBLE);
                        } else if (ruleWithCategory.category.id != categorySelected.id) {
                            ruleDeleteCheckbox.setChecked(true);
                            SpannableString spannableString = new SpannableString(String.format("%s -> %s", ruleWithCategory.rule.name, ruleWithCategory.category.name));
                            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), 0);
                            ruleDeleteCheckbox.setVisibility(View.VISIBLE);
                            ruleDeleteText.setText(spannableString);
                            ruleDeleteText.setVisibility(View.VISIBLE);

                            ruleAddCheckbox.setChecked(true);
                            ruleAddCheckbox.setVisibility(View.VISIBLE);
                            ruleAddText.setText(String.format("%s > %s", newItemName, categorySelected.name));
                            ruleAddText.setVisibility(View.VISIBLE);
                        } else {
                            ruleDeleteCheckbox.setVisibility(View.GONE);
                            ruleDeleteText.setVisibility(View.GONE);
                            ruleAddCheckbox.setVisibility(View.GONE);
                            ruleAddText.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
        List<String> categories = new ArrayList<>(categorizedItemsAdapter.categoriesWithItems.size()+1);
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
//                Category selectedCategory = getCategoryByPosition(position);
//                if (selectedCategory != null) {
//                    ruleViewModel.getRuleWithCategory(item.name, category, object -> {
//                        Rule rule = (Rule) object;
//                        Log.i("CUSTOM", "found rule: " + (rule != null));
//                        // TODO: propose to delete old rule (from name and old category)
//                        // TODO: propose to add new rule
//                    });
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i("CUSTOM", "nothing selected");
            }
        });
    }

    private List<String> getCategoryStringList() {
        List<CategoryWithItems> categoriesWithItems = categorizedItemsAdapter.categoriesWithItems;
        List<String> categories = new ArrayList<>(categoriesWithItems.size()+1);
        for (CategoryWithItems categoryWithItems : categoriesWithItems) categories.add(categoryWithItems.category.name);
        categories.add("None");
        categories.add("New");
        return categories;
    }

    private Category getCategoryByPosition(int position) {
        if (position != categorizedItemsAdapter.categoriesWithItems.size()) return categorizedItemsAdapter.categoriesWithItems.get(position).category;
        else return null;
    }
}
