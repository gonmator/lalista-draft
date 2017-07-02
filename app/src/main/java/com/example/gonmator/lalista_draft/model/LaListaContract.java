package com.example.gonmator.lalista_draft.model;

import android.provider.BaseColumns;

/**
 * Created by gonmator on 07.05.17.
 */

public final class LaListaContract {
    private LaListaContract() {}

    public static class Lista implements BaseColumns {
        public static final String TABLE_NAME = "lista";
        public static final String COLUMN_NAME_LID = "lid";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PARENT_ID = "parent_id";
        public static final String COLUMN_NAME_ATTRIBUTES = "attributes";
    }
}
