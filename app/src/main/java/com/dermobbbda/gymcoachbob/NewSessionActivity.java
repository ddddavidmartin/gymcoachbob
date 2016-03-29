/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.dermobbbda.gymcoachbob;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class NewSessionActivity extends Activity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "GCB";
    private Date mDate;
    private int mRepetitions;
    private double mWeight;
    /** The fraction part of mWeight to use for the NumberPickers. */
    private int mFraction;
    /** The whole part of mWeight to use for the NumberPickers. */
    private int mWhole;

    public static class DatePickerFragment extends DialogFragment {
        private DatePickerDialog.OnDateSetListener mCallback;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), mCallback, year, month, day);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            /* Make sure the container Activity has implemented the callback interface. */
            try {
                mCallback = (DatePickerDialog.OnDateSetListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnDateSetListener");
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mCallback = null;
        }
    }

    /** Set mWeight as a double composed of the given whole and fraction values.
     *  For example 'setWeight(10, 5)' sets mWeight to 10.5. */
    private void setWeight(int whole, int fraction) {
        /* We hardcode to expect one fraction digit. This is somewhat bad for maintenance and future
         * changes, so we raise an Exception if this condition is not met anymore. */
        if (String.valueOf(fraction).length() > 1) {
            throw new IllegalArgumentException("Fraction " + fraction + " must have 1 digit max.");
        }
        mWeight = whole + (fraction / 10.0);
    }

    /** Return the whole part of the given double value.
     *  For example 'whole(10.5)' returns 10. */
    private int whole(double value) {
        return (int) value;
    }

    /** Return the fraction part of the given double value.
     *  For example 'fraction(10.5)' returns 5. */
    private int fraction(double value) {
        /* We hardcode to expect one fraction digit. This is somewhat bad for maintenance and future
         * changes, so we raise an Exception if this condition is not met anymore. */
        if (String.valueOf(value).contains(".") && ((String.valueOf(value).split("\\.")[1].length()) > 1)) {
            throw new IllegalArgumentException("Fraction part of " + value + " must have 1 digits max.");
        }
        return (int) ((value - Math.floor(value)) * 10);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);

        /* Initialise the date for the new Session with the current date. */
        mDate = new Date();
        DateFormat df = new DateFormat();
        String date = df.format("dd/MM/yyyy", mDate).toString();
        TextView dateText = (TextView) findViewById(R.id.new_session_date_text);
        dateText.setText("Today" + "\n" + date);

        Intent intent = getIntent();
        /* We initialise the weight and repetitions with the previously used values, as the new
         * values are most likely close to those and easier to select this way. For example it is
         * quite common to have three Sessions with the same weight, and close repetitions. */
        mWeight = intent.getDoubleExtra(getString(R.string.EXTRA_LAST_WEIGHT), 0);
        mFraction = fraction(mWeight);
        mWhole = whole(mWeight);
        mRepetitions = intent.getIntExtra(getString(R.string.EXTRA_LAST_REPETITIONS), 0);

        NumberPicker weightPicker = (NumberPicker) findViewById(R.id.new_session_weight_picker);
        weightPicker.setMaxValue(getResources().getInteger(R.integer.new_session_max_weight));
        weightPicker.setMinValue(getResources().getInteger(R.integer.new_session_min_weight));
        weightPicker.setValue(mWhole);
        weightPicker.setWrapSelectorWheel(false);
        weightPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                mWhole = newValue;
                setWeight(mWhole, mFraction);
            }
        });

        NumberPicker weightDecimalPicker = (NumberPicker) findViewById(R.id.new_session_weight_decimal_picker);
        weightDecimalPicker.setMaxValue(getResources().getInteger(R.integer.new_session_max_decimal_weight));
        weightDecimalPicker.setMinValue(getResources().getInteger(R.integer.new_session_min_decimal_weight));
        weightDecimalPicker.setValue(mFraction);
        weightDecimalPicker.setWrapSelectorWheel(false);
        weightDecimalPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                mFraction = newValue;
                setWeight(mWhole, mFraction);
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
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.show(getFragmentManager(), "datePicker");
    }

    /** Callback for when the Date is selected in the DatePicker. */
    public void onDateSet(DatePicker view, int year, int month, int day) {
        final Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        mDate = c.getTime();
        TextView dateText = (TextView) findViewById(R.id.new_session_date_text);
        dateText.setText("" + day + "/" + month + "/" + year);
        Log.d(TAG, "Picked date: " + day + "/" + month + "/" + year);
    }


    public void addSession(View view) {
        Session session = new Session(mDate, mWeight, mRepetitions);
        Log.d(TAG, "Created new session: " + session);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.EXTRA_SESSION), session);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
