package com.jschiefner.shoppinglist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class RuleFragment extends Fragment {
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private RuleAdapter adapter;
    private DocumentReference documentRef;
    private Category category;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.rule_fragment, container, false);

        category = MainActivity.instance.getCategory();
        documentRef = db.collection("categories").document(category.getId());
        MainActivity.instance.setActionBarTitle(R.string.rule_fragment_label);

        // init recycler view
        recyclerView = rootView.findViewById(R.id.rules_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // recyclerView.setHasFixedSize(true); // skip for now
        adapter = new RuleAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, swipeFlags) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                category.getRules().remove(viewHolder.getAdapterPosition());
                documentRef.set(category, SetOptions.merge());
                adapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(recyclerView);

        fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(view -> handleFabClick());

        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void handleFabClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.new_dialog_with_text, null);
        TextView topText = view.findViewById(R.id.new_text_top_description);
        final EditText ruleNameInput = view.findViewById(R.id.new_text_text_input);
        Button saveButton = view.findViewById(R.id.new_text_button);

        // set text in new Item Dialog
        topText.setText(R.string.new_rule);
        saveButton.setText(R.string.button_save);

        // open Dialog
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        ruleNameInput.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        saveButton.setOnClickListener(view1 -> {
            addRule(ruleNameInput.getText().toString().toLowerCase());
            dialog.dismiss();
        });

        ruleNameInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            addRule(ruleNameInput.getText().toString().toLowerCase());
            dialog.dismiss();
            return true;
        });
    }

    private void addRule(String name) {
        category.getRules().add(name.toLowerCase());
        documentRef.set(category, SetOptions.merge());
    }
}
