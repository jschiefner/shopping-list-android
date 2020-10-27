package com.jschiefner.shoppinglist;

import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

// TODO: make NewITemDialog and EditItemDialog inherit from ItemDialog (abstract) where common methods such as itemNameInputChange are defined
public class NewItemDialog extends ItemDialog {
    public NewItemDialog() {
        super();
        categoriesRef.orderBy("position", Query.Direction.ASCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
            categories = new ArrayList<>(queryDocumentSnapshots.size());
            List<String> categoryStrings = new ArrayList<>(queryDocumentSnapshots.size()+1);
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Category category = documentSnapshot.toObject(Category.class);
                category.setId(documentSnapshot.getId());
                categories.add(category);
                categoryStrings.add(category.getName());
            }
            categoryStrings.add("New");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ShoppingFragment.instance.getContext(), R.layout.support_simple_spinner_dropdown_item, categoryStrings);
            spinner.setAdapter(adapter);
            categorySelected = categories.get(spinner.getSelectedItemPosition());
        });
    }

    // Button click
    @Override
    public void onClick(View view) {
        String newItemName = itemNameInput.getText().toString();
        if (emptyInputWarned(newItemName)) return;

        int size = categories.size();
        int position = spinner.getSelectedItemPosition();

        String newRuleName = newItemName.toLowerCase();
        if (position == size) { // new category
            String newCategoryName = newCategoryEdit.getText().toString();
            Category category;
            if (ruleAddCheckbox.isChecked()) category = new Category(newCategoryName, newRuleName); // rule added to new category
            else category = new Category(newCategoryName); // rule not added
            categoriesRef.add(category).addOnSuccessListener(documentReference -> {
                Item item = new Item(newItemName, false);
                documentReference.collection("items").add(item);
            });
            if (ruleDeleteCheckbox.isChecked()) {
                foundCategory.deleteRule(newRuleName);
                categoriesRef.document(foundCategory.getId()).set(foundCategory);
            }
        } else { // existing category
            if (ruleAddCheckbox.isChecked()) {
                categorySelected.addRule(newRuleName);
                categoriesRef.document(categorySelected.getId()).set(categorySelected);
            }
            if (ruleDeleteCheckbox.isChecked()) {
                foundCategory.deleteRule(newRuleName);
                categoriesRef.document(foundCategory.getId()).set(foundCategory);
            }
            Item item = new Item(newItemName, false);
            categoriesRef.document(categorySelected.getId()).collection("items").add(item);
        }

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog.dismiss();
    }
}
