/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/** Generic class to create a new ExerciseSession. */
public abstract class NewExerciseSessionActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener {
    protected Date mDate;

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
                mCallback = (DatePickerDialog.OnDateSetListener)activity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mDate = new Date();
        } else {
            mDate = new Date(savedInstanceState.getLong(getString(R.string.EXTRA_LAST_DATE)));
        }
    }

    /** Initialise the date for the new Session. */
    public void initialiseDateText() {
        TextView dateTextView = (TextView) findViewById(R.id.new_session_date_text);
        String dateString = Util.dateString(getApplicationContext(), mDate);
        String dateDescription = Util.timeSince(getApplicationContext(), mDate);
        dateTextView.setText(getString(R.string.activity_new_session_picked_date, dateDescription, dateString));
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
        String dateString = Util.dateString(getApplicationContext(), mDate);
        String timeSince = Util.timeSince(getApplicationContext(), mDate);
        dateText.setText(getString(R.string.activity_new_session_picked_date, timeSince, dateString));
        Log.d("Picked date: " + dateString);
    }

    /** Return a new Intent that contains the given ExerciseSession. */
    public Intent createIntentWithSession(ExerciseSession session) {
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.EXTRA_SESSION), session);
        return intent;
    }

    /** Store the state of the current Activity before a configuration change such as for example a
     *  screen rotation. */
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(getString(R.string.EXTRA_LAST_DATE), mDate.getTime());
    }
}
