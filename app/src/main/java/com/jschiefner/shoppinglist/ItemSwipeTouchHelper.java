package com.jschiefner.shoppinglist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemSwipeTouchHelper extends ItemTouchHelper.SimpleCallback {
    private static final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    private final Drawable trashIcon;
    private final Drawable pencilIcon;
    private final RecyclerView.Adapter adapter;

    public ItemSwipeTouchHelper(Context context, RecyclerView.Adapter adapter, int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
        this.adapter = adapter;
        trashIcon = ContextCompat.getDrawable(context, R.drawable.bin);
        pencilIcon = ContextCompat.getDrawable(context, R.drawable.pencil);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        ItemAdapter.ItemHolder holder = (ItemAdapter.ItemHolder) viewHolder;
        Item item = holder.item;
        switch (direction) {
            case ItemTouchHelper.LEFT:
                item.getReference().delete();
                break;
            case ItemTouchHelper.RIGHT:
                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                new EditItemDialog(item).show();
                break;
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        Drawable icon;

        if (dX > 0) { // Swiping to the right
            if (dX > 100) dX = 100;
            icon = pencilIcon;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();
            int iconRight = itemView.getLeft() + ((int) dX);
            int iconLeft = iconRight - icon.getIntrinsicWidth();

            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        } else if (dX < 0) { // Swiping to the left
            if (dX < -100) dX = -100;
            icon = trashIcon;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();
            int iconLeft = itemView.getRight() + (int) dX;
            int iconRight = itemView.getRight() + ((int) dX) + icon.getIntrinsicWidth();

            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        } else return;

        icon.setAlpha(getAlpha(dX));
        icon.draw(c);
    }

    private int getAlpha(float dx) {
        dx = Math.abs(dx);
        int max = 100;
        float percent = dx / max;
        return (int) (255 * percent);
    }
}
