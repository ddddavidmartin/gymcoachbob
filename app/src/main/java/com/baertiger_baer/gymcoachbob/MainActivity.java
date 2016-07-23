/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    public static final int NEW_EXERCISE_REQUEST = 1;

    /** All Exercises the app knows about. */
    private static ExerciseWrapper mExercises;
    private RecyclerView mRecyclerView;
    private ExerciseViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExercises = ExerciseWrapper.getInstance(getApplicationContext());
        if (mExercises.size() > 0) {
            setUpNonEmptyLayout();
        } else {
            setUpEmptyLayout();
        }
    }

    /** Set up the layout for when there are existing Exercises to be shown. */
    private void setUpNonEmptyLayout() {
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerview);

        /* Improves performance. Only set to true if changes in content do not change the
         * layout size of the RecyclerView. */
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ExerciseViewAdapter(this, mExercises);
        mRecyclerView.setAdapter(mAdapter);
    }

    /** Set up the layout for when no Exercises have been added yet. */
    private void setUpEmptyLayout() {
        setContentView(R.layout.activity_view_empty);
        TextView textView = (TextView) findViewById(R.id.activity_view_empty_textview);
        textView.setText(R.string.activity_main_empty_description);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* Return early if there are no Exercises yet, as no Exercises are set up. */
        if (mExercises.size() == 0) {
            return;
        }

        /* Update the latest Exercise when we return to the MainActivity in case we modified a
         * Session of that Exercise. */
        int position = mExercises.last();
        if (position != ExerciseWrapper.NO_LAST_EXERCISE) {
            mAdapter.notifyItemChanged(position);
        }

        /* If the date has changed we need to update all Exercise entries to update their
           'days since last Exercise' info. */
        if (Exercise.datesHaveChangedSinceLastCheck()) {
            Log.d("Updating Exercises as dates have changed since last check.");
            /* We call notifyItemRangeChanged rather than notifyDataSetChanged as the former
             * implies changes on only the data of the items, and not the position. This should
             * be more efficient. */
            mAdapter.notifyItemRangeChanged(0, mExercises.size());
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
            case R.id.action_new_exercise:
                newExercise(getCurrentFocus());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_EXERCISE_REQUEST && resultCode == RESULT_OK) {
            setUpNonEmptyLayout();
            Exercise exercise = (Exercise) data.getSerializableExtra(getString(R.string.EXTRA_EXERCISE));
            Log.d("Received Exercise: " + exercise);
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

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        /* For now until we implement correct retaining of the ActionMode after long pressing Exercises
         * we reset the full state, so that the app is at least in a clean state after a configuration
         * change. */
        mAdapter.resetActionMode();
    }
}
