package com.jschiefner.shoppinglist;

import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class EditItemDialog extends ItemDialog {
    final Item item;

    EditItemDialog(Item item) {
        super();
        this.item = item;
        TextView title = layout.findViewById(R.id.item_dialog_title);
        title.setText(R.string.edit_item_head);
        itemNameInput.setText(item.getName());
        categoriesRef.orderBy("position", Query.Direction.ASCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
            categories = new ArrayList<>(queryDocumentSnapshots.size());
            List<String> categoryStrings = new ArrayList<>(queryDocumentSnapshots.size()+1);
            int counter = 0, position = 0;
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Category category = documentSnapshot.toObject(Category.class);
                category.setId(documentSnapshot.getId());
                categories.add(category);
                categoryStrings.add(category.getName());

                if (item.getCategory().getId().equals(category.getId())) position = counter;
                counter += 1;
            }
            categoryStrings.add("New");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ShoppingFragment.instance.getContext(), R.layout.support_simple_spinner_dropdown_item, categoryStrings);
            spinner.setAdapter(adapter);
            spinner.setSelection(position);
            categorySelected = categories.get(spinner.getSelectedItemPosition());
        });
    }

    @Override
    public void onClick(View v) {
        String newItemName = itemNameInput.getText().toString();
        if (emptyInputWarned(newItemName)) return;

        int size = categories.size();
        int position = spinner.getSelectedItemPosition();
        item.setName(newItemName);

        String newRuleName = newItemName.toLowerCase();
        if (position == size) { // new category
            String newCategoryName = newCategoryEdit.getText().toString();
            Category category;
            if (ruleAddCheckbox.isChecked()) category = new Category(newCategoryName, newRuleName); // rule added to new category
            else category = new Category(newCategoryName); // rule not added
            categoriesRef.add(category).addOnSuccessListener(documentReference -> {
                item.getReference().delete();
                item.setReference(null);
                item.setId(null);
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
            if (categorySelected.getId().equals(item.getCategory().getId())) {
                // item is already in correct category, just update the title
                item.getReference().set(item);
            } else {
                // item goes into other existing category
                item.getReference().delete();
                categoriesRef.document(categorySelected.getId()).collection("items").add(item);
            }
        }

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog.dismiss();
    }
}
