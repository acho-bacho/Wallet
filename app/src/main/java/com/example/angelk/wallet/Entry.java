package com.example.angelk.wallet;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;


public class Entry implements Serializable
{
    public static final String ITEM_SEP = System.getProperty("line.separator");

    public enum Type
    {
        INCOME, EXPENSE
    };

    public enum Category
    {
        OTHER, PERSONAL, AUTO, UTILITIES
    };

    public final static String ENTRY_OBJ = "entry";
    public final static String AMOUNT = "amount";
    public final static String TITLE = "title";
    public final static String TYPE = "type";
    public final static String CATEGORY = "priority";
    public final static String DATE = "date";
    public final static String FILENAME = "filename";

    public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    private float mAmount = 0;
    private String mTitle = new String();
    private Category mCategory = Category.OTHER;
    private Type mType = Type.EXPENSE;
    private Date mDate = new Date();

    Entry(float amount, Type type, String title)
    {
        this.mAmount = amount;
        this.mType = type;
        this.mTitle = title;
        this.mCategory = Category.OTHER;
        this.mDate = new Date();
    }

    // Create a new Entry from data packaged in an Intent
    Entry(Intent intent)
    {
        mAmount = intent.getFloatExtra(Entry.AMOUNT, 0);
        mTitle = intent.getStringExtra(Entry.TITLE);
        mCategory = Category.valueOf(intent.getStringExtra(Entry.CATEGORY));
        mType = Type.valueOf(intent.getStringExtra(Entry.TYPE));

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

    public Category getPriority()
    {
        return mCategory;
    }
    public void setPriority(Category category)
    {
        mCategory = category;
    }

    public Type getType()
    {
        return mType;
    }
    public void setType(Type type)
    {
        mType = type;
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
                                     Category category, Type type, String date)
    {
        intent.putExtra(Entry.AMOUNT, amount);
        intent.putExtra(Entry.TITLE, title);
        intent.putExtra(Entry.CATEGORY, category.toString());
        intent.putExtra(Entry.TYPE, type.toString());
        intent.putExtra(Entry.DATE, date);

    }

    public String toString()
    {
        return mAmount + ITEM_SEP + mTitle + ITEM_SEP + mCategory + ITEM_SEP + mType + ITEM_SEP + FORMAT.format(mDate);
    }

    public String toLog()
    {
        return "Amount:" + mAmount + ITEM_SEP +
                "Title:" + mTitle + ITEM_SEP +
                "Category:" + mCategory + ITEM_SEP +
                "Type:" + mType + ITEM_SEP +
                "Date:" + FORMAT.format(mDate) + "\n";
    }
}
