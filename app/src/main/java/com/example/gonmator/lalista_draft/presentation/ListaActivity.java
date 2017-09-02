package com.example.gonmator.lalista_draft.presentation;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gonmator.lalista_draft.BuildConfig;
import com.example.gonmator.lalista_draft.R;
import com.example.gonmator.lalista_draft.debug.DebugActivity;
import com.example.gonmator.lalista_draft.model.LaListaDbHelper;
import com.example.gonmator.lalista_draft.model.Lista;

import java.util.Collection;

public class ListaActivity extends AppCompatActivity
        implements EditListDialogFragment.AddItemDialogListener, ListaAdapter.Listener,
        ConfigmDialogFragment.ConfirmDialogListener {

    enum Mode {
        listView,
        edit
    };

    private LaListaDbHelper mDbHelper = null;
    private long mRootId = -1;
    private long mCurrentId = -1;
    private int mDeep = 0;
    Mode mMode = Mode.listView;
    Menu mAppMenu = null;


    // Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        mDbHelper = new LaListaDbHelper(this);
        mRootId = mDbHelper.getRootId();
        mCurrentId = mRootId;

        // check DB consistence
        // int fixed = mDbHelper.fixOrphans(mRootId);

        //  app bar
        Toolbar appBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(appBar);

        // menu


        // edit text
        EditText editText = (EditText)findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                newList(textView.getText().toString());
                textView.setText("");
                return true;
            }
        });

        // list view
        final RecyclerView listView = (RecyclerView) findViewById(R.id.listView);

/*        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(
                    AdapterView<?> adapterView, View view, int position, long id) {
                if (mMode == Mode.listView) {
                    ListaAdapter adapter = (ListaAdapter)adapterView.getAdapter();
                    adapter.selectListaId(id, true);
                    setMode(Mode.edit);
                }
                return true;
            }
        });
*/

        RecyclerView.Adapter listAdapter = new ListaAdapter(this, this, R.layout.row_lista, null);
        listView.setAdapter(listAdapter);
        updateList(listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mAppMenu = menu;
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        menu.findItem(R.id.action_view).setVisible(mMode != Mode.listView);
        menu.findItem(R.id.action_edit).setVisible(mMode != Mode.edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RecyclerView listView;
        ListaAdapter adapter;

        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                return true;
            case R.id.action_edit:
                setMode(Mode.edit);
                invalidateOptionsMenu();
                return true;
            case R.id.action_view:
                setMode(Mode.listView);
                invalidateOptionsMenu();
                return true;
            case R.id.action_delete:
                if (mMode == Mode.listView) {
                    confirmDelete(mCurrentId);
                    return true;
                } else if (mMode == Mode.edit) {
                    listView = (RecyclerView)findViewById(R.id.listView);
                    adapter = (ListaAdapter)listView.getAdapter();
                    Collection<Long> selected = adapter.getSelectedIds();
                    confirmDelete(selected);
                }
                return false;
            case R.id.action_settings:
                break;
            case R.id.action_debug:
                Intent intent = new Intent();
                intent.setClass(this, DebugActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAddButtonClick(View view) {
        TextView textView = (TextView)findViewById(R.id.editText);
        newList(textView.getText().toString());
        textView.setText("");
    }

    void confirmDelete(long id) {
        Bundle context = new Bundle(1);
        context.putLong("id", id);
        confirmDelete(context, R.string.action_confirm_delete_current);
    }
    void confirmDelete(Collection<Long> ids) {
        Bundle context = new Bundle(1);
        long[] idArray = new long[ids.size()];
        int i = 0;
        for (long id: ids) {
            idArray[i++] = id;
        }
        context.putLongArray("ids", idArray);
        confirmDelete(context, R.string.action_confirm_delete_selected);
    }
    void confirmDelete(Bundle context, int strId) {
        ConfigmDialogFragment confirmDialog = new ConfigmDialogFragment();
        Bundle arguments = new Bundle(3);
        Resources resources = getResources();
        arguments.putCharSequence("title", resources.getString(R.string.action_delete));
        arguments.putCharSequence("message", resources.getString(strId));
        arguments.putBundle("context", context);
        confirmDialog.setArguments(arguments);
        confirmDialog.show(getSupportFragmentManager(), "delete_list");
    }

    void goBack() {
        long parent = mDbHelper.getParentIdOfLista(mCurrentId);
        if (parent != -1) {
            mCurrentId = parent;
        } else {
            mCurrentId = mRootId;
        }
        updateList();
    }

    void newList(String description) {
        RecyclerView listView = (RecyclerView)findViewById(R.id.listView);
        newList(description, listView);
    }

    void newList(String description, RecyclerView listView) {
        if (description.length() > 0) {
            Lista lista = new Lista(description);
            mDbHelper.createLista(lista, mCurrentId);
            updateList(listView);
        }
    }

    void deleteList(long id) {
        long parent = mDbHelper.deleteLista(id);
        if (id == mCurrentId) {
            if (parent != -1) {
                mCurrentId = parent;
            } else {
                mCurrentId = mRootId;
            }
        }
        updateList();
    }

    void updateList() {
        updateList((RecyclerView)findViewById(R.id.listView));
    }

    void updateList(RecyclerView listView) {
        ActionBar actionBar = getSupportActionBar();
        Toolbar listTitle = (Toolbar)findViewById(R.id.listTitle);
        if (mCurrentId != mRootId) {
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            if (listTitle != null) {
                listTitle.setTitle(mDbHelper.getLista(mCurrentId).getDescription());
                listTitle.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            }
        } else {
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
            if (listTitle != null) {
                listTitle.setTitle(R.string.app_name);
                listTitle.setNavigationIcon(null);
            }
        }
        Cursor childs = mDbHelper.getListasOf(mCurrentId);
        ListaAdapter adapter = (ListaAdapter)listView.getAdapter();
        adapter.changeCursor(childs);
    }

    void setMode(Mode mode) {
        mMode = mode;
        RecyclerView listView = (RecyclerView) findViewById(R.id.listView);
        ListaAdapter adapter = (ListaAdapter)listView.getAdapter();
        adapter.setEditMode(mode == Mode.edit);
        adapter.notifyDataSetChanged();
        invalidateOptionsMenu();
    }

    void setEditModeAndSelect(int id) {
        mMode = Mode.edit;
        RecyclerView listView = (RecyclerView) findViewById(R.id.listView);
        ListaAdapter adapter = (ListaAdapter)listView.getAdapter();
        adapter.setEditMode(true);
        adapter.notifyDataSetChanged();
        invalidateOptionsMenu();
    }


    // AddItemDialogListener interface

    @Override
    public void onDialogTextDone(String text) {
        newList(text);
    }


    // ConfirmDialogListener interface

    @Override
    public void onConfirmedClick(String tag, Bundle context) {
        if (tag.equals("delete_list") && context != null) {
            if (context.containsKey("id")) {
                long id = context.getLong("id");
                if (BuildConfig.DEBUG && id != mCurrentId) {
                    throw new AssertionError("id expected to be the same as mCurrentId");
                }
                deleteList(id);
            } else if (context.containsKey("ids")) {
                long[] ids = context.getLongArray("ids");
                for (long id: ids) {
                    deleteList(id);
                }
                RecyclerView listView = (RecyclerView) findViewById(R.id.listView);
                ListaAdapter adapter = (ListaAdapter)listView.getAdapter();
                adapter.clearSelected();
                adapter.notifyDataSetChanged();
            }
        }
    }


    // ListaAdapter.Listener interface

    @Override
    public void onSubitemsButtonClick(long id) {
        mCurrentId = id;
        mDeep++;
        updateList();
    }
}
