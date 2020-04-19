package com.jschiefner.shoppinglist.database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jschiefner.shoppinglist.R;

import java.util.ArrayList;
import java.util.List;

public class RuleViewAdapter extends RecyclerView.Adapter<RuleViewAdapter.RuleViewHolder> {
    private List<Rule> categoryRules = new ArrayList<>();

    public static class RuleViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        public Rule rule;

        public RuleViewHolder(@NonNull View ruleView) {
            super(ruleView);

            textView = ruleView.findViewById(R.id.recycler_text_view);
        }
    }

    public void setCategoryRules(List<Rule> categoryRules) {
        this.categoryRules = categoryRules;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rule_recycler_view, parent, false);
        return new RuleViewAdapter.RuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RuleViewHolder holder, int position) {
        Rule rule = categoryRules.get(position);
        holder.textView.setText(rule.name);
        holder.rule = rule;
    }

    @Override
    public int getItemCount() {
        return categoryRules.size();
    }
}
