package com.example.gonmator.lalista_draft.presentation;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gonmator.lalista_draft.R;

/**
 * Created by gonmator on 30.08.17.
 */

public class RowViewHolder extends RecyclerView.ViewHolder {
    interface Listener {
        void onRowClick(RowViewHolder viewHolder);
        void onTextViewClick(RowViewHolder viewHolder);
        void onSubitemsButtonClick(RowViewHolder viewHolder);
        void onTextChanged(RowViewHolder viewHolder);
    }

    private final Listener mListener;
    private ImageButton mSelectButton;
    private TextView mTextView;
    private EditText mEditText;
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
        mSelectButton = rowView.findViewById(R.id.selectButton);
/*
        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onRowClick(viewHolder);
            }
        });
*/

        mTextView = rowView.findViewById(R.id.textView);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onTextViewClick(viewHolder);
            }
        });

        mEditText = rowView.findViewById(R.id.editItem);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT ||
                        actionId == EditorInfo.IME_ACTION_DONE) {
                    mTextView.setText(v.getText());
                    mListener.onTextChanged(viewHolder);
                }
                return false;
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

    public CharSequence getDescriptionText() {
        return mEditText.getText();
    }

    public long getListaId() {
        return mListaId;
    }

    void selectEditText() {
//        mSelectButton.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.GONE);
        mEditText.setVisibility(View.VISIBLE);
    }

    void selectTextView() {
//        mSelectButton.setVisibility(View.GONE);
        mTextView.setVisibility(View.VISIBLE);
        mEditText.setVisibility(View.GONE);
    }

    void setSelectedState(boolean selected) {
//        mSelectButton.setSelected(selected);
        if (selected) {
            itemView.setBackgroundResource(R.color.colorAccent);
        } else {
            itemView.setBackgroundResource(R.color.colorBackgroundDark);
        }
    }

    void setBackgroundResource(int resourceId) {
        itemView.setBackgroundResource(resourceId);
    }

    void setDescriptionText(CharSequence text) {
        mEditText.setText(text);
        mTextView.setText(text);
    }

    void setListaId(long id) {
        mListaId = id;
    }

    void setSubitemsButtonVisibility(int visibility) {
        mSubitemsButton.setVisibility(visibility);
    }
}
