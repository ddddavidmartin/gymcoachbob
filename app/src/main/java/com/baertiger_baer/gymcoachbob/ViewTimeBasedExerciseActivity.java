/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

public class ViewTimeBasedExerciseActivity extends ViewExerciseActivity {

    /** Set up the Exercise layout for when there are Sessions for this Exercise. */
    protected void setUpNonEmptyExercise() {
        setContentView(R.layout.activity_view_timebased_exercise);
        mRecyclerView = (RecyclerView) findViewById(R.id.view_exercise_recyclerview);
        /* Improves performance. Only set to true if changes in content do not change the
         * layout size of the RecyclerView. */
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TimeBasedSessionViewAdapter(getApplicationContext(), mExercise);
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
                Intent intent = new Intent(this, NewTimeBasedSessionActivity.class);
                startActivityForResult(intent, NEW_SESSION_REQUEST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
