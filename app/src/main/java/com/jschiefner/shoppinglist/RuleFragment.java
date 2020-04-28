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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jschiefner.shoppinglist.database.Rule;
import com.jschiefner.shoppinglist.database.RuleViewAdapter;
import com.jschiefner.shoppinglist.database.RuleViewModel;
import com.jschiefner.shoppinglist.database.RuleViewModelFactory;

public class RuleFragment extends Fragment {
    private RecyclerView recyclerView;
    private RuleViewModel ruleViewModel;
    private final RuleViewAdapter adapter = new RuleViewAdapter();
    private FloatingActionButton fab;

    private final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.rule_fragment, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setActionBarTitle(R.string.rule_fragment_label);

        recyclerView = rootView.findViewById(R.id.rules_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        long categoryId = mainActivity.getCategoryID();
        ruleViewModel = ViewModelProviders.of(this, new RuleViewModelFactory(getActivity().getApplication(), categoryId)).get(RuleViewModel.class);
        ruleViewModel.getCategoryRules().observe(this, adapter::setCategoryRules);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, swipeFlags) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                RuleViewAdapter.RuleViewHolder holder = (RuleViewAdapter.RuleViewHolder) viewHolder;
                Rule rule = holder.rule;
                ruleViewModel.delete(rule);
            }
        }).attachToRecyclerView(recyclerView);

        fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(view -> handleFabClick());

        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void handleFabClick() {
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
            addRule(ruleNameInput.getText().toString());
            dialog.dismiss();
        });

        ruleNameInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            addRule(ruleNameInput.getText().toString());
            dialog.dismiss();
            return true;
        });
    }

    private void addRule(String name) {
        MainActivity mainActivity = (MainActivity) getActivity();
        Rule rule = new Rule(name, mainActivity.getCategoryID());
        ruleViewModel.insert(rule);
    }
}
