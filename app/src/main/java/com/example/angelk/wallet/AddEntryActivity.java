package com.example.angelk.wallet;

import android.app.Activity;

import java.util.Calendar;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

public class AddEntryActivity extends Activity
{
    // 7 days in milliseconds - 7 * 24 * 60 * 60 * 1000
    private static final int SEVEN_DAYS = 604800000;
    private static final String TAG = "WalletLogTag";

    private static String timeString;
    private static String dateString;

    private long mEntryToEditId;


    // UI elements
    private Spinner mSpinner;
    private EditText mAmountText;
    private EditText mTitleText;
    private RadioGroup mTypeRadioGroup;
    private RadioButton mDefaultTypeBtn;
    private Date mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_entry);

        mSpinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default mSpinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.expense_categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the mSpinner
        mSpinner.setAdapter(adapter);


        mAmountText = (EditText) findViewById(R.id.amount);
        mTitleText = (EditText) findViewById(R.id.title);
        mDefaultTypeBtn = (RadioButton) findViewById(R.id.typeExpense);
        mTypeRadioGroup = (RadioGroup) findViewById(R.id.typeGroup);

        Intent i = getIntent();
        Entry entryToEdit = (Entry) i.getSerializableExtra(Entry.ENTRY_OBJ);

        // In case we EDIT an existing entry -> set fields
        if (entryToEdit != null)
        {
            mEntryToEditId = i.getLongExtra(Entry.ENTRY_ID, -1);

            mAmountText.setText(Float.toString(entryToEdit.getAmount()));
            mTitleText.setText(entryToEdit.getTitle());

            if (entryToEdit.getType() == Entry.Type.EXPENSE)
            {
                mTypeRadioGroup.check(R.id.typeExpense);
            } else
            {
                mTypeRadioGroup.check(R.id.typeIncome);
            }

            setSpinnerValues();

            int pos = ((ArrayAdapter)mSpinner.getAdapter()).getPosition(entryToEdit.getCategory());
            if(pos!=-1)
            {
                mSpinner.setSelection(pos);
            }
        }

        // Set the default date and time
        setDefaultDateTime();

        mTypeRadioGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId)
                    {
                        setSpinnerValues();
                    }
                }
        );

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
                // gather ENTRY data
                String category = getCategory();
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

    private String getCategory()
    {
        return mSpinner.getSelectedItem().toString();
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

    private void setSpinnerValues()
    {
        int spinnerArrayResourceId = getType()== Entry.Type.EXPENSE ? R.array.expense_categories : R.array.income_categories;

        mSpinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default mSpinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, spinnerArrayResourceId, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the mSpinner
        mSpinner.setAdapter(adapter);
    }
}
