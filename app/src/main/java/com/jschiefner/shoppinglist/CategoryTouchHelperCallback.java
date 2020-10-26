package com.jschiefner.shoppinglist;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CategoryTouchHelperCallback extends ItemTouchHelper.SimpleCallback {
    private final CollectionReference categoriesRef = FirebaseFirestore.getInstance().collection("categories");
    private final IgnoreChangesFirestoreRecyclerAdapter<Category, ?> adapter;

    private int dragFrom = -1;
    private int dragTo = -1;

    public CategoryTouchHelperCallback(IgnoreChangesFirestoreRecyclerAdapter<Category, ?> recyclerAdapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
        adapter = recyclerAdapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
        if (dragFrom == -1) {
            dragFrom = source.getAdapterPosition();
        }
        dragTo = target.getAdapterPosition();
        adapter.notifyItemMoved(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (dragFrom == -1) return;

        final Category draggedItem = adapter.getItem(dragFrom);
        final Category draggedToItem = adapter.getItem(dragTo);
        final String draggedId = adapter.getSnapshots().getSnapshot(dragFrom).getId();
        final int itemCount = adapter.getItemCount();

        adapter.setIgnoreChanges(true);

        if (dragTo == itemCount - 1) {
            draggedItem.setPosition(draggedToItem.getPosition() + 100);
        } else if (dragTo == 0) {
            draggedItem.setPosition(draggedToItem.getPosition() / 2);
        } else {
            Category draggedToNext = adapter.getItem(dragTo > dragFrom ? dragTo + 1 : dragTo - 1);
            draggedItem.setPosition((draggedToItem.getPosition() + draggedToNext.getPosition()) / 2);
        }

        categoriesRef.document(draggedId).update("position", draggedItem.getPosition()).addOnCompleteListener(MainActivity.instance, task -> adapter.setIgnoreChanges(false));
        dragFrom = dragTo = -1;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        CategoryAdapter.CategoryHolder categoryHolder = (CategoryAdapter.CategoryHolder) viewHolder;
        categoriesRef.document(categoryHolder.category.getId()).delete();
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
}
