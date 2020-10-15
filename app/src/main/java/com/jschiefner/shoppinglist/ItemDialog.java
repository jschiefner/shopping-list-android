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

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

public class ItemDialog implements View.OnClickListener, Spinner.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
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
    private View layout;

    private Item itemToEdit;
    private Rule ruleToDelete;

    public ItemDialog() {
        builder = new AlertDialog.Builder(ShoppingFragment.instance.getContext());
        layout = ShoppingFragment.instance.getLayoutInflater().inflate(R.layout.new_item_dialog, null);
        builder.setView(layout);

        itemNameInput = layout.findViewById(R.id.item_name_edit_text);
        newCategoryText = layout.findViewById(R.id.new_category_text_view);
        newCategoryEdit = layout.findViewById(R.id.new_category_edit_text);
        spinner = layout.findViewById(R.id.category_spinner);
        saveButton = layout.findViewById(R.id.save_button);
        ruleDeleteCheckbox = layout.findViewById(R.id.checkbox_rule_to_delete);
        ruleDeleteText = layout.findViewById(R.id.text_rule_to_delete);
        ruleAddCheckbox = layout.findViewById(R.id.checkbox_rule_to_add);
        ruleAddText = layout.findViewById(R.id.text_rule_to_add);

        categories = getCategoryStringList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ShoppingFragment.instance.getContext(), R.layout.support_simple_spinner_dropdown_item, categories);
        // spinner.setAdapter(adapter); TODO: set spinner adapter
        spinner.setSelection(categories.size()-2);

        saveButton.setOnClickListener(this);
        itemNameInput.setOnEditorActionListener(this::itemNameInputChange);
        newCategoryEdit.setOnEditorActionListener(this::newCategoryInputChange);
        spinner.setOnItemSelectedListener(this);
        ruleDeleteCheckbox.setOnCheckedChangeListener(this);
        ruleDeleteText.setOnClickListener(v -> ruleDeleteCheckbox.toggle());
        ruleAddText.setOnClickListener(v -> ruleAddCheckbox.toggle());
    }

    public ItemDialog(Item item) {
        this(); // call constructor without any parameters
        this.itemToEdit = item;
//        TextView title = layout.findViewById(R.id.item_dialog_title);
//        title.setText(R.string.edit_item_head);
//        itemNameInput.setText(item.name);
//        if (item.isCategorized()) {
//            List<CategoryWithItems> categoryWithItems = ShoppingFragment.instance.categoriesWithItems;
//            for (int position = 0; position < ShoppingFragment.instance.categoriesWithItems.size(); position++) {
//                if (!categoryWithItems.get(position).items.contains(item)) continue;
//                spinner.setSelection(position);
//                break;
//            }
//        }
    }

    public void show() {
        dialog = builder.create();
        itemNameInput.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    private boolean itemNameInputChange(TextView textView, int i, KeyEvent keyEvent) {
//        categoryViewModel.getCategoryByRuleName(itemNameInput.getText().toString(), object -> {
//            if (object == null) {
//                spinner.setSelection(categories.size()-2);
//                return;
//            }
//            Category category = (Category) object;
//            newCategoryEdit.getText().toString();
//            spinner.setSelection(categories.indexOf(category.name));
//        });
        return true;
    }

    private boolean newCategoryInputChange(TextView textView, int i, KeyEvent keyEvent) {
//        String newItemName = itemNameInput.getText().toString();
//        String newCategoryName = newCategoryEdit.getText().toString();
//        ruleAddCheckbox.setVisibility(View.VISIBLE);
//        ruleAddCheckbox.setChecked(true);
//        ruleAddText.setVisibility(View.VISIBLE);
//        ruleAddText.setText(String.format("%s > %s", newItemName, newCategoryName));
//        ruleViewModel.getRuleWithCategory(newItemName, object -> {
//            RuleWithCategory ruleWithCategory = (RuleWithCategory) object;
//            if (ruleWithCategory == null) {
//                ruleToDelete = null;
//                return;
//            }
//            ruleToDelete = ruleWithCategory.rule;
//            Category category = ruleWithCategory.category;
//
//            ruleDeleteCheckbox.setChecked(true);
//            ruleDeleteCheckbox.setVisibility(View.VISIBLE);
//            SpannableString spannableString = new SpannableString(String.format("%s -> %s", ruleToDelete.name, category.name));
//            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), 0);
//            ruleDeleteText.setText(spannableString);
//            ruleDeleteText.setVisibility(View.VISIBLE);
//
//        });
        return true;
    }

    private List<String> getCategoryStringList() {
//        List<CategoryWithItems> categoriesWithItems = ShoppingFragment.instance.categoriesWithItems;
//        List<String> categories = new ArrayList<>(categoriesWithItems.size()+1);
//        for (CategoryWithItems categoryWithItems : categoriesWithItems) categories.add(categoryWithItems.category.name);
//        categories.add("None");
//        categories.add("New");
//        return categories;
        return new ArrayList<String>();
    }

//    private Category getCategoryByPosition(int position) {
//        if (position != ShoppingFragment.instance.categoriesWithItems.size()) return ShoppingFragment.instance.categoriesWithItems.get(position).category;
//        else return null;
//    }

    @Override
    public void onClick(View view) {
//        String newItemName = itemNameInput.getText().toString();
//        if (newItemName.isEmpty()) {
//            Toast.makeText(ShoppingFragment.instance.getContext(), "Please fill out the Item Name", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        int size = categories.size();
//        int position = spinner.getSelectedItemPosition();
//        if (position == size-1) { // New
//            String newCategoryName = newCategoryEdit.getText().toString();
//            if (newCategoryName.isEmpty()) {
//                Toast.makeText(ShoppingFragment.instance.getContext(), "Please fill out the new Category or select another Category Option", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (itemToEdit == null) itemViewModel.insert(new Item(newItemName), new Category(newCategoryName), ruleToDelete, ruleDeleteCheckbox.isChecked(), ruleAddCheckbox.isChecked());
//            else {
//                itemToEdit.name = newItemName;
//                itemViewModel.update(itemToEdit, new Category(newCategoryName), ruleToDelete, ruleDeleteCheckbox.isChecked(), ruleAddCheckbox.isChecked());
//            }
//        } else if (position == size-2) { // None
//            if (itemToEdit == null) itemViewModel.insert(new Item(newItemName), null, ruleToDelete, ruleDeleteCheckbox.isChecked(), false);
//            else {
//                itemToEdit.name = newItemName;
//                itemToEdit.categoryId = null;
//                itemViewModel.update(itemToEdit, null, ruleToDelete, ruleDeleteCheckbox.isChecked(), false);
//            }
//        } else { // existing Category
//            Category category = getCategoryByPosition(position);
//            if (itemToEdit == null) itemViewModel.insert(new Item(newItemName, category.id), null, ruleToDelete, ruleDeleteCheckbox.isChecked(), ruleAddCheckbox.isChecked());
//            else {
//                itemToEdit.name = newItemName;
//                itemToEdit.categoryId = category.id;
//                itemViewModel.update(itemToEdit, null, ruleToDelete, ruleDeleteCheckbox.isChecked(), ruleAddCheckbox.isChecked());
//            }
//        }
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog.dismiss();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//        String newItemName = itemNameInput.getText().toString();
//        if (newItemName.isEmpty()) return;
//        int size = categories.size();
//        if (position == size-1) { // New
//            newCategoryText.setVisibility(View.VISIBLE);
//            newCategoryEdit.setText("");
//            newCategoryEdit.setVisibility(View.VISIBLE);
//            ruleAddCheckbox.setVisibility(View.GONE);
//            ruleAddText.setVisibility(View.GONE);
//        } else if (position == size-2){ // None
//            newCategoryText.setVisibility(View.GONE);
//            newCategoryEdit.setVisibility(View.GONE);
//            ruleAddCheckbox.setVisibility(View.GONE);
//            ruleAddText.setVisibility(View.GONE);
//            ruleViewModel.getRuleWithCategory(newItemName, object -> {
//                RuleWithCategory ruleWithCategory = (RuleWithCategory) object;
//                if (ruleWithCategory == null) {
//                    ruleDeleteCheckbox.setVisibility(View.GONE);
//                    ruleDeleteText.setVisibility(View.GONE);
//                    ruleToDelete = null;
//                    return;
//                }
//                ruleToDelete = ruleWithCategory.rule;
//                Category category = ruleWithCategory.category;
//
//                ruleDeleteCheckbox.setVisibility(View.VISIBLE);
//                ruleDeleteCheckbox.setChecked(true);
//                SpannableString spannableString = new SpannableString(String.format("%s -> %s", ruleToDelete.name, category.name));
//                spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), 0);
//                ruleDeleteText.setVisibility(View.VISIBLE);
//                ruleDeleteText.setText(spannableString);
//            });
//        } else { // existing category
//            newCategoryText.setVisibility(View.GONE);
//            newCategoryEdit.setVisibility(View.GONE);
//
//            ruleViewModel.getRuleWithCategory(newItemName, object -> {
//                RuleWithCategory ruleWithCategory = (RuleWithCategory) object;
//                Category categorySelected = getCategoryByPosition(position);
//
//                if (ruleWithCategory == null) {
//                    ruleToDelete = null;
//                    ruleDeleteCheckbox.setVisibility(View.GONE);
//                    ruleDeleteText.setVisibility(View.GONE);
//                    ruleAddCheckbox.setChecked(true);
//                    ruleAddCheckbox.setVisibility(View.VISIBLE);
//                    ruleAddText.setText(String.format("%s > %s", newItemName, categorySelected.name));
//                    ruleAddText.setVisibility(View.VISIBLE);
//                } else if (ruleWithCategory.category.id != categorySelected.id) {
//                    ruleToDelete = ruleWithCategory.rule;
//                    ruleDeleteCheckbox.setChecked(true);
//                    SpannableString spannableString = new SpannableString(String.format("%s -> %s", ruleToDelete.name, ruleWithCategory.category.name));
//                    spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), 0);
//                    ruleDeleteCheckbox.setVisibility(View.VISIBLE);
//                    ruleDeleteText.setText(spannableString);
//                    ruleDeleteText.setVisibility(View.VISIBLE);
//
//                    ruleAddCheckbox.setChecked(true);
//                    ruleAddCheckbox.setVisibility(View.VISIBLE);
//                    ruleAddText.setText(String.format("%s > %s", newItemName, categorySelected.name));
//                    ruleAddText.setVisibility(View.VISIBLE);
//                } else {
//                    ruleToDelete = null;
//                    ruleDeleteCheckbox.setVisibility(View.GONE);
//                    ruleDeleteText.setVisibility(View.GONE);
//                    ruleAddCheckbox.setVisibility(View.GONE);
//                    ruleAddText.setVisibility(View.GONE);
//                }
//            });
//        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
//        if (ruleDeleteCheckbox != compoundButton) return;
//
//        if (checked) ruleDeleteCheckbox.setButtonTintList(ColorStateList.valueOf(ShoppingFragment.instance.getContext().getResources().getColor(R.color.red)));
//        else ruleDeleteCheckbox.setButtonTintList(ColorStateList.valueOf(ShoppingFragment.instance.getContext().getResources().getColor(R.color.gray_default_checkbox)));
    }
}
