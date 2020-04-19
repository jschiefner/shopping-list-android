package com.jschiefner.shoppinglist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jschiefner.shoppinglist.database.Rule;
import com.jschiefner.shoppinglist.database.RuleViewAdapter;
import com.jschiefner.shoppinglist.database.RuleViewModel;
import com.jschiefner.shoppinglist.database.RuleViewModelFactory;

import java.util.List;

public class RuleFragment extends Fragment {
    private RecyclerView recyclerView;
    private RuleViewModel ruleViewModel;
    private final RuleViewAdapter adapter = new RuleViewAdapter();
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.rule_fragment, container, false);

        recyclerView = rootView.findViewById(R.id.rules_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        MainActivity mainActivity = (MainActivity) getActivity();
        long categoryId = mainActivity.getCategoryID();
        ruleViewModel = ViewModelProviders.of(this, new RuleViewModelFactory(getActivity().getApplication(), categoryId)).get(RuleViewModel.class);
        ruleViewModel.getCategoryRules().observe(this, new Observer<List<Rule>>() {
            @Override
            public void onChanged(List<Rule> rules) {
                adapter.setCategoryRules(rules);
            }
        });

        fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFabClick();
            }
        });

        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void handleFabClick() {
        // handle fab click
    }
}
