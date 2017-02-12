/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

public class ViewWeightBasedExerciseActivity extends ViewExerciseActivity {

    /** Set up the Exercise layout for when there are Sessions for this Exercise. */
    protected void setUpNonEmptyExercise() {
        setContentView(R.layout.activity_view_weightbased_exercise);
        mRecyclerView = (RecyclerView) findViewById(R.id.view_exercise_recyclerview);
        /* Improves performance. Only set to true if changes in content do not change the
         * layout size of the RecyclerView. */
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new WeightBasedSessionViewAdapter(getApplicationContext(), mExercise);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_new_session:
                Intent intent = new Intent(this, NewWeightBasedSessionActivity.class);
                /* Provide the last added Session details so that the Activity can be initialised
                 * with good default values. */
                intent.putExtra(getString(R.string.EXTRA_LAST_WEIGHT),
                        ((WeightBasedExercise) mExercise).lastWeight());
                intent.putExtra(getString(R.string.EXTRA_LAST_REPETITIONS),
                        ((WeightBasedExercise) mExercise).lastRepetitions());
                startActivityForResult(intent, NEW_SESSION_REQUEST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
