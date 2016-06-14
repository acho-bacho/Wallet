package com.example.angelk.wallet;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.angelk.wallet.Entry.Priority;
import com.example.angelk.wallet.Entry.Status;


public class MainActivity extends ListActivity
{
    private static final int ADD_TODO_ITEM_REQUEST = 0;
    private static final String FILE_NAME = "TodoManagerActivityData.txt";
    private static final String TAG = "Lab-UserInterface";

    // IDs for menu items
    private static final int MENU_DELETE = Menu.FIRST;
    private static final int MENU_DUMP = Menu.FIRST + 1;

    EntryAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Create a new TodoListAdapter for this ListActivity's ListView
        mAdapter = new EntryAdapter(getApplicationContext());

        // Put divider between ToDoItems and FooterView
        getListView().setFooterDividersEnabled(true);

        // TODOx - Inflate footerView for footer_view.xml file
        TextView footerView = (TextView) getLayoutInflater().inflate(R.layout.footer_view, null);


        // TODOx - Add footerView to ListView
        getListView().addFooterView(footerView);

        // TODOx - Attach Listener to FooterView
        footerView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODOx - Implement OnClick().
                Intent addToDoIntent = new Intent(MainActivity.this, AddEntryActivity.class);
                startActivityForResult(addToDoIntent, ADD_TODO_ITEM_REQUEST);
            }
        });

        // TODOx - Attach the adapter to this ListActivity's ListView
        setListAdapter(mAdapter);
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


    // Do not modify below here

    @Override
    public void onResume()
    {
        super.onResume();

        // Load saved ToDoItems, if necessary

        if (mAdapter.getCount() == 0)
            loadItems();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // Save ToDoItems

        saveItems();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete all");
        menu.add(Menu.NONE, MENU_DUMP, Menu.NONE, "Dump to log");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case MENU_DELETE:
                mAdapter.clear();
                return true;
            case MENU_DUMP:
                dump();
                return true;
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

    // Load stored ToDoItems
    private void loadItems()
    {
        BufferedReader reader = null;
        try
        {
            FileInputStream fis = openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String title = null;
            String priority = null;
            String status = null;
            Date date = null;

            while (null != (title = reader.readLine()))
            {
                priority = reader.readLine();
                status = reader.readLine();
                date = Entry.FORMAT.parse(reader.readLine());
                mAdapter.add(new Entry(title, Priority.valueOf(priority),
                        Status.valueOf(status), date));
            }

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (ParseException e)
        {
            e.printStackTrace();
        } finally
        {
            if (null != reader)
            {
                try
                {
                    reader.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    // Save ToDoItems to file
    private void saveItems()
    {
        PrintWriter writer = null;
        try
        {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));

            for (int idx = 0; idx < mAdapter.getCount(); idx++)
            {

                writer.println(mAdapter.getItem(idx));

            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (null != writer)
            {
                writer.close();
            }
        }
    }

}
