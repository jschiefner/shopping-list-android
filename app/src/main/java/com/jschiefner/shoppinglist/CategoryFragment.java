package com.jschiefner.shoppinglist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jschiefner.shoppinglist.database.Category;
import com.jschiefner.shoppinglist.database.CategoryViewAdapter;
import com.jschiefner.shoppinglist.database.CategoryViewModel;
import com.jschiefner.shoppinglist.database.CategoryViewModelFactory;
import com.jschiefner.shoppinglist.database.Item;
import com.jschiefner.shoppinglist.database.ItemViewAdapter;
import com.jschiefner.shoppinglist.database.ItemViewModel;
import com.jschiefner.shoppinglist.database.ItemViewModelFactory;

import java.util.List;

public class CategoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryViewModel categoryViewModel;
    private final CategoryViewAdapter adapter = new CategoryViewAdapter();
    private FloatingActionButton fab;
    public static CategoryFragment instance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.category_fragment, container, false);

        recyclerView = rootView.findViewById(R.id.categories_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        categoryViewModel = ViewModelProviders.of(this, new CategoryViewModelFactory(getActivity().getApplication())).get(CategoryViewModel.class);
        categoryViewModel.getCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                adapter.setCategories(categories);
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

    @Override
    public void onResume() {
        super.onResume();
        Log.i("CUSTOM", "onresume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("CUSTOM", "onpause");
        instance = null;
    }

    public void handleFabClick() {
        // handle fab click
    }
}
