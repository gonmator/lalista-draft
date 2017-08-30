package com.example.gonmator.lalista_draft.debug;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.example.gonmator.lalista_draft.R;
import com.example.gonmator.lalista_draft.model.LaListaDbHelper;

public class DebugAllEntries extends Fragment {

    private LaListaDbHelper mDbHelper = null;

    public static Fragment newInstance(Context context, int sectionNumber) {
        Fragment fragment = new DebugAllEntries();
        Bundle args = new Bundle();
        args.putInt("section_number", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public DebugAllEntries() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDbHelper = new LaListaDbHelper(getActivity());
        View view = inflater.inflate(R.layout.all_entries_debug, container, false);
        int sectionNumber = getArguments().getInt("section_number");

        Cursor cursor = null;
        if (sectionNumber == 0) {
            cursor = mDbHelper.getAllListas();
        } else if (sectionNumber == 1) {
            cursor = mDbHelper.getOrphanListas();
        }

        final ListView listView = (ListView)view.findViewById(R.id.debug_list);
        CursorAdapter listAdapter =
                new DebugListViewAdapter(getActivity(), cursor);
        listView.setAdapter(listAdapter);
        return view;
    }
}
