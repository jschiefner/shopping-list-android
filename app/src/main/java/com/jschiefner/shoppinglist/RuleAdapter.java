package com.jschiefner.shoppinglist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.RuleHolder> {
    private Category category = MainActivity.instance.getCategory();

    @NonNull
    @Override
    public RuleAdapter.RuleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rule_recycler_view, parent, false);
        return new RuleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RuleHolder holder, int position) {
        holder.ruleTextView.setText(category.getRules().get(position));
    }

    @Override
    public int getItemCount() {
        return category.getRules().size();
    }

    static class RuleHolder extends RecyclerView.ViewHolder {
        TextView ruleTextView;

        RuleHolder(@NonNull View itemView) {
            super(itemView);
            ruleTextView = itemView.findViewById(R.id.rule_text_view);
        }
    }
}
