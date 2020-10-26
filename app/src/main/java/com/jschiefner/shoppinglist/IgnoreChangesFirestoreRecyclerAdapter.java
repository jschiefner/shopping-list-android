package com.jschiefner.shoppinglist;

import androidx.annotation.NonNull;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import static androidx.recyclerview.widget.RecyclerView.*;

public abstract class IgnoreChangesFirestoreRecyclerAdapter<T, VH extends ViewHolder> extends FirestoreRecyclerAdapter<T, VH> {
    private boolean mIgnoreChanges = false;

    public IgnoreChangesFirestoreRecyclerAdapter(@NonNull FirestoreRecyclerOptions<T> options) {
        super(options);
    }

    public void setIgnoreChanges(boolean ignoreChanges) {
        mIgnoreChanges = ignoreChanges;
    }

    @Override
    public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
        if (!mIgnoreChanges) {
            super.onChildChanged(type, snapshot, newIndex, oldIndex);
        }
    }
}
