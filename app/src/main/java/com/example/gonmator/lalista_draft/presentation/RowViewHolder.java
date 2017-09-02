package com.example.gonmator.lalista_draft.presentation;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gonmator.lalista_draft.R;

/**
 * Created by gonmator on 30.08.17.
 */

public class RowViewHolder extends RecyclerView.ViewHolder {
    interface Listener {
        public void onRowClick(RowViewHolder viewHolder);
        public void onTextViewClick(RowViewHolder viewHolder);
        public void onSubitemsButtonClick(RowViewHolder viewHolder);
    }

    private final Listener mListener;
    private TextView mTextView;
    private ImageButton mSubitemsButton;
    private long mListaId;

    public RowViewHolder(@NonNull Listener listener, View itemView) {
        super(itemView);
        final RowViewHolder viewHolder = this;
        mListener = listener;
        RowView rowView = (RowView) itemView;
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onRowClick(viewHolder);
            }
        });
        mTextView = rowView.findViewById(R.id.textView);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onTextViewClick(viewHolder);
            }
        });
        mSubitemsButton = rowView.findViewById(R.id.subitemsButton);
        mSubitemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSubitemsButtonClick(viewHolder);
            }
        });
    }

    public long getListaId() {
        return mListaId;
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

    void setListaId(long id) {
        mListaId = id;
    }

    void setSubitemsButtonVisibility(int visibility) {
        mSubitemsButton.setVisibility(visibility);
    }
}
