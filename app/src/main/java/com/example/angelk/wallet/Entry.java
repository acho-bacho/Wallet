package com.example.angelk.wallet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;


public class Entry
{
    public static final String ITEM_SEP = System.getProperty("line.separator");

    public enum Priority
    {
        LOW, MED, HIGH
    };

    public enum Status
    {
        NOTDONE, DONE
    };
    public final static String AMOUNT = "amount";
    public final static String TITLE = "title";
    public final static String PRIORITY = "priority";
    public final static String STATUS = "status";
    public final static String DATE = "date";
    public final static String FILENAME = "filename";

    public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    private float mAmount = 0;
    private String mTitle = new String();
    private Priority mPriority = Priority.LOW;
    private Status mStatus = Status.NOTDONE;
    private Date mDate = new Date();

    Entry(float amount, String title, Priority priority, Status status, Date date)
    {
        this.mAmount = amount;
        this.mTitle = title;
        this.mPriority = priority;
        this.mStatus = status;
        this.mDate = date;
    }

    // Create a new Entry from data packaged in an Intent
    Entry(Intent intent)
    {
        mAmount = intent.getFloatExtra(Entry.AMOUNT, 0);
        mTitle = intent.getStringExtra(Entry.TITLE);
        mPriority = Priority.valueOf(intent.getStringExtra(Entry.PRIORITY));
        mStatus = Status.valueOf(intent.getStringExtra(Entry.STATUS));

        try
        {
            mDate = Entry.FORMAT.parse(intent.getStringExtra(Entry.DATE));
        }
        catch (ParseException e)
        {
            mDate = new Date();
        }
    }

    public float getAmount()
    {
        return mAmount;
    }
    public void setAmount(float amount) { mAmount = amount;}

    public String getTitle()
    {
        return mTitle;
    }
    public void setTitle(String title)
    {
        mTitle = title;
    }

    public Priority getPriority()
    {
        return mPriority;
    }
    public void setPriority(Priority priority)
    {
        mPriority = priority;
    }

    public Status getStatus()
    {
        return mStatus;
    }
    public void setStatus(Status status)
    {
        mStatus = status;
    }

    public Date getDate()
    {
        return mDate;
    }
    public void setDate(Date date)
    {
        mDate = date;
    }

    // Take a set of String data values and
    // package them for transport in an Intent

    public static void packageIntent(Intent intent, float amount, String title,
                                     Priority priority, Status status, String date)
    {
        intent.putExtra(Entry.AMOUNT, amount);
        intent.putExtra(Entry.TITLE, title);
        intent.putExtra(Entry.PRIORITY, priority.toString());
        intent.putExtra(Entry.STATUS, status.toString());
        intent.putExtra(Entry.DATE, date);

    }

    public String toString()
    {
        return mAmount + ITEM_SEP + mTitle + ITEM_SEP + mPriority + ITEM_SEP + mStatus + ITEM_SEP + FORMAT.format(mDate);
    }

    public String toLog()
    {
        return "Amount:" + mAmount + ITEM_SEP +
                "Title:" + mTitle + ITEM_SEP +
                "Priority:" + mPriority + ITEM_SEP +
                "Status:" + mStatus + ITEM_SEP +
                "Date:" + FORMAT.format(mDate) + "\n";
    }
}
