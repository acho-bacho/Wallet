package com.example.angelk.wallet;

import android.app.Activity;

import java.util.Calendar;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.ExtractedTextRequest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AddEntryActivity extends Activity
{
    // 7 days in milliseconds - 7 * 24 * 60 * 60 * 1000
    private static final int SEVEN_DAYS = 604800000;
    private static final String TAG = "WalletLogTag";

    private static String timeString;
    private static String dateString;

    private long mEntryToEditId;
    private EditText mAmountText;
    private EditText mTitleText;

    private RadioGroup mCategoryRadio;
    private RadioButton mDefaultCategoryBtn;
    private RadioGroup mTypeRadioGroup;
    private RadioButton mDefaultTypeBtn;
    private Date mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_entry);

        mAmountText = (EditText) findViewById(R.id.amount);
        mTitleText = (EditText) findViewById(R.id.title);
        mDefaultTypeBtn = (RadioButton) findViewById(R.id.typeExpense);
        mDefaultCategoryBtn = (RadioButton) findViewById(R.id.catOther);
        mCategoryRadio = (RadioGroup) findViewById(R.id.categoryGroup);
        mTypeRadioGroup = (RadioGroup) findViewById(R.id.typeGroup);

        Intent i = getIntent();
        Entry entryToEdit = (Entry) i.getSerializableExtra(Entry.ENTRY_OBJ);

        if(entryToEdit!=null)
        {
            mEntryToEditId = i.getLongExtra(Entry.ENTRY_ID, -1);

            mAmountText.setText(Float.toString(entryToEdit.getAmount()));
            mTitleText.setText(entryToEdit.getTitle());

            if(entryToEdit.getType()== Entry.Type.EXPENSE)
            {
                mTypeRadioGroup.check(R.id.typeExpense);
            }
            else
            {
                mTypeRadioGroup.check(R.id.typeIncome);
            }

            if(entryToEdit.getCategory() == Entry.Category.PERSONAL)
            {
                mCategoryRadio.check(R.id.catPersonal);
            }
            else if(entryToEdit.getCategory() == Entry.Category.AUTO)
            {
                mCategoryRadio.check(R.id.catAuto);
            }
            else
            {
                mCategoryRadio.check(R.id.catOther);
            }
        }


        // Set the default date and time
        setDefaultDateTime();

        // OnClickListener for the Cancel Button,
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        final Button resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                resetDefaultValues();
                setDefaultDateTime();
            }
        });

        // Set up OnClickListener for the Submit Button
        final Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // gather ToDoItem data
                Entry.Category category = getCategory();
                Entry.Type type = getType();

                float amount = mAmountText.getText().length() > 0 ? Float.parseFloat(mAmountText.getText().toString()) : 0;
                String titleString = mTitleText.getText().toString();

                // Construct the Date string
                String fullDate = dateString + " " + timeString;

                // Package ToDoItem data into an Intent
                Intent data = new Intent();
                Entry.packageIntent(data, amount, titleString, category, type, fullDate);
                data.putExtra(Entry.ENTRY_ID, mEntryToEditId);
                //return data Intent and finish
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    private void resetDefaultValues()
    {
        mTitleText.setText("");
        mDefaultTypeBtn.setChecked(true);
        mDefaultCategoryBtn.setChecked(true);
    }
    // Do not modify below this point.

    private void setDefaultDateTime()
    {
        // Default is current time + 7 days
        mDate = new Date();
        mDate = new Date(mDate.getTime() + SEVEN_DAYS);

        Calendar c = Calendar.getInstance();
        c.setTime(mDate);

        setDateString(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));

        setTimeString(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                c.get(Calendar.MILLISECOND));
    }

    private static void setDateString(int year, int monthOfYear, int dayOfMonth)
    {

        // Increment monthOfYear for Calendar/Date -> Time Format setting
        monthOfYear++;
        String mon = "" + monthOfYear;
        String day = "" + dayOfMonth;

        if (monthOfYear < 10)
            mon = "0" + monthOfYear;
        if (dayOfMonth < 10)
            day = "0" + dayOfMonth;

        dateString = year + "-" + mon + "-" + day;
    }

    private static void setTimeString(int hourOfDay, int minute, int mili)
    {
        String hour = "" + hourOfDay;
        String min = "" + minute;

        if (hourOfDay < 10)
            hour = "0" + hourOfDay;
        if (minute < 10)
            min = "0" + minute;

        timeString = hour + ":" + min + ":00";
    }

    private Entry.Category getCategory()
    {
        switch (mCategoryRadio.getCheckedRadioButtonId())
        {
            case R.id.catPersonal:
            {
                return Entry.Category.PERSONAL;
            }
            case R.id.catAuto:
            {
                return Entry.Category.AUTO;
            }
            default:
            {
                return Entry.Category.OTHER;
            }
        }
    }

    private Entry.Type getType()
    {
        switch (mTypeRadioGroup.getCheckedRadioButtonId())
        {
            case R.id.typeIncome:
            {
                return Entry.Type.INCOME;
            }
            default:
            {
                return Entry.Type.EXPENSE;
            }
        }
    }

    private String getToDoTitle()
    {
        return mTitleText.getText().toString();
    }
}
