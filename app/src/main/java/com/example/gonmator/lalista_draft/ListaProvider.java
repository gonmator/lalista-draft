/*
package com.example.gonmator.lalista_draft;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.gonmator.lalista_draft.helper.LaListaDbHelper;

import java.util.List;

*/
/**
 * Created by gonmator on 09.05.17.
 *//*


public class ListaProvider extends ContentProvider {
    private LaListaDbHelper mDbHelper;

    public ListaProvider() {
        super();
        mDbHelper = null;
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new LaListaDbHelper(null);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(
            @NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
            @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String path = uri.getPath();
        if (path != "lista") {
            throw new IllegalArgumentException("Invalid path");
        }
        return mDbHelper.getListas(projection, selection, selectionArgs, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String path = uri.getPath();
        if (path != "lista") {
            throw new IllegalArgumentException("Invalid path");
        }
        return "vnd.android.cursor.dir/vnd.com.example.gonmator.provider.lista";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(
            @NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(
            @NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
            @Nullable String[] selectionArgs) {
        return 0;
    }

}
*/
