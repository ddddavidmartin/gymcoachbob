package com.dermobbbda.gymcoachbob;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import java.util.Calendar;
import java.util.Date;

public class NewSessionActivity extends Activity {
    private static final String TAG = "GCB";
    private int mRepetitions;
    private int mWeight;
    /* Reference to the DatePicker so that we add new Sessions with the selected date. */
    private DatePickerFragment mDatePicker;

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private int mYear;
        private int mMonth;
        private int mDay;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Log.d(TAG, "Picked date: " + day + "/" + month + "/" + year);
            mYear = year;
            mMonth = month;
            mDay = day;
        }

        /** Return a Date object for the date that was picked in the DatePicker. */
        public Date date() {
            final Calendar c = Calendar.getInstance();
            c.set(mYear, mMonth, mDay);
            return c.getTime();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);

        Intent intent = getIntent();
        /* We initialise the weight and repetitions with the previously used values, as the new
         * values are most likely close to those and easier to select this way. For example it is
         * quite common to have three Sessions with the same weight, and close repetitions. */
        mWeight = intent.getIntExtra(getString(R.string.EXTRA_LAST_WEIGHT), 0);
        mRepetitions = intent.getIntExtra(getString(R.string.EXTRA_LAST_REPETITIONS), 0);

        NumberPicker weightPicker = (NumberPicker) findViewById(R.id.new_session_weight_picker);
        weightPicker.setMaxValue(getResources().getInteger(R.integer.new_session_max_weight));
        weightPicker.setMinValue(getResources().getInteger(R.integer.new_session_min_weight));
        weightPicker.setValue(mWeight);
        weightPicker.setWrapSelectorWheel(false);
        weightPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                mWeight = newValue;
            }
        });

        NumberPicker repetitionsPicker = (NumberPicker) findViewById(R.id.new_session_repetitions_picker);
        repetitionsPicker.setMaxValue(getResources().getInteger(R.integer.new_session_max_repetitions));
        repetitionsPicker.setMinValue(getResources().getInteger(R.integer.new_session_min_repetitions));
        repetitionsPicker.setValue(mRepetitions);
        repetitionsPicker.setWrapSelectorWheel(false);
        repetitionsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                mRepetitions = newValue;
            }
        });
    }

    public void showDatePickerDialog(View v) {
        if (mDatePicker == null) {
            mDatePicker = new DatePickerFragment();
        }
        mDatePicker.show(getFragmentManager(), "datePicker");
    }

    public void addSession(View view) {
        Date date = new Date();
        /* If the user picked a date via the DatePicker we use it, otherwise we just use the
         * current one. */
        if (mDatePicker != null) {
            date = mDatePicker.date();
        }
        Session session = new Session(date, mWeight, mRepetitions);
        Log.d(TAG, "Created new session: " + session);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.EXTRA_SESSION), session);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
