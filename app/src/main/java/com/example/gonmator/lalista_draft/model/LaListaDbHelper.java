package com.example.gonmator.lalista_draft.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

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
    private static final String[] PARENT_ID_COLUMNS = { KEY_PARENT_ID };
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

    // Consistence

    public List<Long> findOrphans() {
        List<Long> rv = new Vector<Long>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_LISTA, ID_COLUMNS, null, null, null, null, null);
        if (c != null) {
            try {
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    long id = c.getLong(0);
                    long root = getRootOf(db, id);
                    if (root != -1) {
                        rv.add(id);
                    }
                }
            } finally {
                c.close();
            }
        }
        return rv;
    }

    public int fixOrphans(long root) {
        int count = 0;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cAll = db.query(TABLE_LISTA, ID_COLUMNS, null, null, null, null, null);
        if (cAll != null) {
            try {
                for (cAll.moveToFirst(); !cAll.isAfterLast(); cAll.moveToNext()) {
                    long id = cAll.getLong(0);
                    long ancestor = getRootOf(db, id);
                    if (ancestor != -1) {
                        ContentValues values = new ContentValues();
                        values.put(KEY_PARENT_ID, root);
                        count += db.update(TABLE_LISTA, values, PARENT_ID_SELECTION,
                                new String[] { String.valueOf(ancestor)});
                    }
                }
            } finally {
                cAll.close();
            }
        }
        return count;
    }

    // Specific helper function

    public long createLista(Lista lista, long parentId) {
        SQLiteDatabase db = getWritableDatabase();
        return createLista(db, lista, parentId);
    }

    public Cursor getAllListas() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_LISTA, null, null, null, null, null, KEY_DESCRIPTION);
        return c;
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
                new String[] { String.valueOf(parentId)}, null, null, KEY_DESCRIPTION);
    }
    public Cursor getListasOf(String parentLid) {
        SQLiteDatabase db = getReadableDatabase();
        long parentId = getIndex(db, parentLid);
        if (parentId == -1) return null;
        return getListasOf(parentId);
    }

    public Cursor getOrphanListas() {
        List<Long> orphanIds = findOrphans();
        SQLiteDatabase db = getReadableDatabase();

        String where = TextUtils.join(",", Collections.nCopies(orphanIds.size(), "?"));
        String[] selectionArgs = new String[orphanIds.size()];
        int i = 0;
        for (Long id: orphanIds) {
            selectionArgs[i] = String.valueOf(id);
            i++;
        }
        Cursor c = db.query(TABLE_LISTA, null, KEY_ID + " IN (" + where + ")", selectionArgs,
                null, null, null);
        return c;
    }

    public long getParentIdOfLista(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_LISTA, PARENT_ID_COLUMNS, ID_SELECTION,
                new String[]{String.valueOf(id)}, null, null, null, "1");
        if (c != null) {
            try {
                c.moveToFirst();
                if (!c.isAfterLast()) {
                    return c.getLong(0);
                }
            } finally {
                c.close();
            }
        }
        return -1;
    }

    public void updateLista(Lista lista) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, lista.getDescription());
        values.put(KEY_ATTRIBUTES, lista.getAttributesStr());

        db.update(TABLE_LISTA, values, KEY_LID_SELECTION, new String[] { lista.getLidStr() });
    }

    public long deleteLista(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return deleteLista(db, id);
    }
    public long deleteLista(String lid) {
        SQLiteDatabase db = getWritableDatabase();
        long id = getIndex(db, lid);
        return deleteLista(id);
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

    private long deleteLista(SQLiteDatabase db, long id) {
        // get the parent to return after
        Cursor c = db.query(TABLE_LISTA, new String[] { KEY_PARENT_ID }, ID_SELECTION,
                new String[] { String.valueOf(id)}, null, null, null, "1");
        long parent = -1;
        if (c != null) {
            try {
                c.moveToFirst();
                if (c.isAfterLast()) {
                    return -1;
                }
                parent = c.getLong(0);
            } finally {
                c.close();
            }
        }

        try {
            // recursive, get the list of descendent
            List<String> descendentIds = new ArrayList<>();
            feedUpDescendentIds(db, descendentIds, id);
            if (!descendentIds.isEmpty()) {
                String where = TextUtils.join(",", Collections.nCopies(descendentIds.size(), "?"));
                int deleted = db.delete(TABLE_LISTA, KEY_ID + " IN ("  + where + ")",
                        descendentIds.toArray(new String[0]));
            }
            if (id != getRootId()) {
                db.delete(TABLE_LISTA, KEY_ID + " = ?", new String[]{String.valueOf(id)});
            }
        } catch (Exception e) {
            Exception f = e;
        }
        return parent;
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
                new String[] { String.valueOf(id) }, null, null, null);
        if (c != null) {
            try {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    long childId = c.getLong(c.getColumnIndex(KEY_ID));
                    descendentIds.add(String.valueOf(childId));
                    feedUpDescendentIds(db, descendentIds, childId);
                    c.moveToNext();
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

    private long getRootOf(SQLiteDatabase db, long id) {
        Cursor c = db.query(TABLE_LISTA, PARENT_ID_COLUMNS, ID_SELECTION,
                new String[] { String.valueOf(id)}, null, null, null, "1");
        if (c != null) {
            try {
                c.moveToFirst();
                if (c.isAfterLast()) {
                    return id;
                }
                long parentId = c.getLong(0);
                if (parentId != -1) {
                    return getRootOf(db, parentId);
                }
                return -1;
            } finally {
                c.close();
            }
        }
        return id;
    }
}
