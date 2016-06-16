package com.example.angelk.wallet;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;

import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;

import com.example.angelk.wallet.Entry.Category;
import com.example.angelk.wallet.Entry.Type;

import android.support.design.widget.Snackbar;

public class MainActivity extends ListActivity
{
    private static final int ADD_TODO_ITEM_REQUEST = 0;
    private static final String FILE_NAME = "WalletActivityData.txt";
    private static final String EXPORT_FILE_NAME = "export_WalletActivityData.txt";
    private static final String TAG = "WalletLogTag";

    // IDs for menu items
    private static final int MENU_DELETE = Menu.FIRST;
    private static final int MENU_EXPORT = Menu.FIRST + 1;
    private static final int MENU_IMPORT = Menu.FIRST + 2;
    private static final int MENU_ADD_TEST_DATA = Menu.FIRST + 3;

    private static final int CTXT_MENU_CMD_EDIT = 0;
    private static final int CTXT_MENU_CMD_DELETE = 1;

    EntryAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Create a new TodoListAdapter for this ListActivity's ListView
        mAdapter = new EntryAdapter(getApplicationContext());

        // Put divider between ToDoItems and FooterView
        getListView().setFooterDividersEnabled(true);

        TextView headerView = (TextView) getLayoutInflater().inflate(R.layout.header_view, null);
        TextView footerView = (TextView) getLayoutInflater().inflate(R.layout.footer_view, null);

        getListView().addHeaderView(headerView);
        getListView().addFooterView(footerView);

        footerView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent addToDoIntent = new Intent(MainActivity.this, AddEntryActivity.class);
                startActivityForResult(addToDoIntent, ADD_TODO_ITEM_REQUEST);
            }
        });

        headerView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateTotalAmount();
            }
        });

        setListAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null)
        {
            fab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        getListView().setLongClickable(true);
//        this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
//        {
//            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id)
//            {
//                Log.v("long clicked","pos: " + position);
//                return true;
//            }
//        });
//
//
        registerForContextMenu(getListView());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(TAG, "Entered onActivityResult()");

        // TODOx - Check result code and request code
        // if user submitted a new ToDoItem
        // Create a new ToDoItem from the data Intent
        // and then add it to the adapter
        if (requestCode == ADD_TODO_ITEM_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
                Entry newItem = new Entry(data);
                mAdapter.add(newItem);
            }
        }

    }


    @Override
    public void onResume()
    {
        super.onResume();

        // Load saved Entries
        if (mAdapter.getCount() == 0)
        {
            loadItems();
        }

        updateTotalAmount();
    }


    @Override
    protected void onPause()
    {
        super.onPause();

        // Save ToDoItems
        saveItems();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        long id = getListAdapter().getItemId(info.position);

        Log.d(TAG, ">>>item id is " + id);
        menu.add(0, CTXT_MENU_CMD_EDIT, 0, R.string.context_menu_edit);
        menu.add(0, CTXT_MENU_CMD_DELETE, 0, R.string.context_menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId())
        {
            case CTXT_MENU_CMD_EDIT:
                Log.d(TAG, ">>> editing " + info.id);
                return true;
            case CTXT_MENU_CMD_DELETE:
                deleteEntry(info.id);
                Log.d(TAG, ">>> deleting " + info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, R.string.options_menu_delete_all);
        menu.add(Menu.NONE, MENU_EXPORT, Menu.NONE, R.string.options_menu_export_to_file);
        menu.add(Menu.NONE, MENU_IMPORT, Menu.NONE, R.string.options_menu_import_from_file);
        menu.add(Menu.NONE, MENU_ADD_TEST_DATA, Menu.NONE, R.string.options_menu_add_test_data);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case MENU_DELETE:
            {
                mAdapter.clear();
                return true;
            }
            case MENU_EXPORT:
            {
                saveItems(EXPORT_FILE_NAME);
                return true;
            }
            case MENU_IMPORT:
            {
                loadItems(EXPORT_FILE_NAME);
                return true;
            }
            case MENU_ADD_TEST_DATA:
            {
                addTestData();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dump()
    {

        for (int i = 0; i < mAdapter.getCount(); i++)
        {
            String data = ((Entry) mAdapter.getItem(i)).toLog();
            Log.i(TAG, "Item " + i + ": " + data.replace(Entry.ITEM_SEP, ","));
        }

    }

    private void addTestData()
    {
        mAdapter.clear();

        Entry newItem = new Entry(940, Type.INCOME, "стартова сума");
        mAdapter.add(newItem);

        newItem = new Entry(40, Type.EXPENSE, "джобни");
        mAdapter.add(newItem);
        newItem = new Entry(100, Type.EXPENSE, "джобни");
        mAdapter.add(newItem);
        newItem = new Entry(22, Type.EXPENSE, "БОБ");
        mAdapter.add(newItem);
        newItem = new Entry(100, Type.EXPENSE, "наем Май месец");
        mAdapter.add(newItem);
        newItem = new Entry(15, Type.EXPENSE, "пазар");
        mAdapter.add(newItem);
        newItem = new Entry(60, Type.EXPENSE, "джобни");
        mAdapter.add(newItem);
        newItem = new Entry(50, Type.EXPENSE, "джобни");
        mAdapter.add(newItem);
        newItem = new Entry(60, Type.EXPENSE, "репонт Астра");
        mAdapter.add(newItem);
        newItem = new Entry(40, Type.EXPENSE, "бензин");
        mAdapter.add(newItem);
        newItem = new Entry(40, Type.EXPENSE, "на Илко за брат ми");
        mAdapter.add(newItem);
        newItem = new Entry(60, Type.EXPENSE, "джобни");
        mAdapter.add(newItem);
        newItem = new Entry(51, Type.EXPENSE, "ток");
        mAdapter.add(newItem);
        updateTotalAmount();
    }

    // Load stored Entries
    private void loadItems(String... filename)
    {
        String filenameToUse = filename.length > 0 ? filename[0] : FILE_NAME;

        BufferedReader reader = null;
        try
        {
            FileInputStream fis = openFileInput(filenameToUse);
            reader = new BufferedReader(new InputStreamReader(fis));

            String amount = null;
            String type = null;
            String title = null;
            String category = null;
            Date date = null;

            while (null != (amount = reader.readLine()))
            {
                title = reader.readLine();
                category = reader.readLine();
                type = reader.readLine();
                date = Entry.FORMAT.parse(reader.readLine());
                mAdapter.add(new Entry(Float.parseFloat(amount), Type.valueOf(type), title));
            }

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != reader)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onContentChanged()
    {
        super.onContentChanged();
        updateTotalAmount();
    }

    // Save ToDoItems to file
    private void saveItems(String... filename)
    {
        String filenameToUse = filename.length > 0 ? filename[0] : FILE_NAME;

        PrintWriter writer = null;
        try
        {
            FileOutputStream fos = openFileOutput(filenameToUse, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));

            for (int idx = 0; idx < mAdapter.getCount(); idx++)
            {
                writer.println(mAdapter.getItem(idx));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != writer)
            {
                writer.close();
            }
        }
    }

    public void updateTotalAmount()
    {
        TextView header = (TextView) findViewById(R.id.headerView);
        if (header != null)
        {
            header.setText(String.format("%.2f", mAdapter.getTotalAmount()));
        }
    }

    public void deleteEntry(long id)
    {
        mAdapter.delete((int) id);
        updateTotalAmount();
    }
}
