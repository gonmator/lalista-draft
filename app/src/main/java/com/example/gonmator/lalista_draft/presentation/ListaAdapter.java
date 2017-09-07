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

import com.example.gonmator.lalista_draft.model.LaListaContract;

import java.util.Collection;

/**
 * Created by gonmator on 03.05.17.
 */

public class ListaAdapter extends RecyclerView.Adapter implements RowViewHolder.Listener {

    interface Listener {
        public void onSubitemsButtonClick(long position);
        public void onSelectedItemsChanged(int selectedCount);
    }

    private Cursor mCursor;
    private int mIdColumn;
    private int mDescriptionColumn;
    private LayoutInflater mInflater;
    private int mLayoutId;
    private Listener mListener;
    private ArraySet<Long> mSelected;
    private boolean mEditMode;
    private boolean mSelectMode;


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
        long id = viewHolder.getListaId();
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
            notifyItemRangeChanged(0, getItemCount());
        }
    }

    public void setSelectMode(boolean selectMode) {
        boolean prevSelectMode = mSelectMode;
        mSelectMode = selectMode;
        if (prevSelectMode != selectMode) {
            mSelected.clear();
            mListener.onSelectedItemsChanged(0);
            notifyItemRangeChanged(0, getItemCount());
        }
    }

    // RecyclerView.Adapter

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowViewHolder viewHolder =
                new RowViewHolder(this, mInflater.inflate(mLayoutId, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RowViewHolder viewHolder = (RowViewHolder)holder;
        if (mCursor != null) {
            mCursor.moveToPosition(position);
            final long id = mCursor.getLong(mIdColumn);
            viewHolder.setListaId(id);
            viewHolder.setSelectedState(mSelectMode && mSelected.contains(id));
            viewHolder.setDescriptionText(mCursor.getString(mDescriptionColumn));
            if (mEditMode) {
                viewHolder.selectEditText();
            } else {
                viewHolder.selectTextView();
            }
            if (mEditMode || mSelectMode) {
                viewHolder.setSubitemsButtonVisibility(View.VISIBLE);
            } else {
                viewHolder.setSubitemsButtonVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    // RowViewHolder.Listener

    @Override
    public void onRowClick(RowViewHolder viewHolder) {
        if (mSelectMode) {
            toggleSelected(viewHolder);
        } else {
            mListener.onSubitemsButtonClick(viewHolder.getListaId());
        }
    }

    @Override
    public void onTextViewClick(RowViewHolder viewHolder) {
       onRowClick(viewHolder);
    }

    @Override
    public void onSubitemsButtonClick(RowViewHolder viewHolder) {
        mListener.onSubitemsButtonClick(viewHolder.getListaId());
    }
}
