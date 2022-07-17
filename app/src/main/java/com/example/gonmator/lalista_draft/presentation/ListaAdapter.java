package com.example.gonmator.lalista_draft.presentation;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gonmator.lalista_draft.R;
import com.example.gonmator.lalista_draft.model.LaListaContract;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by gonmator on 03.05.17.
 */

public class ListaAdapter extends RecyclerView.Adapter<RowViewHolder>
        implements RowViewHolder.Listener {

    interface Listener {
        void onItemTextUpdated(long position, String text);
        void onSubitemsButtonClick(long id);
        void onEnterSelectMode();
        void onExitSelectMode();
        void onSelectedItemsChanged(int selectedCount);
    }

    private final String EDIT_MODE_KEY = "LISTA_ADAPTER.EDIT_MODE";
    private final String EDITING_KEY = "LISTA_ADAPTER.EDITING";
    private final String SELECT_MODE_KEY = "LISTA_ADAPTER.SELECT_MODE";
    private final String SELECTED_KEY = "LISTA_ADAPTER.SELECTED_KEY";

    public enum SelectMode {
        disabled(0),
        selecting(1),
        cutting(2),
        copying(3);

        private int mSm;
        SelectMode(int sm) {
            mSm = sm;
        }

        int toInt() {
            return mSm;
        }
    };

    private final int EDIT_MODE_ON = 1;
    private final int EDIT_MODE_OFF = 2;
    private final int SELECT_MODE_ON = 3;
    private final int SELECT_MODE_OFF = 4;
    private final int ENTER_EDITING = 5;
    private final int EXIT_EDITING = 6;
    private final int SELECT = 7;
    private final int UNSELECT=8;

    private Context mContext;
    private Cursor mCursor;
    private int mIdColumn;
    private int mDescriptionColumn;
    private LayoutInflater mInflater;
    private int mLayoutId;
    private Listener mListener;
    private ArraySet<Long> mSelected;
    private boolean mEditMode;
    private SelectMode mSelectMode;
    private long mEditing;

    // ListaAdapter

    public ListaAdapter(
            @NonNull Context context, @NonNull Listener listener, @LayoutRes int layoutId,
            Cursor cursor) {
        super();
        mContext = context;
        mCursor = null;
        changeCursor(cursor);
        mListener = listener;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutId = layoutId;
        mSelected = new ArraySet<>();
        mEditMode = false;
        mSelectMode = SelectMode.disabled;
        mEditing = -1;
        setHasStableIds(true);
    }

    public void changeCursor(Cursor cursor) {
        synchronized (this) {
            if (mCursor != null) {
                mCursor.close();
            }
            mCursor = cursor;
            if (mCursor != null) {
                mIdColumn = cursor.getColumnIndex(LaListaContract.Lista._ID);
                mDescriptionColumn = cursor.getColumnIndex(
                        LaListaContract.Lista.COLUMN_NAME_DESCRIPTION);
            }
        }
        notifyDataSetChanged();
    }

    public void clearSelected() {
        mSelected.clear();
    }

    public SelectMode getSelectMode() {
        return mSelectMode;
    }

    public void setSelected(RowViewHolder viewHolder) {
        long id = viewHolder.getItemId();
        mSelected.add(id);
        notifyItemChanged(viewHolder.getAdapterPosition(), SELECT);
        mListener.onSelectedItemsChanged(mSelected.size());
    }

    public void toggleSelected(RowViewHolder viewHolder) {
        if (mSelectMode != SelectMode.selecting) {
            setSelectMode(SelectMode.selecting);
        }
        long id = viewHolder.getItemId();
        if (mSelected.contains(id)) {
            mSelected.remove(id);
            notifyItemChanged(viewHolder.getAdapterPosition(), UNSELECT);
        } else {
            mSelected.add(id);
            notifyItemChanged(viewHolder.getAdapterPosition(), SELECT);
        }
        mListener.onSelectedItemsChanged(mSelected.size());
    }

    public Collection<Long> getSelectedIds() {
        return mSelected;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EDIT_MODE_KEY, mEditMode);
        outState.putLong(EDITING_KEY, mEditing);
        outState.putInt(SELECT_MODE_KEY, mSelectMode.toInt());
        // outState.putSerializable(SELECTED_KEY, (Serializable)mSelected);
    }

    public int selectAll() {
        if (mSelectMode != SelectMode.selecting) {
            setSelectMode(SelectMode.selecting);
        }
        for (int position = 0; position < getItemCount(); position++) {
            long id = getItemId(position);
            mSelected.add(id);
        }
        notifyItemRangeChanged(0, getItemCount(), SELECT);
        mListener.onSelectedItemsChanged(mSelected.size());
        return getItemCount();
    }

    public void setEditMode(boolean editMode) {
        boolean prevEditMode = mEditMode;
        mEditMode = editMode;
        if (prevEditMode != editMode) {
            notifyItemRangeChanged(0, getItemCount(), mEditMode ? EDIT_MODE_ON : EDIT_MODE_OFF);
        }
    }

    public void setSelectMode(SelectMode selectMode) {
        SelectMode prevSelectMode = mSelectMode;
        mSelectMode = selectMode;
        if (prevSelectMode != selectMode) {
            if (mSelectMode == SelectMode.disabled) {
                mSelected.clear();
                mListener.onSelectedItemsChanged(0);
            }
            notifyItemRangeChanged(0, getItemCount(),
                    mSelectMode == SelectMode.disabled ? SELECT_MODE_OFF : SELECT_MODE_ON);
            if (mSelectMode == SelectMode.selecting) {
                mListener.onEnterSelectMode();
            } else if (mSelectMode == SelectMode.disabled) {
                mListener.onExitSelectMode();
            }
        }
    }


    // RecyclerView.Adapter

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        final long id = getItemId(position);
        holder.setDescriptionText(mCursor.getString(mDescriptionColumn));
        if (mEditMode) {
            holder.selectEditText();
        } else {
            holder.selectTextView();
        }
        setSelectedState(holder, mSelectMode != SelectMode.disabled && mSelected.contains(id));
        if (mEditMode || mSelectMode != SelectMode.disabled) {
            if (mSelectMode != SelectMode.disabled || id != mEditing) {
                holder.setActionButtonImage(R.drawable.ic_arrow_forward_white_24dp);
            } else {
                holder.setActionButtonImage(R.drawable.ic_done_white_24dp);
            }
            holder.setActionButtonVisibility(View.VISIBLE);
        } else {
            holder.setActionButtonVisibility(View.GONE);
        }
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowViewHolder viewHolder =
                new RowViewHolder(this, mInflater.inflate(mLayoutId, parent, false));
        return viewHolder;
    }

    @Override
    public long getItemId(int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(mIdColumn);
        }
        return RecyclerView.NO_ID;
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
            return;
        }
        for (Object payload: payloads) {
            Integer update = (Integer)payload;
            switch (update) {
                case EDIT_MODE_ON:
                    holder.selectEditText();
                    holder.setActionButtonVisibility(View.VISIBLE);
                    break;
                case EDIT_MODE_OFF:
                    holder.selectTextView();
                    holder.setActionButtonImage(R.drawable.ic_arrow_forward_white_24dp);
                    holder.setActionButtonVisibility(View.GONE);
                    break;
                case SELECT_MODE_ON:
                    setSelectedState(holder, mSelected.contains(holder.getItemId()));
                    holder.setActionButtonVisibility(View.VISIBLE);
                    break;
                case SELECT_MODE_OFF:
                    setSelectedState(holder, false);
                    holder.setActionButtonVisibility(View.GONE);
                    break;
                case ENTER_EDITING:
                    if (mEditMode) {
                        holder.setActionButtonImage(R.drawable.ic_done_white_24dp);
                    }
                    break;
                case EXIT_EDITING:
                    if (mEditMode) {
                        holder.setActionButtonImage(R.drawable.ic_arrow_forward_white_24dp);
                    }
                    break;
                case SELECT:
                    if (mSelectMode != SelectMode.disabled) {
                        setSelectedState(holder, true);
                    }
                    break;
                case UNSELECT:
                    setSelectedState(holder, false);
                    break;
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(RowViewHolder holder) {
        holder.setAttached(true);
    }

    @Override
    public void onViewDetachedFromWindow(RowViewHolder holder) {
        holder.setAttached(false);
    }


    // RowViewHolder.Listener

    @Override
    public void onActionButtonClick(RowViewHolder viewHolder) {
        if (mEditMode && viewHolder.getItemId() == mEditing) {
            onTextChanged(viewHolder);
        } else {
            mListener.onSubitemsButtonClick(viewHolder.getItemId());
        }
    }

    @Override
    public void onEditFocusChange(RowViewHolder viewHolder, boolean hasFocus) {
        if (mEditMode) {
            if (hasFocus) {
                mEditing = viewHolder.getItemId();
                notifyItemChanged(viewHolder.getAdapterPosition(), ENTER_EDITING);
            } else {
                if (mEditing == viewHolder.getItemId()) {
                    mEditing = -1;
                }
                if (viewHolder.isAttached()) {
                    notifyItemChanged(viewHolder.getAdapterPosition(), EXIT_EDITING);
                }
            }
        }
    }

    @Override
    public void onRowClick(RowViewHolder viewHolder) {
        if (mSelectMode != SelectMode.disabled) {
            toggleSelected(viewHolder);
        } else {
            mListener.onSubitemsButtonClick(viewHolder.getItemId());
        }
    }

    @Override
    public void onRowLongClick(RowViewHolder viewHolder) {
        setSelectMode(SelectMode.selecting);
        setSelected(viewHolder);
    }

    @Override
    public void onTextViewClick(RowViewHolder viewHolder) {
        onRowClick(viewHolder);
    }

    @Override
    public void onTextViewLongClick(RowViewHolder viewHolder) {
        onRowLongClick(viewHolder);
    }

    @Override
    public void onTextChanged(RowViewHolder viewHolder) {
        mListener.onItemTextUpdated(viewHolder.getItemId(),
                viewHolder.getDescriptionText().toString());
    }


    // private functions

    int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
    }

    void setSelectedState(RowViewHolder holder, boolean selected) {
        if (selected) {
            if (mSelectMode == SelectMode.cutting) {
                holder.setBackgroundResource(R.color.colorAccentTrans);
            } else {
                holder.setBackgroundResource(R.color.colorAccent);
            }
            if (mSelectMode == SelectMode.copying || mSelectMode == SelectMode.cutting) {
                holder.setMargins(dpToPx(2), dpToPx(3), dpToPx(2), dpToPx(3));
            } else {
                holder.setMargins(dpToPx(4), dpToPx(1), dpToPx(4), dpToPx(1));
            }
        } else {
            holder.setBackgroundResource(R.color.colorBackgroundDark);
            holder.setMargins(dpToPx(4), dpToPx(1), dpToPx(4), dpToPx(1));
        }
    }
}
