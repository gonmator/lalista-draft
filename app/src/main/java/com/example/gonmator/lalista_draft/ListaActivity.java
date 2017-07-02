package com.example.gonmator.lalista_draft;

import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gonmator.lalista_draft.helper.LaListaDbHelper;
import com.example.gonmator.lalista_draft.model.Lista;

import java.util.Stack;

public class ListaActivity extends AppCompatActivity {
    LaListaDbHelper mDbHelper = null;
    private long mRootId = -1;
    private long mCurrentId = -1;
    Stack<Long> mParentStack = null;
    Stack<Long> mPosStack = null;

    ActionMode mActionMode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        mDbHelper = new LaListaDbHelper(this);
        mRootId = mDbHelper.getRootId();
        mCurrentId = mRootId;
        mParentStack = new Stack<Long>();
        mPosStack = new Stack<Long>();

        final ListView listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mParentStack.push(mCurrentId);
                mCurrentId = parent.getItemIdAtPosition(position);
                mPosStack.push(mCurrentId);
                updateList((ListView)parent);
            }
        });
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode mode, int position,
                                                  long id, boolean checked) {
                ListView listView = (ListView)findViewById(R.id.listView);
                ListaAdapter adapter = (ListaAdapter)listView.getAdapter();

            }

            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.item_menu, menu);
                ListView listView = (ListView)findViewById(R.id.listView);
                ListaAdapter adapter = (ListaAdapter)listView.getAdapter();

                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.deleteItem:
                        // delete selected items
                        ListView listView = (ListView)findViewById(R.id.listView);
                        ListaAdapter adapter = (ListaAdapter)listView.getAdapter();
                        SparseBooleanArray checked = listView.getCheckedItemPositions();
                        for (int i = adapter.getCount() - 1; i >= 0; i--) {
                            if (checked.get(i)) {
                                mDbHelper.deleteLista(adapter.getItemId(i));
                            }
                        }
                        updateList(listView);
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
                ListView listView = (ListView)findViewById(R.id.listView);
                ListaAdapter adapter = (ListaAdapter)listView.getAdapter();
                // adapter.setActionMode(false);
            }
        });

        EditText editText = (EditText)findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    newItem(view, listView);
                    return true;
                }
                return false;
            }
        });

        listView.setClickable(true);
        listView.setItemsCanFocus(true);
        listView.setAdapter(new ListaAdapter(
                this, R.layout.row_lista, R.id.textView, null));
        updateList(listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mCurrentId = mParentStack.pop();
                mPosStack.pop();
                updateList();
                return true;
            case R.id.deleteItem:
                mCurrentId = mParentStack.pop();
                long oldId = mPosStack.pop();
                mDbHelper.deleteLista(oldId);
                updateList();
                return true;
        }
        return false;
    }

    public void onAddClick(View view) {
        newItem();
    }

    void updateList() {
        ListView listView = (ListView)findViewById(R.id.listView);
        updateList(listView);
    }

    void updateList(ListView listView) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Lista lista = mDbHelper.getLista(mCurrentId);
            actionBar.setTitle(lista.getDescription());
            actionBar.setDisplayHomeAsUpEnabled(mCurrentId != mRootId);
        }
        Cursor childs = mDbHelper.getListasOf(mCurrentId);
        CursorAdapter adapter = (CursorAdapter)listView.getAdapter();
        adapter.changeCursor(childs);
    }

    void newItem() {
        TextView textView = (TextView)findViewById(R.id.editText);
        ListView listView = (ListView)findViewById(R.id.listView);
        newItem(textView, listView);
    }

    void newItem(TextView textView) {
        ListView listView = (ListView)findViewById(R.id.listView);
        newItem(textView, listView);
    }

    void newItem(TextView textView, ListView listView) {
        String description = textView.getText().toString();
        if (description.length() > 0) {
            Lista lista = new Lista(description);
            mDbHelper.createLista(lista, mCurrentId);
            updateList(listView);
            textView.setText("");
        }
    }
}
