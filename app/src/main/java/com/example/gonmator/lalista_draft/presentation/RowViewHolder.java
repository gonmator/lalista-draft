package com.example.gonmator.lalista_draft.presentation;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gonmator.lalista_draft.R;

/**
 * Created by gonmator on 30.08.17.
 */

public class RowViewHolder extends RecyclerView.ViewHolder {
    private TextView mTextView;
    private ImageButton mSubitemsButton;

    public RowViewHolder(View itemView) {
        super(itemView);
        RowView rowView = (RowView) itemView;
        mTextView = rowView.findViewById(R.id.textView);
        mSubitemsButton = rowView.findViewById(R.id.subitemsButton);
    }

    public ImageButton getSubitemsButton() {
        return mSubitemsButton;
    }

    public TextView getTextView() {
        return mTextView;
    }

    void setBackgroundResource(int resourceId) {
        itemView.setBackgroundResource(resourceId);
    }

    void setDescriptionText(CharSequence text) {
        mTextView.setText(text);
    }

    void setSubitemsButtonVisibility(int visibility) {
        mSubitemsButton.setVisibility(visibility);
    }
}
