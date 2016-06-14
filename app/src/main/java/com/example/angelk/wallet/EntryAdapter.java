package com.example.angelk.wallet;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class EntryAdapter extends BaseAdapter
{

    private final List<Entry> mItems = new ArrayList<Entry>();
    private final Context mContext;

    private static final String TAG = "Lab-UserInterface";

    public EntryAdapter(Context context)
    {

        mContext = context;

    }

    // Add a ToDoItem to the adapter
    // Notify observers that the data set has changed

    public void add(Entry item)
    {
        mItems.add(item);
        notifyDataSetChanged();
    }

    // Clears the list adapter of all items.

    public void clear()
    {
        mItems.clear();
        notifyDataSetChanged();
    }

    // Returns the number of ToDoItems

    @Override
    public int getCount()
    {

        return mItems.size();

    }

    // Retrieve the number of ToDoItems

    @Override
    public Object getItem(int pos)
    {
        return mItems.get(pos);
    }

    // Get the ID for the ToDoItem
    // In this case it's just the position

    @Override
    public long getItemId(int pos)
    {

        return pos;

    }

    static class ViewHolder
    {
        TextView amountView;
        TextView titleView;
        CheckBox statusView;
        TextView priorityView;
        TextView dateView;
    }

    // Create a View for the ToDoItem at specified position
    // Remember to check whether convertView holds an already allocated View
    // before created a new View.
    // Consider using the ViewHolder pattern to make scrolling more efficient
    // See: http://developer.android.com/training/improving-layouts/smooth-scrolling.html

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder viewHolder;

        // TODOx - Get the current ToDoItem
        final Entry currentEntry = (Entry) getItem(position);

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.entry, null);

            viewHolder = new ViewHolder();
            viewHolder.amountView = (TextView) convertView.findViewById(R.id.amountView);
            viewHolder.titleView = (TextView) convertView.findViewById(R.id.titleView);
            viewHolder.statusView = (CheckBox) convertView.findViewById(R.id.statusCheckBox);
            viewHolder.priorityView = (TextView) convertView.findViewById(R.id.priorityView);
            viewHolder.dateView = (TextView) convertView.findViewById(R.id.dateView);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }



        // Fill in specific ToDoItem data
        // Remember that the data that goes in this View
        // corresponds to the user interface elements defined
        // in the layout file
        viewHolder.amountView.setText(Float.toString(currentEntry.getAmount()));
        viewHolder.titleView.setText(currentEntry.getTitle());
        viewHolder.statusView.setChecked(currentEntry.getStatus() == Entry.Status.DONE);
        viewHolder.priorityView.setText(currentEntry.getPriority().toString());
        viewHolder.dateView.setText(Entry.FORMAT.format(currentEntry.getDate()));


        // TODOx - Must also set up an OnCheckedChangeListener,
        // which is called when the user toggles the status checkbox
        final View finalConvertView = convertView;
        viewHolder.statusView.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (buttonView.isChecked())
                {
                    viewHolder.statusView.setChecked(true);
                    currentEntry.setStatus(Entry.Status.DONE);
                } else
                {
                    viewHolder.statusView.setChecked(false);
                    currentEntry.setStatus(Entry.Status.NOTDONE);
                }
            }
        });

        // Return the View you just created
        return convertView;

    }
}
