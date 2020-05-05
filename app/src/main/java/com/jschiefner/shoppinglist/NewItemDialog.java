package com.jschiefner.shoppinglist;

import android.content.res.ColorStateList;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jschiefner.shoppinglist.database.Category;
import com.jschiefner.shoppinglist.database.CategoryViewModel;
import com.jschiefner.shoppinglist.database.CategoryViewModelFactory;
import com.jschiefner.shoppinglist.database.CategoryWithItems;
import com.jschiefner.shoppinglist.database.Item;
import com.jschiefner.shoppinglist.database.ItemViewModel;
import com.jschiefner.shoppinglist.database.ItemViewModelFactory;
import com.jschiefner.shoppinglist.database.Rule;
import com.jschiefner.shoppinglist.database.RuleViewModel;
import com.jschiefner.shoppinglist.database.RuleViewModelFactory;
import com.jschiefner.shoppinglist.database.RuleWithCategory;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;

public class NewItemDialog implements View.OnClickListener, Spinner.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    public ItemViewModel itemViewModel;
    private CategoryViewModel categoryViewModel;
    private RuleViewModel ruleViewModel;

    private EditText itemNameInput;
    private TextView newCategoryText;
    private EditText newCategoryEdit;
    private Spinner spinner;
    private Button saveButton;
    private CheckBox ruleDeleteCheckbox;
    private TextView ruleDeleteText;
    private CheckBox ruleAddCheckbox;
    private TextView ruleAddText;

    private AlertDialog.Builder builder;
    private List<String> categories;
    private AlertDialog dialog;

    private Rule ruleToDelete;

    public NewItemDialog() {
        itemViewModel = ViewModelProviders.of(ShoppingFragment.instance, new ItemViewModelFactory(ShoppingFragment.instance.getActivity().getApplication())).get(ItemViewModel.class);
        categoryViewModel = ViewModelProviders.of(ShoppingFragment.instance, new CategoryViewModelFactory(ShoppingFragment.instance.getActivity().getApplication())).get(CategoryViewModel.class);
        ruleViewModel = ViewModelProviders.of(ShoppingFragment.instance, new RuleViewModelFactory(ShoppingFragment.instance.getActivity().getApplication(), -1L)).get(RuleViewModel.class);

        builder = new AlertDialog.Builder(ShoppingFragment.instance.getContext());
        View view = ShoppingFragment.instance.getLayoutInflater().inflate(R.layout.new_item_dialog, null);
        builder.setView(view);

        itemNameInput = view.findViewById(R.id.item_name_edit_text);
        newCategoryText = view.findViewById(R.id.new_category_text_view);
        newCategoryEdit = view.findViewById(R.id.new_category_edit_text);
        spinner = view.findViewById(R.id.category_spinner);
        saveButton = view.findViewById(R.id.save_button);
        ruleDeleteCheckbox = view.findViewById(R.id.checkbox_rule_to_delete);
        ruleDeleteText = view.findViewById(R.id.text_rule_to_delete);
        ruleAddCheckbox = view.findViewById(R.id.checkbox_rule_to_add);
        ruleAddText = view.findViewById(R.id.text_rule_to_add);

        categories = getCategoryStringList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ShoppingFragment.instance.getContext(), R.layout.support_simple_spinner_dropdown_item, categories);
        spinner.setAdapter(adapter);
        spinner.setSelection(categories.size()-2);

        saveButton.setOnClickListener(this);
        itemNameInput.setOnEditorActionListener(this::itemNameInputChange);
        newCategoryEdit.setOnEditorActionListener(this::newCategoryInputChange);
        spinner.setOnItemSelectedListener(this);
        ruleDeleteCheckbox.setOnCheckedChangeListener(this);
        ruleDeleteText.setOnClickListener(v -> ruleDeleteCheckbox.toggle());
        ruleAddText.setOnClickListener(v -> ruleAddCheckbox.toggle());
    }

    public void show() {
        dialog = builder.create();
        itemNameInput.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    private boolean itemNameInputChange(TextView textView, int i, KeyEvent keyEvent) {
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
    }

    private boolean newCategoryInputChange(TextView textView, int i, KeyEvent keyEvent) {
        String newItemName = itemNameInput.getText().toString();
        String newCategoryName = newCategoryEdit.getText().toString();
        ruleAddCheckbox.setVisibility(View.VISIBLE);
        ruleAddCheckbox.setChecked(true);
        ruleAddText.setVisibility(View.VISIBLE);
        ruleAddText.setText(String.format("%s > %s", newItemName, newCategoryName));
        ruleViewModel.getRuleWithCategory(newItemName, object -> {
            RuleWithCategory ruleWithCategory = (RuleWithCategory) object;
            if (ruleWithCategory == null) {
                ruleToDelete = null;
                return;
            }
            ruleToDelete = ruleWithCategory.rule;
            Category category = ruleWithCategory.category;

            ruleDeleteCheckbox.setChecked(true);
            ruleDeleteCheckbox.setVisibility(View.VISIBLE);
            SpannableString spannableString = new SpannableString(String.format("%s -> %s", ruleToDelete.name, category.name));
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), 0);
            ruleDeleteText.setText(spannableString);
            ruleDeleteText.setVisibility(View.VISIBLE);

        });
        return true;
    }

    private List<String> getCategoryStringList() {
        List<CategoryWithItems> categoriesWithItems = ShoppingFragment.instance.categoriesWithItems;
        List<String> categories = new ArrayList<>(categoriesWithItems.size()+1);
        for (CategoryWithItems categoryWithItems : categoriesWithItems) categories.add(categoryWithItems.category.name);
        categories.add("None");
        categories.add("New");
        return categories;
    }

    private Category getCategoryByPosition(int position) {
        if (position != ShoppingFragment.instance.categoriesWithItems.size()) return ShoppingFragment.instance.categoriesWithItems.get(position).category;
        else return null;
    }

    @Override
    public void onClick(View view) {
        String newItemName = itemNameInput.getText().toString();
        if (newItemName.isEmpty()) {
            Toast.makeText(ShoppingFragment.instance.getContext(), "Please fill out the Item Name", Toast.LENGTH_SHORT).show();
            return;
        }
        int size = categories.size();
        int position = spinner.getSelectedItemPosition();
        if (position == size-1) { // New
            String newCategoryName = newCategoryEdit.getText().toString();
            if (newCategoryName.isEmpty()) {
                Toast.makeText(ShoppingFragment.instance.getContext(), "Please fill out the new Category or select another Category Option", Toast.LENGTH_SHORT).show();
                return;
            }
            itemViewModel.insert(new Item(newItemName), new Category(newCategoryName), ruleToDelete, ruleDeleteCheckbox.isChecked(), ruleAddCheckbox.isChecked());
        } else if (position == size-2) { // None
            itemViewModel.insert(new Item(newItemName), null, ruleToDelete, ruleDeleteCheckbox.isChecked(), false);
        } else { // existing Category
            Category category = getCategoryByPosition(position);
            itemViewModel.insert(new Item(newItemName, category.id), null, ruleToDelete, ruleDeleteCheckbox.isChecked(), ruleAddCheckbox.isChecked());
        }
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog.dismiss();
    }

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
                if (ruleWithCategory == null) {
                    ruleDeleteCheckbox.setVisibility(View.GONE);
                    ruleDeleteText.setVisibility(View.GONE);
                    ruleToDelete = null;
                    return;
                }
                ruleToDelete = ruleWithCategory.rule;
                Category category = ruleWithCategory.category;

                ruleDeleteCheckbox.setVisibility(View.VISIBLE);
                ruleDeleteCheckbox.setChecked(true);
                SpannableString spannableString = new SpannableString(String.format("%s -> %s", ruleToDelete.name, category.name));
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
                    ruleToDelete = null;
                    ruleDeleteCheckbox.setVisibility(View.GONE);
                    ruleDeleteText.setVisibility(View.GONE);
                    ruleAddCheckbox.setChecked(true);
                    ruleAddCheckbox.setVisibility(View.VISIBLE);
                    ruleAddText.setText(String.format("%s > %s", newItemName, categorySelected.name));
                    ruleAddText.setVisibility(View.VISIBLE);
                } else if (ruleWithCategory.category.id != categorySelected.id) {
                    ruleToDelete = ruleWithCategory.rule;
                    ruleDeleteCheckbox.setChecked(true);
                    SpannableString spannableString = new SpannableString(String.format("%s -> %s", ruleToDelete.name, ruleWithCategory.category.name));
                    spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), 0);
                    ruleDeleteCheckbox.setVisibility(View.VISIBLE);
                    ruleDeleteText.setText(spannableString);
                    ruleDeleteText.setVisibility(View.VISIBLE);

                    ruleAddCheckbox.setChecked(true);
                    ruleAddCheckbox.setVisibility(View.VISIBLE);
                    ruleAddText.setText(String.format("%s > %s", newItemName, categorySelected.name));
                    ruleAddText.setVisibility(View.VISIBLE);
                } else {
                    ruleToDelete = null;
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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (ruleDeleteCheckbox != compoundButton) return;

        if (checked) ruleDeleteCheckbox.setButtonTintList(ColorStateList.valueOf(ShoppingFragment.instance.getContext().getResources().getColor(R.color.red)));
        else ruleDeleteCheckbox.setButtonTintList(ColorStateList.valueOf(ShoppingFragment.instance.getContext().getResources().getColor(R.color.gray_default_checkbox)));
    }
}
