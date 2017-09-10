package com.example.gonmator.lalista_draft.presentation;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gonmator.lalista_draft.R;
import com.example.gonmator.lalista_draft.model.LaListaContract;

import java.util.Collection;
import java.util.List;

/**
 * Created by gonmator on 03.05.17.
 */

public class ListaAdapter extends RecyclerView.Adapter<RowViewHolder>
        implements RowViewHolder.Listener {

    interface Listener {
        void onItemTextUpdated(long position, String text);
        void onSubitemsButtonClick(long position);
        void onSelectedItemsChanged(int selectedCount);
    }

    private final int EDIT_MODE_ON = 1;
    private final int EDIT_MODE_OFF = 2;
    private final int SELECT_MODE_ON = 3;
    private final int SELECT_MODE_OFF = 4;
    private final int ENTER_EDITING = 5;
    private final int EXIT_EDITING = 6;

    private Cursor mCursor;
    private int mIdColumn;
    private int mDescriptionColumn;
    private LayoutInflater mInflater;
    private int mLayoutId;
    private Listener mListener;
    private ArraySet<Long> mSelected;
    private boolean mEditMode;
    private boolean mSelectMode;
    private long mEditing;

    // ListaAdapter

    public ListaAdapter(
            @NonNull Context context, @NonNull Listener listener, @LayoutRes int layoutId,
            Cursor cursor) {
        super();
        mCursor = null;
        changeCursor(cursor);
        mListener = listener;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutId = layoutId;
        mSelected = new ArraySet<>();
        mEditMode = false;
        mSelectMode = false;
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

    public void toggleSelected(RowViewHolder viewHolder) {
        long id = viewHolder.getItemId();
        if (mSelected.contains(id)) {
            mSelected.remove(id);
            viewHolder.setSelectedState(false);
        } else {
            mSelected.add(id);
            viewHolder.setSelectedState(true);
        }
        mListener.onSelectedItemsChanged(mSelected.size());
    }

    public Collection<Long> getSelectedIds() {
        return mSelected;
    }

    public void setEditMode(boolean editMode) {
        boolean prevEditMode = mEditMode;
        mEditMode = editMode;
        if (prevEditMode != editMode) {
            Integer payload = mEditMode ? EDIT_MODE_ON : EDIT_MODE_OFF;
            notifyItemRangeChanged(0, getItemCount(), payload);
        }
    }

    public void setSelectMode(boolean selectMode) {
        boolean prevSelectMode = mSelectMode;
        mSelectMode = selectMode;
        if (prevSelectMode != selectMode) {
            mSelected.clear();
            mListener.onSelectedItemsChanged(0);
            Integer payload = mSelectMode ? SELECT_MODE_ON : SELECT_MODE_OFF;
            notifyItemRangeChanged(0, getItemCount(), payload);
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
        holder.setListaId(id);
        holder.setDescriptionText(mCursor.getString(mDescriptionColumn));
        if (mEditMode) {
            holder.selectEditText();
        } else {
            holder.selectTextView();
        }
        holder.setSelectedState(mSelectMode && mSelected.contains(id));
        if (mEditMode || mSelectMode) {
            if (mSelectMode || id != mEditing) {
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
                    holder.setSelectedState(mSelected.contains(holder.getItemId()));
                    holder.setActionButtonVisibility(View.VISIBLE);
                    break;
                case SELECT_MODE_OFF:
                    holder.setSelectedState(false);
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
                Integer payload = ENTER_EDITING;
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
        if (mSelectMode) {
            toggleSelected(viewHolder);
        } else {
            mListener.onSubitemsButtonClick(viewHolder.getItemId());
        }
    }

    @Override
    public void onTextViewClick(RowViewHolder viewHolder) {
        onRowClick(viewHolder);
    }

    @Override
    public void onTextChanged(RowViewHolder viewHolder) {
        mListener.onItemTextUpdated(viewHolder.getItemId(),
                viewHolder.getDescriptionText().toString());
    }
}
