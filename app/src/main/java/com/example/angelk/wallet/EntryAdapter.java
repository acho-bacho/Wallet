package com.example.angelk.wallet;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class EntryAdapter extends BaseAdapter
{

    private final List<Entry> mItems = new ArrayList<Entry>();
    private final Context mContext;

    private static final String TAG = "WalletLogTag";

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
    public void update(int location, Entry item)
    {
        if(mItems.size()>0)
        {
            mItems.set(location, item);
            notifyDataSetChanged();
        }
    }
    public void delete(int location)
    {
        mItems.remove(location);
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
        TextView colorView;
        TextView amountView;
        TextView titleView;
        TextView typeView;
        TextView priorityView;
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
            viewHolder.colorView = (TextView) convertView.findViewById(R.id.colorView);
            viewHolder.amountView = (TextView) convertView.findViewById(R.id.amountView);
            viewHolder.titleView = (TextView) convertView.findViewById(R.id.titleView);
            viewHolder.typeView = (TextView) convertView.findViewById(R.id.typeView);
            viewHolder.priorityView = (TextView) convertView.findViewById(R.id.priorityView);
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
        viewHolder.typeView.setText(currentEntry.getType().toString());
        viewHolder.priorityView.setText(currentEntry.getCategory().toString());

        if (currentEntry.getType()== Entry.Type.EXPENSE)
        {
            viewHolder.colorView.setBackgroundResource(R.color.colorAccent);
            //convertView.setBackgroundResource(R.color.colorListExpense);
        }
        else
        {
            viewHolder.colorView.setBackgroundResource(R.color.colorPrimary);
            //convertView.setBackgroundResource(R.color.colorListIncome);
        }

        // TODOx - Must also set up an OnCheckedChangeListener,
        // which is called when the user toggles the status checkbox
        final View finalConvertView = convertView;

        // Return the View you just created
        return convertView;

    }

    public float getTotalAmount()
    {
        float totalAmount = 0;

        for (int i = 0; i < mItems.size(); i++)
        {
            Entry thisEntry = mItems.get(i);
            if(thisEntry.getType()== Entry.Type.EXPENSE)
            {
                totalAmount -= thisEntry.getAmount();
            }
            else
            {
                totalAmount += thisEntry.getAmount();
            }

        }

        return totalAmount;
    }
}
