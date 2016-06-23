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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.angelk.wallet.Entry.Type;

public class MainActivity extends ListActivity
{
    private static final int ADD_ENTRY_REQUEST = 0;
    private static final int EDIT_ENTRY_REQUEST = 1;

    private static final String FILE_NAME = "WalletActivityData.txt";
    private static final String EXPORT_FILE_NAME = "export_WalletActivityData.txt";
    private static final String TAG = "WalletLogTag";

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
                startAddActivity();
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


        getListView().setLongClickable(true);
        registerForContextMenu(getListView());

//        setContentView(R.layout.activity_main);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//
//        fab.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                startAddActivity();
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == ADD_ENTRY_REQUEST)
            {
                Entry newItem = new Entry(data);
                mAdapter.add(newItem);
            }
            else if (requestCode == EDIT_ENTRY_REQUEST)
            {
                Entry entryToEdit = new Entry(data);
                long mEntryToEditId = data.getLongExtra(Entry.ENTRY_ID, -1);
                mAdapter.update((int) mEntryToEditId, entryToEdit);
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
        saveItems();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        long id = getListAdapter().getItemId(info.position);

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
                startEditActivity(info.id);
                return true;
            case CTXT_MENU_CMD_DELETE:
                deleteEntry(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_delete_all:
            {
                mAdapter.clear();
                return true;
            }
            case R.id.action_export:
            {
                saveItems(EXPORT_FILE_NAME);
                return true;
            }
            case R.id.action_import:
            {
                loadItems(EXPORT_FILE_NAME);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
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
                mAdapter.add(new Entry(Float.parseFloat(amount), Type.valueOf(type), title, category));
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

    // Save Entries to file
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
    public void startEditActivity(long id)
    {
        Entry entryToEdit = (Entry) mAdapter.getItem((int) id);

        if(entryToEdit==null)
        {
            return;
        }

        Intent editIntent = new Intent(MainActivity.this, AddEntryActivity.class);

        editIntent.putExtra(Entry.ENTRY_ID, id);
        editIntent.putExtra(Entry.ENTRY_OBJ, entryToEdit);
        startActivityForResult(editIntent, EDIT_ENTRY_REQUEST);
    }

    private void startAddActivity()
    {
        Intent addEntry = new Intent(MainActivity.this, AddEntryActivity.class);
        startActivityForResult(addEntry, ADD_ENTRY_REQUEST);
    }
}
