package com.jschiefner.shoppinglist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jschiefner.shoppinglist.database.CategoryViewModel;
import com.jschiefner.shoppinglist.database.CategoryViewModelFactory;
import com.jschiefner.shoppinglist.database.CategoryWithItems;
import com.jschiefner.shoppinglist.database.CategoryItemViewAdapter;
import com.jschiefner.shoppinglist.database.Item;
import com.jschiefner.shoppinglist.database.ItemViewModel;
import com.jschiefner.shoppinglist.database.ItemViewModelFactory;
import com.jschiefner.shoppinglist.database.ItemSwipeTouchHelper;
import com.jschiefner.shoppinglist.database.RuleViewModel;
import com.jschiefner.shoppinglist.database.RuleViewModelFactory;
import com.jschiefner.shoppinglist.database.UncategorizedItemViewAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingFragment extends Fragment {
    private RecyclerView categorizedRecyclerView;
    private RecyclerView uncategorizedRecyclerView;
    private LinearLayout emptyCartLayout;
    private TextView uncategorizedItemsHeader;
    public ItemViewModel itemViewModel;
    private CategoryViewModel categoryViewModel;
    private RuleViewModel ruleViewModel;
    private final CategoryItemViewAdapter categorizedItemsAdapter = new CategoryItemViewAdapter();
    private UncategorizedItemViewAdapter uncategorizedItemViewAdapter = new UncategorizedItemViewAdapter();
    private FloatingActionButton fab;
    public static ShoppingFragment instance;
    public List<CategoryWithItems> categoriesWithItems = new ArrayList<>();
    public List<Item> uncategorizedItems = new ArrayList<>();
    private final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        instance = this;
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.shopping_fragment, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setActionBarTitle(R.string.shopping_fragment_label);

        uncategorizedItemsHeader = rootView.findViewById(R.id.uncategorized_items);
        emptyCartLayout = rootView.findViewById(R.id.empty_cart_layout);

        categorizedRecyclerView = rootView.findViewById(R.id.items_recycler_view);
        categorizedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categorizedRecyclerView.setHasFixedSize(true);
        categorizedRecyclerView.setAdapter(categorizedItemsAdapter);

        uncategorizedRecyclerView = rootView.findViewById(R.id.uncategorized_recycler_view);
        uncategorizedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        uncategorizedRecyclerView.setHasFixedSize(true);
        uncategorizedRecyclerView.setAdapter(uncategorizedItemViewAdapter);

        itemViewModel = ViewModelProviders.of(this, new ItemViewModelFactory(getActivity().getApplication())).get(ItemViewModel.class);
        ruleViewModel = ViewModelProviders.of(this, new RuleViewModelFactory(getActivity().getApplication(), -1L)).get(RuleViewModel.class);

        // Setup Item List
        categoryViewModel = ViewModelProviders.of(this, new CategoryViewModelFactory(getActivity().getApplication())).get(CategoryViewModel.class);
        categoryViewModel.getCategoriesWithItems().observe(this, items -> {
            categoriesWithItems = items;
            categorizedItemsAdapter.update();
            updateView();
        });
        itemViewModel.getUncategorizedItems().observe(this, items -> {
            uncategorizedItems = items;
            uncategorizedItemViewAdapter.notifyDataSetChanged();
            if (items.isEmpty()) uncategorizedItemsHeader.setVisibility(View.GONE);
            else uncategorizedItemsHeader.setVisibility(View.VISIBLE);
            updateView();
        });

        new ItemTouchHelper(new ItemSwipeTouchHelper(this, categorizedItemsAdapter, 0, swipeFlags)).attachToRecyclerView(categorizedRecyclerView);
        new ItemTouchHelper(new ItemSwipeTouchHelper(this, uncategorizedItemViewAdapter, 0, swipeFlags)).attachToRecyclerView(uncategorizedRecyclerView);

        fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(view -> handleFabClick());

        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        instance = this;
    }

    @Override
    public void onPause() {
        super.onPause();
        instance = null;
    }

    public void handleFabClick() {
        new ItemDialog().show();
    }

    private void updateView() {
        if (categorizedItemsAdapter.getItemCount() == 0 && uncategorizedItems.isEmpty()) emptyCartLayout.setVisibility(View.VISIBLE);
        else emptyCartLayout.setVisibility(View.GONE);
    }
}
