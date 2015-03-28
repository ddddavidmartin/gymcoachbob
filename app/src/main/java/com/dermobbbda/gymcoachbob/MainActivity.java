package com.dermobbbda.gymcoachbob;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.dermobbbda.gymcoachbob.MESSAGE";
    public static final int NEW_WORKOUT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_quit:
                // TODO: Find good way of exiting the app.
                return true;
            case R.id.action_new_workout:
                newWorkout(getCurrentFocus());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_WORKOUT_REQUEST) {
            if (resultCode == RESULT_OK) {
                String res = data.getStringExtra(getString(R.string.EXTRA_WORKOUT_NAME));
                System.out.println("Received workoutName: " + res);
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("New workout creation was cancelled.");
            }
        }
    }

    public void sendMessage(View view) {
        // Handle the sending of a message.
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);

        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
    }

    public void newWorkout(View view) {
        // Add a new workout.
        Intent intent = new Intent(this, NewWorkoutActivity.class);
        startActivityForResult(intent, NEW_WORKOUT_REQUEST);
    }
}
