package com.jschiefner.shoppinglist;

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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    private AlertDialog dialog;
    private View layout;

    private Item itemToEdit;
    private String ruleToAdd;
    private String ruleToDelete;
    private Category categoryToAddTo;
    private Category categoryToDeleteFrom;
    private Category categorySelected;
    private List<Category> categories;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference categoriesRef = db.collection("categories");
    private static final String RULE_FORMAT = "%s > %s";

    public ItemDialog() {
        builder = new AlertDialog.Builder(ShoppingFragment.instance.getContext());
        layout = ShoppingFragment.instance.getLayoutInflater().inflate(R.layout.item_dialog, null);
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

        categoriesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            categories = new ArrayList<>(queryDocumentSnapshots.size());
            List<String> categoryStrings = new ArrayList<>(queryDocumentSnapshots.size()+1);
            int counter = 0, position = 0;
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Category category = documentSnapshot.toObject(Category.class);
                category.setId(documentSnapshot.getId());
                categories.add(category);
                categoryStrings.add(category.getName());

                // remember position for spinner
                if (itemToEdit != null) {
                    if (itemToEdit.getCategory().getId().equals(category.getId())) position = counter;
                    counter += 1;
                }
            }
            categoryStrings.add("New");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ShoppingFragment.instance.getContext(), R.layout.support_simple_spinner_dropdown_item, categoryStrings);
            spinner.setAdapter(adapter);
            if (itemToEdit != null) spinner.setSelection(position);
            categorySelected = categories.get(spinner.getSelectedItemPosition());
        });
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
        TextView title = layout.findViewById(R.id.item_dialog_title);
        title.setText(R.string.edit_item_head);
        itemNameInput.setText(item.getName());
    }

    public void show() {
        dialog = builder.create();
        itemNameInput.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    private boolean itemNameInputChange(TextView textView, int i, KeyEvent keyEvent) {
        String input = itemNameInput.getText().toString().toLowerCase();
        categoriesRef // TODO: maybe search in categories list already fetched but its easier with whereArrayContains of course
                .whereArrayContains("rules", input)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) return;
                    String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                    for (int position = 0; position < categories.size(); position++) {
                        if (categories.get(position).getId().equals(id)) spinner.setSelection(position);
                    }
                });
        return true;
    }

    public boolean newCategoryInputChange(TextView textView, int i, KeyEvent keyEvent) {
        String newItemName = itemNameInput.getText().toString();
        String newCategoryName = newCategoryEdit.getText().toString();
        ruleAddCheckbox.setVisibility(View.VISIBLE);
        ruleAddCheckbox.setChecked(true);
        ruleAddText.setVisibility(View.VISIBLE);
        ruleAddText.setText(String.format("%s > %s", newItemName, newCategoryName));
        if (ruleDeleteCheckbox.getVisibility() == View.VISIBLE) {
            ruleDeleteCheckbox.setChecked(true);
        }
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

    // Button click
    @Override
    public void onClick(View view) {
        String newItemName = itemNameInput.getText().toString();
        if (newItemName.isEmpty()) {
            Toast.makeText(ShoppingFragment.instance.getContext(), "Please fill out the Item Name", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: old from here
//        int size = categories.size();
//        int position = spinner.getSelectedItemPosition();
//        if (position == size-1) { // New Category chosen!
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
//        } else if (position == size-2) { // TODO: none ignore
////            if (itemToEdit == null) itemViewModel.insert(new Item(newItemName), null, ruleToDelete, ruleDeleteCheckbox.isChecked(), false);
////            else {
////                itemToEdit.name = newItemName;
////                itemToEdit.categoryId = null;
////                itemViewModel.update(itemToEdit, null, ruleToDelete, ruleDeleteCheckbox.isChecked(), false);
////            }
//        } else { // existing Category
//            Category category = getCategoryByPosition(position);
//            if (itemToEdit == null) itemViewModel.insert(new Item(newItemName, category.id), null, ruleToDelete, ruleDeleteCheckbox.isChecked(), ruleAddCheckbox.isChecked());
//            else {
//                itemToEdit.name = newItemName;
//                itemToEdit.categoryId = category.id;
//                itemViewModel.update(itemToEdit, null, ruleToDelete, ruleDeleteCheckbox.isChecked(), ruleAddCheckbox.isChecked());
//            }
//        }
        // TODO: old to here, new from nere

        int size = categories.size();
        int position = spinner.getSelectedItemPosition();

        String rule = newItemName.toLowerCase();
        if (position == size) { // New Category
            Category category = new Category(newCategoryEdit.getText().toString(), rule); // rule added to new category
            categoriesRef.add(category).addOnSuccessListener(documentReference -> {
                if (itemToEdit == null) { // new item
                    Item item = new Item(newItemName, false);
                    documentReference.collection("items").add(item);
                } else { // existing item
                    itemToEdit.setName(newItemName);
                    Category oldCategory = itemToEdit.getCategory();
                    itemToEdit.getReference().set(itemToEdit);
                    CollectionReference oldCategoryItemsReference = categoriesRef.document(oldCategory.getId()).collection("items");
                    oldCategoryItemsReference.document(itemToEdit.getId()).delete(); // delete from old category
                    documentReference.collection("items").add(itemToEdit);
                }
            });
        } else { // existing category
            if (ruleAddCheckbox.isChecked()) { // add new rule
                categorySelected.addRule(rule);
                categoriesRef.document(categorySelected.getId()).set(categorySelected);
            }
            if (ruleDeleteCheckbox.isChecked()) { // delete rule
                categoryToDeleteFrom.deleteRule(rule);
                categoriesRef.document(categoryToDeleteFrom.getId()).set(categoryToDeleteFrom);
            }
            if (itemToEdit == null) { // new item
                Item item = new Item(newItemName, false);
                categoriesRef.document(categorySelected.getId()).collection("items").add(item);
            } else {
                itemToEdit.setName(newItemName);
                categoriesRef.document(categorySelected.getId()).collection("items").document(itemToEdit.getId()).set(itemToEdit);
            }
        }

        // TODO: new ends
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog.dismiss();
    }

    // Spinner item selected
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String newItemName = itemNameInput.getText().toString();
        if (newItemName.isEmpty()) return;
        int size = categories.size();
        if (position == size) { // New
            newCategoryText.setVisibility(View.VISIBLE);
            newCategoryEdit.setText("");
            newCategoryEdit.setVisibility(View.VISIBLE);
            ruleAddCheckbox.setChecked(true);
            ruleDeleteCheckbox.setChecked(false);
        } else { // existing category
            newCategoryText.setVisibility(View.GONE);
            newCategoryEdit.setVisibility(View.GONE);
            categorySelected = categories.get(position);
        }
        ruleToAdd = newItemName.toLowerCase();
        String rule = newItemName.toLowerCase();
        categoriesRef.whereArrayContains("rules", rule).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // es gibt bisher keine rule
                        ruleDeleteCheckbox.setChecked(false);
                        ruleDeleteCheckbox.setVisibility(View.GONE);
                        ruleDeleteText.setVisibility(View.GONE);
                        ruleAddCheckbox.setChecked(true);
                        ruleAddCheckbox.setVisibility(View.VISIBLE);
                        ruleAddText.setText(String.format(RULE_FORMAT, rule, categorySelected.getName()));
                        ruleAddText.setVisibility(View.VISIBLE);
                        ruleToAdd = rule;
                    } else {
                        DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Category foundCategory = snapshot.toObject(Category.class);
                        foundCategory.setId(snapshot.getId());
                        if (foundCategory.getId().equals(categorySelected.getId())) {
                            // es gibt eine rule auf die selected category
                            ruleToDelete = null; // TODO: mehr auf null setzen? an anderen stellen?
                            ruleDeleteCheckbox.setChecked(false);
                            ruleDeleteCheckbox.setVisibility(View.GONE);
                            ruleDeleteText.setVisibility(View.GONE);
                            ruleAddCheckbox.setVisibility(View.GONE);
                            ruleAddText.setVisibility(View.GONE);
                            // ruleToADd etc null?
                        } else {
                            // es gibt eine rule auf eine andere category
                            categoryToDeleteFrom = foundCategory;
                            ruleDeleteCheckbox.setChecked(true);
                            ruleDeleteText.setText(String.format(RULE_FORMAT, rule, foundCategory.getName()));
                            ruleAddCheckbox.setChecked(true);
                            ruleAddText.setText(String.format(RULE_FORMAT, rule, categorySelected.getName()));
                            ruleDeleteCheckbox.setVisibility(View.VISIBLE);
                            ruleDeleteText.setVisibility(View.VISIBLE);
                            ruleAddCheckbox.setVisibility(View.VISIBLE);
                            ruleAddText.setVisibility(View.VISIBLE);
                        }
                    }
                });
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
