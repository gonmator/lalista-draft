package com.example.gonmator.lalista_draft;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gonmator.lalista_draft.model.LaListaContract;

import java.util.List;

/**
 * Created by gonmator on 03.05.17.
 */

public class ListaAdapter extends CursorAdapter {
    private LayoutInflater mCursorInflater;
    private int mLayoutId;
    private int mDescriptionViewId;

    public ListaAdapter(
            @NonNull Context context, @LayoutRes int layoutId, int descriptionViewId,
            Cursor cursor) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
        mCursorInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutId = layoutId;
        mDescriptionViewId = descriptionViewId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = (ViewGroup)super.getView(position, convertView, parent);
        ListView listView = (ListView)parent;

/*
        if (listView.isItemChecked(position)) {
            view.setBackgroundColor(0xE0E0E0);
        } else {
            view.setBackgroundColor(0x202020);
        }
*/
        return view;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mCursorInflater.inflate(mLayoutId, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView descriptionView = (TextView)view.findViewById(mDescriptionViewId);
        String description = cursor.getString(cursor.getColumnIndex(
                LaListaContract.Lista.COLUMN_NAME_DESCRIPTION));
        descriptionView.setText(description);
    }
}
