package com.example.gonmator.lalista_draft.debug;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.gonmator.lalista_draft.R;
import com.example.gonmator.lalista_draft.model.LaListaContract;

/**
 * Created by gonmator on 20.08.17.
 */

public class DebugListViewAdapter extends CursorAdapter {
    private LayoutInflater mCursorInflater;

    public DebugListViewAdapter(@NonNull Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        mCursorInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mCursorInflater.inflate(R.layout.debug_row_view, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView cId = (TextView)view.findViewById(R.id.col_id);
        TextView cLid = (TextView)view.findViewById(R.id.col_lid);
        TextView cDescription = (TextView)view.findViewById(R.id.col_description);
        TextView cParent = (TextView)view.findViewById(R.id.col_parentId);
        TextView cAttributes = (TextView)view.findViewById(R.id.col_attributes);
        cId.setText(cursor.getString(cursor.getColumnIndex(LaListaContract.Lista._ID)));
        // cLid.setText(cursor.getString(cursor.getColumnIndex(LaListaContract.Lista.COLUMN_NAME_LID)));
        cDescription.setText(cursor.getString(cursor.getColumnIndex(LaListaContract.Lista.COLUMN_NAME_DESCRIPTION)));
        cParent.setText(cursor.getString(cursor.getColumnIndex(LaListaContract.Lista.COLUMN_NAME_PARENT_ID)));
        cAttributes.setText(cursor.getString(cursor.getColumnIndex(LaListaContract.Lista.COLUMN_NAME_ATTRIBUTES)));
    }
}
