package com.example.gonmator.lalista_draft;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.Checkable;

/**
 * Created by gonmator on 04.05.17.
 */

public class RowView extends ConstraintLayout implements Checkable {
    public RowView(Context context) {
        super(context);
    }

    public RowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        if (mChecked) {
            super.setBackgroundColor(0x80E0E0E0);
        } else {
            super.setBackgroundColor(0x80202020);
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        mChecked = !mChecked;
    }

    boolean mChecked = false;
}
