package com.example.gonmator.lalista_draft.presentation;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ListaActivity extends AppCompatActivity
        implements EditListDialogFragment.AddItemDialogListener, ListaAdapter.Listener,
        ConfigmDialogFragment.ConfirmDialogListener {

    private LaListaDbHelper mDbHelper = null;
    private long mRootId = -1;
    private long mCurrentId = -1;
    private int mDeep = 0;
    private Menu mAppMenu = null;
    private boolean mEditMode = false;
    private boolean mSelectMode = false;


    // Activity

    @Override
    public void onBackPressed() {
        if (!goBack()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        mDbHelper = new LaListaDbHelper(this);
        mRootId = mDbHelper.getRootId();
        mCurrentId = mRootId;
        mEditMode = false;
        mSelectMode = false;

        // check DB consistence
        // int fixed = mDbHelper.fixOrphans(mRootId);

        //  app bar
        Toolbar appBar = (Toolbar)findViewById(R.id.appbar);
        appBar.setTitle("");
        setSupportActionBar(appBar);
        CollapsingToolbarLayout appbarBox = (CollapsingToolbarLayout)findViewById(R.id.appbarBox);
        appbarBox.setTitleEnabled(false);
        Toolbar listTitle = (Toolbar)findViewById(R.id.listBar);
        listTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        Toolbar selectBar = (Toolbar)findViewById(R.id.selectBar);
        selectBar.inflateMenu(R.menu.menu_select);
        selectBar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        selectBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectMode(false);
            }
        });
        selectBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        return deleteSelected();
                    case R.id.action_select_all:
                        break;
                }
                return false;
            }
        });
        selectBar.setTitleTextColor(0xff000000);

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
        ListaAdapter listAdapter = new ListaAdapter(this, this, R.layout.row_lista, null);
        listView.setAdapter(listAdapter);
        updateList(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mAppMenu = menu;
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        MenuItem editModeItem = menu.findItem(R.id.action_edit_mode);
        if (mEditMode) {
            editModeItem.setIcon(R.drawable.ic_visibility_white_24dp);
        } else {
            editModeItem.setIcon(R.drawable.ic_mode_edit_white_24dp);
        }
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
            case R.id.action_edit_mode:
                toggleEditMode();
                return true;
            case R.id.action_select_mode:
                toggleSelectMode();
                return true;
            case R.id.action_delete:
                if (mSelectMode) {
                    listView = (RecyclerView)findViewById(R.id.listView);
                    adapter = (ListaAdapter)listView.getAdapter();
                    Collection<Long> selected = adapter.getSelectedIds();
                    confirmDelete(selected);
                } else {
                    confirmDelete(mCurrentId);
                }
                return true;
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

    void deleteList(long id) {
        List<Long> ancestors = mDbHelper.getIdOfAncestors(mCurrentId);
        mDbHelper.deleteLista(id);
        if (id == mCurrentId) {
            if (ancestors.size() > 0) {
                mCurrentId = ancestors.get(0);
            } else {
                mCurrentId = mRootId;
            }
        } else {
            Iterator<Long> it = ancestors.iterator();
            while (it.hasNext()) {
                if (it.next() == id) {
                    if (it.hasNext()) {
                        mCurrentId = it.next();
                    } else {
                        mCurrentId = mRootId;
                    }
                    break;
                }
            }
        }
    }

    void deleteLists(long[] ids) {
        long oldCurrentId = mCurrentId;
        for (long id: ids) {
            long parent = mDbHelper.getParentId(id);
            deleteList(id);
            if (parent == mCurrentId) {
                oldCurrentId = -1;
            }
        }
        RecyclerView listView = (RecyclerView) findViewById(R.id.listView);
        ListaAdapter adapter = (ListaAdapter) listView.getAdapter();
        adapter.clearSelected();
        setSelectMode(false);
        if (oldCurrentId != mCurrentId) {
            updateList(adapter);
        }
    }

    boolean deleteSelected() {
        RecyclerView listView = (RecyclerView)findViewById(R.id.listView);
        ListaAdapter adapter = (ListaAdapter)listView.getAdapter();
        Collection<Long> selected = adapter.getSelectedIds();
        if (selected.size() > 0) {
            confirmDelete(selected);
            return true;
        }
        return false;
    }

    boolean goBack() {
        boolean rv = true;
        long parent = mDbHelper.getParentId(mCurrentId);
        if (parent != -1) {
            mCurrentId = parent;
        } else {
            rv = mCurrentId != mRootId;
            mCurrentId = mRootId;
        }
        if (rv) {
            updateList();
        }
        return rv;
    }

    void newList(String description) {
        if (description.length() > 0) {
            RecyclerView listView = (RecyclerView)findViewById(R.id.listView);
            ListaAdapter adapter = (ListaAdapter)listView.getAdapter();
            Lista lista = new Lista(description);
            mDbHelper.createLista(lista, mCurrentId);
            updateList(adapter);
        }
    }

    void updateList() {
        RecyclerView listView = (RecyclerView)findViewById(R.id.listView);
        updateList((ListaAdapter)listView.getAdapter());
    }

    void updateList(ListaAdapter adapter) {
        Toolbar listBar = (Toolbar)findViewById(R.id.listBar);
        if (mCurrentId != mRootId) {
            if (listBar != null) {
                listBar.setTitle(mDbHelper.getLista(mCurrentId).getDescription());
                listBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            }
        } else {
            if (listBar != null) {
                listBar.setTitle(R.string.app_name);
                listBar.setNavigationIcon(null);
            }
        }
        Cursor childs = mDbHelper.getListasOf(mCurrentId);
        adapter.changeCursor(childs);
    }

    void setEditMode(boolean editMode) {
        RecyclerView listView = (RecyclerView) findViewById(R.id.listView);
        ListaAdapter adapter = (ListaAdapter)listView.getAdapter();
        setEditMode(editMode, adapter);
    }
    void setEditMode(boolean editMode, ListaAdapter adapter) {
        mEditMode = editMode;
        adapter.setEditMode(editMode);
        if (mEditMode) {
            setSelectMode(false, adapter);
        }
        invalidateOptionsMenu();
    }

    void setSelectMode(boolean selectMode) {
        RecyclerView listView = (RecyclerView) findViewById(R.id.listView);
        ListaAdapter adapter = (ListaAdapter)listView.getAdapter();
        setSelectMode(selectMode, adapter);
    }
    void setSelectMode(boolean selectMode, ListaAdapter adapter) {
        mSelectMode = selectMode;
        adapter.setSelectMode(mSelectMode);
        Toolbar selectBar = (Toolbar)findViewById(R.id.selectBar);
        if (mSelectMode) {
            selectBar.setVisibility(View.VISIBLE);
            setEditMode(false, adapter);
        } else {
            selectBar.setVisibility(View.GONE);
        }
        invalidateOptionsMenu();
    }

    void toggleEditMode() {
        setEditMode(!mEditMode);
    }

    void toggleSelectMode() {
        setSelectMode(!mSelectMode);
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
                deleteLists(new long[]{id});
            } else if (context.containsKey("ids")) {
                long oldCurrentId = mCurrentId;
                deleteLists(context.getLongArray("ids"));
            }
        }
    }


    // ListaAdapter.Listener interface

    @Override
    public void onItemTextUpdated(long id, String newText) {
        mDbHelper.updateListaDescription(id, newText);
        updateList();
    }

    @Override
    public void onSelectedItemsChanged(int selectedCount) {
        Toolbar selectBar = (Toolbar)findViewById(R.id.selectBar);
        if (selectBar!= null) {
            if (selectedCount != 0) {
                MessageFormat fmt = new MessageFormat(getString(R.string.selected_count));
                String title = fmt.format(new Object[]{selectedCount});
                selectBar.setTitle(title);
            } else {
                selectBar.setTitle("");
            }
        }
    }

    @Override
    public void onSubitemsButtonClick(long id) {
        mCurrentId = id;
        mDeep++;
        updateList();
    }
}
