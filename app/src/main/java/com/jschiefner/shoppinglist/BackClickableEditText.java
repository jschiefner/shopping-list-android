package com.jschiefner.shoppinglist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BackClickableEditText extends androidx.appcompat.widget.AppCompatEditText {
    public BackClickableEditText(@NonNull Context context) {
        super(context);
    }

    public BackClickableEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BackClickableEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            clearFocus(); // clear focus when a BackClickableEditText is active and "back" is pressed
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
}
