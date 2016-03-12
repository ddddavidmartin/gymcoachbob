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

public class NewSessionActivity extends Activity {
    private static final String TAG = "GCB";
    private int mRepetitions;
    private int mWeight;

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Log.d(TAG, "Picked date: " + day + "/" + month + "/" + year);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);

        NumberPicker weightPicker = (NumberPicker) findViewById(R.id.new_session_weight_picker);
        weightPicker.setMaxValue(getResources().getInteger(R.integer.new_session_max_weight));
        weightPicker.setMinValue(getResources().getInteger(R.integer.new_session_min_weight));
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
        repetitionsPicker.setWrapSelectorWheel(false);
        repetitionsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                mRepetitions = newValue;
            }
        });
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void addSession(View view) {
        Session session = new Session(mWeight, mRepetitions);
        Log.d(TAG, "Created new session: " + session);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.EXTRA_SESSION), session);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
