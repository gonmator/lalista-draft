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
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.gonmator.lalista_draft.R;
import com.example.gonmator.lalista_draft.model.LaListaContract;

import java.util.Collection;

/**
 * Created by gonmator on 03.05.17.
 */

public class ListaAdapter extends RecyclerView.Adapter {

    interface ListaAdapterListener {
        public void onSubitemsButtonClick(long position, AdapterView<?> parent);
    }

    private Cursor mCursor;
    private int mIdColumn;
    private int mDescriptionColumn;
    private LayoutInflater mInflater;
    private int mLayoutId;
    private int mDescriptionViewId;
    private int mSubitemsButtonId;
    private ListaAdapterListener mListener;
    private ArraySet<Long> mSelected;
    private boolean mEditMode;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowViewHolder viewHolder = new RowViewHolder(mInflater.inflate(mLayoutId, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RowViewHolder viewHolder = (RowViewHolder)holder;
        if (mCursor != null) {
            mCursor.moveToPosition(position);
            final long id = mCursor.getLong(mIdColumn);
            if (mEditMode && mSelected.contains(id)) {
                viewHolder.setBackgroundResource(R.color.colorAccent);
            } else {
                viewHolder.setBackgroundResource(R.color.colorBackground);
            }
            viewHolder.setDescriptionText(mCursor.getString(mDescriptionColumn));
            if (mEditMode) {
                viewHolder.setSubitemsButtonVisibility(View.VISIBLE);
    /*
                subitemsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onSubitemsButtonClick(id, null);
                    }
                });
    */
            } else {
                viewHolder.setSubitemsButtonVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public ListaAdapter(
            @NonNull Context context, @LayoutRes int layoutId, Cursor cursor) {
        super();
        mCursor = null;
        changeCursor(cursor);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutId = layoutId;
        mListener = null;
        mSelected = new ArraySet<>();
        mEditMode = false;
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

    public void changeSelectListaId(long id) {
        if (mSelected.contains(id)) {
            mSelected.remove(id);
        } else {
            mSelected.add(id);
        }
    }

    public Collection<Long> getSelectedIds() {
        return mSelected;
    }

    public void selectListaId(long listaId, boolean state) {
        if (state) {
            mSelected.add(listaId);
        } else {
            mSelected.remove(listaId);
        }
    }

    public void setEditMode(boolean editMode) {
        mEditMode = editMode;
        if (!editMode) {
            mSelected.clear();
        }
    }
}
