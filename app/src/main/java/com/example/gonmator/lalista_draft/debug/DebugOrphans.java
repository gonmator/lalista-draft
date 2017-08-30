package com.example.gonmator.lalista_draft.debug;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gonmator.lalista_draft.R;
import com.example.gonmator.lalista_draft.model.LaListaDbHelper;

/**
 * Created by gonmator on 24.08.17.
 */

public class DebugOrphans extends Fragment {
    private LaListaDbHelper mDbHelper = null;

    public static Fragment newInstance(Context context) {
        Fragment fragment = new DebugOrphans();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDbHelper = new LaListaDbHelper(getActivity());
        View view = inflater.inflate(R.layout.all_entries_debug, container, false);

        return view;
    }
}
