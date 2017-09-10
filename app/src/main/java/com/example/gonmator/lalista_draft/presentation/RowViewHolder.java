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

public class RowViewHolder extends RecyclerView.ViewHolder implements
        TextView.OnEditorActionListener, View.OnClickListener, View.OnFocusChangeListener {

    interface Listener {
        void onActionButtonClick(RowViewHolder viewHolder);
        void onEditFocusChange(RowViewHolder viewHolder, boolean hasFocus);
        void onTextChanged(RowViewHolder viewHolder);
        void onTextViewClick(RowViewHolder viewHolder);
        void onRowClick(RowViewHolder viewHolder);
    }

    private final Listener mListener;
    private ImageButton mSelectButton;
    private TextView mTextView;
    private EditText mEditText;
    private ImageButton mActionButton;
    private long mListaId;
    private boolean mAttached;

    public RowViewHolder(@NonNull Listener listener, View itemView) {
        super(itemView);
        mListener = listener;

        RowView rowView = (RowView) itemView;
        rowView.setOnClickListener(this);

        mSelectButton = rowView.findViewById(R.id.selectButton);

        mTextView = rowView.findViewById(R.id.textView);
        mTextView.setOnClickListener(this);

        mEditText = rowView.findViewById(R.id.editItem);
        mEditText.setHorizontallyScrolling(false);
        mEditText.setMaxLines(Integer.MAX_VALUE);
        mEditText.setOnEditorActionListener(this);
        mEditText.setOnFocusChangeListener(this);

        mActionButton = rowView.findViewById(R.id.actionButton);
        mActionButton.setOnClickListener(this);

        mAttached = false;
    }

    public CharSequence getDescriptionText() {
        return mEditText.getText();
    }

    public boolean isAttached() {
        return mAttached;
    }

    void selectEditText() {
        mTextView.setVisibility(View.GONE);
        mEditText.setVisibility(View.VISIBLE);
    }

    void selectTextView() {
        mTextView.setVisibility(View.VISIBLE);
        mEditText.setVisibility(View.GONE);
    }

    void setActionButtonImage(int resId) {
        mActionButton.setImageResource(resId);
    }

    void setAttached(boolean attached) {
        mAttached = attached;
    }

    void setSelectedState(boolean selected) {
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

    void setActionButtonVisibility(int visibility) {
        mActionButton.setVisibility(visibility);
    }


    // OnClickListener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.actionButton:
                mListener.onActionButtonClick(this);
                break;
            case R.id.rowView:
                mListener.onRowClick(this);
                break;
            case R.id.textView:
                mListener.onTextViewClick(this);
                break;
        }
    }

    // onEditActionListener
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            mTextView.setText(v.getText());
            mListener.onTextChanged(this);
            return true;
        }
        return false;
    }

    // OnFocusChangeListener
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        mListener.onEditFocusChange(this, hasFocus);
    }
}
