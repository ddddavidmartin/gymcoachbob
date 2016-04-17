/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.dermobbbda.gymcoachbob;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {
    public static final int NEW_EXERCISE_REQUEST = 1;
    public static final String TAG = "GCB";

    /** All Exercises the app knows about. */
    private static ExerciseWrapper mExercises;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerview);

        /* Improves performance. Only set to true if changes in content do not change the
         * layout size of the RecyclerView. */
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mExercises = ExerciseWrapper.getInstance(this);

        mAdapter = new ExerciseViewAdapter(this, mExercises);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* Update the latest Exercise when we return to the MainActivity in case we modified a
           Session of that Exercise. */
        int position = mExercises.last();
        if (position != ExerciseWrapper.NO_LAST_EXERCISE) {
            mAdapter.notifyItemChanged(position);
        }
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
            case R.id.action_new_exercise:
                newExercise(getCurrentFocus());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            Log.d(TAG, "Received cancellation of request with code %d " + requestCode + ".");
            return;
        }

        if (requestCode == NEW_EXERCISE_REQUEST && resultCode == RESULT_OK) {
            Exercise exercise = (Exercise) data.getSerializableExtra(getString(R.string.EXTRA_EXERCISE));
            Log.d(TAG, "Received Exercise: " + exercise);
            int position = mExercises.add(exercise);
            mAdapter.notifyItemInserted(position);
            /* Scroll the view to the newly added Exercise so that it is visible. */
            mRecyclerView.scrollToPosition(position);
        }
    }

    /* Start the respective Activity to add a new exercise. */
    public void newExercise(View view) {
        Intent intent = new Intent(this, NewExerciseActivity.class);
        startActivityForResult(intent, NEW_EXERCISE_REQUEST);
    }
}
