package com.jschiefner.shoppinglist;

import android.content.res.ColorStateList;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class ItemDialog implements View.OnClickListener, Spinner.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    EditText itemNameInput;
    TextView newCategoryText;
    EditText newCategoryEdit;
    Spinner spinner;
    Button saveButton;
    CheckBox ruleDeleteCheckbox;
    TextView ruleDeleteText;
    CheckBox ruleAddCheckbox;
    TextView ruleAddText;

    AlertDialog.Builder builder;
    AlertDialog dialog;
    View layout;

    Category categorySelected;
    Category foundCategory;
    List<Category> categories;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference categoriesRef = db.collection("categories");
    static final String RULE_FORMAT = "%s > %s";

    ItemDialog() {
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

        saveButton.setOnClickListener(this);
        itemNameInput.setOnEditorActionListener(this::itemNameInputChange);
        newCategoryEdit.setOnEditorActionListener(this::newCategoryInputChange);
        spinner.setOnItemSelectedListener(this);
        ruleDeleteCheckbox.setOnCheckedChangeListener(this);
        ruleDeleteText.setOnClickListener(v -> ruleDeleteCheckbox.toggle());
        ruleAddText.setOnClickListener(v -> ruleAddCheckbox.toggle());
    }

    // name text field changes
    boolean itemNameInputChange(TextView textView, int i, KeyEvent keyEvent) {
        String input = itemNameInput.getText().toString().toLowerCase();
        if (input.isEmpty()) {
            ruleAddCheckbox.setChecked(false);
            ruleAddCheckbox.setVisibility(GONE);
            ruleDeleteCheckbox.setChecked(false);
            ruleDeleteCheckbox.setVisibility(GONE);
            ruleAddText.setVisibility(GONE);
            ruleDeleteText.setVisibility(GONE);
            return true;
        }
        categoriesRef // TODO: maybe search in categories list already fetched but its easier with whereArrayContains of course
                .whereArrayContains("rules", input)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        if (spinner.getSelectedItemPosition() != categories.size()) {
                            // stands on existing category
                            Category category = categories.get(spinner.getSelectedItemPosition());
                            if (category.isDefault()) {
                                ruleAddCheckbox.setChecked(false);
                                ruleAddCheckbox.setVisibility(GONE);
                                ruleAddText.setVisibility(GONE);
                            } else {
                                ruleAddCheckbox.setChecked(true);
                                ruleAddCheckbox.setVisibility(VISIBLE);
                                ruleAddText.setText(String.format(RULE_FORMAT, input.toLowerCase(), categorySelected.getName()));
                                ruleAddText.setVisibility(VISIBLE);
                            }
                        }
                        ruleDeleteCheckbox.setChecked(false);
                        ruleDeleteCheckbox.setVisibility(GONE);
                        ruleDeleteText.setVisibility(GONE);
                    } else {
                        String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                        for (int position = 0; position < categories.size(); position++) {
                            if (categories.get(position).getId().equals(id)) spinner.setSelection(position);
                        }
                        ruleAddCheckbox.setChecked(false);
                        ruleAddCheckbox.setVisibility(GONE);
                        ruleAddText.setVisibility(GONE);
                    }
                });
        return true;
    }

    // new category text field changes
    boolean newCategoryInputChange(TextView textView, int i, KeyEvent keyEvent) {
        String newItemName = itemNameInput.getText().toString().toLowerCase();
        String newCategoryName = newCategoryEdit.getText().toString();
        ruleAddCheckbox.setVisibility(VISIBLE);
        ruleAddCheckbox.setChecked(true);
        ruleAddText.setText(String.format(RULE_FORMAT, newItemName, newCategoryName));
        ruleAddText.setVisibility(VISIBLE);
        if (ruleDeleteCheckbox.getVisibility() == VISIBLE) {
            ruleDeleteCheckbox.setChecked(true);
        }
        return true;
    }

    void show() {
        dialog = builder.create();
        itemNameInput.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    // Spinner item selected
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String newItemName = itemNameInput.getText().toString();
        if (newItemName.isEmpty()) return;
        int size = categories.size();

        if (position == size) { // New
            newCategoryText.setVisibility(VISIBLE);
            newCategoryEdit.setText("");
            newCategoryEdit.setVisibility(VISIBLE);
            ruleAddCheckbox.setChecked(true);
            categorySelected = null;
        } else { // existing category
            newCategoryText.setVisibility(View.GONE);
            newCategoryEdit.setVisibility(View.GONE);
            newCategoryText.setVisibility(GONE);
            categorySelected = categories.get(position);
            if (categorySelected.isDefault()) {
                ruleAddCheckbox.setVisibility(GONE);
                ruleAddCheckbox.setChecked(false);
                ruleDeleteCheckbox.setVisibility(GONE);
                ruleDeleteCheckbox.setChecked(false);
                ruleAddText.setVisibility(GONE);
                ruleDeleteText.setVisibility(GONE);
                return;
            }
        }

        String newRuleName = newItemName.toLowerCase();
        categoriesRef.whereArrayContains("rules", newRuleName).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) { // keine rule bisher
                        ruleDeleteCheckbox.setChecked(false);
                        ruleDeleteCheckbox.setVisibility(GONE);
                        ruleDeleteText.setVisibility(GONE);
                        if (position != size) {
                            ruleAddText.setText(String.format(RULE_FORMAT, newRuleName, categorySelected.getName()));
                            ruleAddText.setVisibility(VISIBLE);
                            ruleAddCheckbox.setChecked(true);
                            ruleAddCheckbox.setVisibility(VISIBLE);
                        } else {
                            ruleAddText.setVisibility(GONE);
                            ruleAddCheckbox.setVisibility(GONE);
                        }
                    } else { // es existiert eine rule
                        DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                        foundCategory = snapshot.toObject(Category.class);
                        foundCategory.setId(snapshot.getId());
                        if (position == size || !foundCategory.getId().equals(categorySelected.getId())) {
                            // es gibt eine rule auf eine andere category
                            ruleDeleteCheckbox.setChecked(true);
                            ruleDeleteCheckbox.setVisibility(VISIBLE);
                            ruleDeleteText.setText(String.format(RULE_FORMAT, newRuleName, foundCategory.getName()));
                            ruleDeleteText.setVisibility(VISIBLE);
                            if (position == size) { // New
                                ruleAddCheckbox.setChecked(true);
                                ruleAddCheckbox.setVisibility(GONE);
                                ruleAddText.setVisibility(GONE);
                            } else {
                                ruleAddCheckbox.setChecked(true);
                                ruleAddCheckbox.setVisibility(VISIBLE);
                                ruleAddText.setText(String.format(RULE_FORMAT, newRuleName, categorySelected.getName()));
                                ruleAddText.setVisibility(VISIBLE);
                            }
                        } else {
                            // es gibt eine rule auf die selected category
                            ruleDeleteCheckbox.setChecked(false);
                            ruleDeleteCheckbox.setVisibility(View.GONE);
                            ruleDeleteText.setVisibility(View.GONE);
                            ruleAddCheckbox.setVisibility(View.GONE);
                            ruleAddText.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // pass
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (ruleDeleteCheckbox != compoundButton) return;

        if (checked) ruleDeleteCheckbox.setButtonTintList(ColorStateList.valueOf(ShoppingFragment.instance.getContext().getResources().getColor(R.color.red)));
        else ruleDeleteCheckbox.setButtonTintList(ColorStateList.valueOf(ShoppingFragment.instance.getContext().getResources().getColor(R.color.gray_default_checkbox)));
    }

    boolean emptyInputWarned(String input, String what) {
        if (input.isEmpty()) {
            Toast.makeText(ShoppingFragment.instance.getContext(), "Please fill out the " + what + " name", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
