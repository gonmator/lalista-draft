package com.example.gonmator.lalista_draft.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.example.gonmator.lalista_draft.model.LaListaContract;
import com.example.gonmator.lalista_draft.model.Lista;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gonmator on 07.05.17.
 */

public class LaListaDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "laLista.db";

    private static final String TABLE_LISTA = LaListaContract.Lista.TABLE_NAME;

    public static final String KEY_ID = BaseColumns._ID;
    private static final String KEY_LID = LaListaContract.Lista.COLUMN_NAME_LID;
    private static final String KEY_DESCRIPTION = LaListaContract.Lista.COLUMN_NAME_DESCRIPTION;
    private static final String KEY_PARENT_ID = LaListaContract.Lista.COLUMN_NAME_PARENT_ID;
    private static final String KEY_ATTRIBUTES = LaListaContract.Lista.COLUMN_NAME_ATTRIBUTES;

    private static final String CREATE_TABLE_LISTA = "CREATE TABLE " +
            TABLE_LISTA + " (" + KEY_ID + " INTEGER PRIMARY KEY, " +
            KEY_LID + " TEXT NOT NULL UNIQUE, " +
            KEY_DESCRIPTION + " TEXT, " +
            KEY_PARENT_ID + " INTEGER, " +
            KEY_ATTRIBUTES + " TEXT)";

    private static final String[] LISTA_COLUMNS = {KEY_LID, KEY_DESCRIPTION, KEY_ATTRIBUTES};
    private static final String[] LISTA_ID_COLUMNS = {
            KEY_ID, KEY_LID, KEY_DESCRIPTION, KEY_ATTRIBUTES };
    private static final String[] ID_COLUMNS = { KEY_ID };
    private static final String ID_SELECTION = KEY_ID + " = ?";
    private static final String KEY_LID_SELECTION = KEY_LID + " = ?";
    private static final String PARENT_ID_SELECTION = KEY_PARENT_ID + " = ?";
    private static final String[] PARENT_ROOT_ID = { String.valueOf(-1) };

    private long mRootId;

    // Basic implementation

    public LaListaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mRootId = -1;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LISTA);
        createLista(db, new Lista(), -1);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new SQLException("Upgrade of database is not supported");
    }

    // Root access

    public long getRootId() {
        if (mRootId == -1) {
            SQLiteDatabase db = getReadableDatabase();
            Cursor c = db.query(TABLE_LISTA, ID_COLUMNS, PARENT_ID_SELECTION, PARENT_ROOT_ID,
                    null, null, null, "1");
            if (c != null) {
                c.moveToFirst();
                mRootId = c.getLong(0);
                c.close();
            }
        }
        return mRootId;
    }

    // Specific helper functions

    public long createLista(Lista lista, long parentId) {
        SQLiteDatabase db = getWritableDatabase();
        return createLista(db, lista, parentId);
    }
    public Lista getLista(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_LISTA, LISTA_COLUMNS, ID_SELECTION,
                new String[] { String.valueOf(id)}, null, null, null, "1");
        Lista lista = null;
        if (c != null) {
            c.moveToFirst();
            lista = new Lista(
                    c.getString(c.getColumnIndex(KEY_LID)),
                    c.getString(c.getColumnIndex(KEY_DESCRIPTION)),
                    c.getString(c.getColumnIndex(KEY_ATTRIBUTES)));
            c.close();
        }
        return lista;
    }
    public Lista getLista(String lid) {
        Lista lista = null;
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = { KEY_DESCRIPTION, KEY_ATTRIBUTES };
        String[] selectionArgs = { lid };
        Cursor c = db.query(
                TABLE_LISTA, columns, KEY_LID + " = ?", selectionArgs, null, null, null);
        if (c != null) {
            c.moveToFirst();
            lista = new Lista(
                    lid, c.getString(c.getColumnIndex(KEY_DESCRIPTION)),
                    c.getString(c.getColumnIndex(KEY_ATTRIBUTES)));
            c.close();
        }
        // todo: throw exception ?
        return lista;
    }
    public Cursor getListasOf(long parentId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_LISTA, LISTA_ID_COLUMNS, PARENT_ID_SELECTION,
                new String[] { String.valueOf(parentId)}, null, null, null);
    }
    public Cursor getListasOf(String parentLid) {
        SQLiteDatabase db = getReadableDatabase();
        long parentId = getIndex(db, parentLid);
        if (parentId == -1) return null;
        return getListasOf(parentId);
    }

    public void updateLista(Lista lista) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, lista.getDescription());
        values.put(KEY_ATTRIBUTES, lista.getAttributesStr());

        db.update(TABLE_LISTA, values, KEY_LID_SELECTION, new String[] { lista.getLidStr() });
    }

    public void deleteLista(long id) {
        SQLiteDatabase db = getWritableDatabase();
        // recursive, get the list of descendent
        List<String> descendentIds = new ArrayList<>();
        feedUpDescendentIds(db, descendentIds, id);
        if (!descendentIds.isEmpty()) {
            db.delete(
                    TABLE_LISTA, KEY_ID + " IN (?)",
                    new String[] {TextUtils.join(", ", descendentIds)});
        }
        db.delete(TABLE_LISTA, KEY_ID + " = ?", new String[] { String.valueOf(id) });
    }
    public void deleteLista(String lid) {
        SQLiteDatabase db = getWritableDatabase();
        long id = getIndex(db, lid);
        deleteLista(id);
    }

    // private methods

    private long createLista(SQLiteDatabase db, Lista lista, long parentId) {
        ContentValues values = new ContentValues();
        values.put(KEY_LID, lista.getLidStr());
        values.put(KEY_DESCRIPTION, lista.getDescription());
        values.put(KEY_PARENT_ID, parentId);
        values.put(KEY_ATTRIBUTES, lista.getAttributesStr());
        return db.insert(TABLE_LISTA, null, values);
    }

    private long getIndex(SQLiteDatabase db, String lid) {
        Cursor c = db.query(
                TABLE_LISTA, ID_COLUMNS, KEY_LID_SELECTION, new String[] { lid },
                null, null, null, "1");
        if (c == null) {
            return -1;
        }
        c.moveToFirst();
        long rv = c.getLong(0);
        c.close();
        return rv;
    }

    private void feedUpDescendentIds(SQLiteDatabase db, List<String> descendentIds, long id)  {
        Cursor c = db.query(TABLE_LISTA, ID_COLUMNS, PARENT_ID_SELECTION,
                new String[] { String.valueOf(id)}, null, null, null, "1");
        if (c != null) {
            try {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    long childId = c.getLong(c.getColumnIndex(KEY_ID));
                    descendentIds.add(String.valueOf(childId));
                    feedUpDescendentIds(db, descendentIds, childId);
                }
            } finally {
                c.close();
            }
        }
    }

    private Lista listaFromCursor(Cursor c) {
        return new Lista(
                c.getString(c.getColumnIndex(KEY_LID)),
                c.getString(c.getColumnIndex(KEY_DESCRIPTION)),
                c.getString(c.getColumnIndex(KEY_ATTRIBUTES)));
    }
}
