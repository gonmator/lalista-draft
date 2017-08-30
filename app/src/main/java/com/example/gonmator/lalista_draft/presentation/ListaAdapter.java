package com.example.gonmator.lalista_draft.presentation;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gonmator.lalista_draft.R;
import com.example.gonmator.lalista_draft.model.LaListaContract;

import java.util.AbstractCollection;
import java.util.Collection;

/**
 * Created by gonmator on 03.05.17.
 */

public class ListaAdapter extends CursorAdapter {

    interface ListaAdapterListener {
        public void onSubitemsButtonClick(long position, AdapterView<?> parent);
    }

    private LayoutInflater mCursorInflater;
    private int mLayoutId;
    private int mDescriptionViewId;
    private int mSubitemsButtonId;
    private ListaAdapterListener mListener;
    private ArraySet<Long> mSelected;
    private boolean mEditMode;

    public ListaAdapter(
            @NonNull Context context, @LayoutRes int layoutId, int descriptionViewId,
            int subitemsButtonId, Cursor cursor, ListaAdapterListener listener) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
        mCursorInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutId = layoutId;
        mDescriptionViewId = descriptionViewId;
        mSubitemsButtonId = subitemsButtonId;
        mListener = listener;
        mSelected = new ArraySet<>();
        mEditMode = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int position_ = position;
        final AdapterView<?> parent_ = (AdapterView<?>) parent;
        View view = super.getView(position, convertView, parent);
        return view;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mCursorInflater.inflate(mLayoutId, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final long id = cursor.getLong(cursor.getColumnIndex(LaListaContract.Lista._ID));
        if (mEditMode && mSelected.contains(id)) {
            view.setBackgroundResource(R.color.colorAccent);
        } else {
            view.setBackgroundResource(R.color.colorBackground);
        }
        TextView descriptionView = (TextView)view.findViewById(mDescriptionViewId);
        String description = cursor.getString(cursor.getColumnIndex(
                LaListaContract.Lista.COLUMN_NAME_DESCRIPTION));
        descriptionView.setText(description);
        View subitemsButton = view.findViewById(mSubitemsButtonId);
        if (mEditMode) {
            subitemsButton.setVisibility(View.VISIBLE);
            subitemsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onSubitemsButtonClick(id, null);
                }
            });
        } else {
            subitemsButton.setVisibility(View.GONE);
        }
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
