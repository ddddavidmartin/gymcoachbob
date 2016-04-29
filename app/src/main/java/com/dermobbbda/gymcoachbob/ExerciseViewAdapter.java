/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.dermobbbda.gymcoachbob;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ExerciseViewAdapter extends RecyclerView.Adapter<ExerciseViewAdapter.ViewHolder> {
    private static ActionMode mActionMode;
    private static Activity mActivity;
    private static ExerciseWrapper mDataSet;
    private static ExerciseViewAdapter mAdapter;
    /** Value to mark that no position is currently selected. */
    private static final int NO_POSITION_SELECTED = -1;
    /** The currently selected position / Exercise. */
    private static int mSelectedPosition = NO_POSITION_SELECTED;
    /** Flag to mark that an ActionBar item has been selected. In that case the respective
     *  ActionBar item is responsible to unselect any picked items. Whilst the flag is true,
     *  the selection will not automatically be unmarked when the ActionBar is closed. */
    private static boolean mActionItemClicked = false;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameTextView;
        public TextView mTimeSinceTextView;
        public ViewHolder(View v) {
            super(v);
            /* Short clicking one of the listed Exercises opens the respective Exercise in detail. */
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getPosition();
                    if (pos == -1) {
                        return;
                    }

                    Intent intent = new Intent(mActivity.getApplicationContext(), ViewExerciseActivity.class);
                    intent.putExtra(mActivity.getString(R.string.EXTRA_EXERCISE_POSITION), pos);
                    mActivity.startActivity(intent);
                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mActionMode != null) {
                        return false;
                    }
                    int pos = getPosition();
                    if (pos == -1) {
                        return false;
                    }

                    Log.d(Util.TAG, "Element " + pos + " long clicked.");
                    markExerciseAsSelected(pos);

                    v.setSelected(true);
                    /* Reset any previous ActionBar item selections before starting a new ActionBar. */
                    mActionItemClicked = false;
                    mActionMode = mActivity.startActionMode(mActionModeCallback);
                    return true;
                }
            });
            mNameTextView = (TextView) v.findViewById(R.id.exercise_row_item_name_textview);
            mTimeSinceTextView = (TextView) v.findViewById(R.id.exercise_row_item_time_since_textview);
        }
    }

    public ExerciseViewAdapter(Activity activity, ExerciseWrapper exercises) {
        mDataSet = exercises;
        mActivity = activity;
        mAdapter = this;
    }

    /** Mark the given position as being selected.
     *  This means mSelectedPosition will be updated and the ViewAdapter notified. */
    private static void markExerciseAsSelected(int position) {
        mSelectedPosition = position;
        mAdapter.notifyItemChanged(mSelectedPosition);
    }

    /** Unmark the currently selected position.
     *  mSelectedPosition will be reset.
     *  If notifyAdapter is true, the ViewAdapter will be notified about the change.
     *  Returns the position that was unmarked. */
    private static int unmarkSelectedExercise(boolean notifyAdapter) {
        int previousSelection = mSelectedPosition;
        mSelectedPosition = NO_POSITION_SELECTED;
        if (notifyAdapter) {
            mAdapter.notifyItemChanged(previousSelection);
        }
        return previousSelection;
    }

    /** Create new Views (called by the layout manager) */
    @Override
    public ExerciseViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.exercise_card_layout, parent, false);
        return new ViewHolder(v);
    }

    /** Replace the contents of a view (invoked by the layout manager) */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Exercise exercise = mDataSet.get(position);
        String name = exercise.name();
        if (position == mSelectedPosition) {
            name += " (selected)";
        }

        Session lastSession = exercise.lastSession();
        String last = mActivity.getString(R.string.exercise_last);
        String timeSinceLast;
        if (lastSession == null) {
            timeSinceLast = last  + ": " + mActivity.getString(R.string.time_never);
        } else {
            timeSinceLast = last + ": " + Util.timeSince(mActivity.getApplicationContext(), lastSession.date());
        }

        holder.mNameTextView.setText(name);
        holder.mTimeSinceTextView.setText(timeSinceLast);
    }

    /** Return the size of your dataset (invoked by the layout manager) */
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    private static ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        /* Called when the action mode is created; startActionMode() was called */
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            /* Inflate a menu resource providing context menu items */
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.main_exercise_context_menu, menu);
            return true;
        }

        /* Called each time the action mode is shown. Always called after onCreateActionMode, but
         * may be called multiple times if the mode is invalidated. */
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; /* Return false if nothing is done */
        }

        /* Called when the user selects a contextual menu item */
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            mActionItemClicked = true;
            switch (item.getItemId()) {
                case R.id.menu_context_delete_exercise:
                    /* Confirm deletion of Exercise as it can not be recovered. */
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity.getApplicationContext());
                    /* Include the actual Exercise name in the alert title so that it is clear which
                     * selection is going to be deleted. */
                    String title = mActivity.getResources().getString(R.string.alert_delete_exercise_title);
                    title = title + " '" + mDataSet.get(mSelectedPosition).name() + "'?";
                    builder.setMessage(R.string.alert_delete_exercise_message)
                           .setTitle(title);
                    builder.setPositiveButton(R.string.alert_delete_exercise_okay,
                                              new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d(Util.TAG, "Selected to delete the exercise on slot " + mSelectedPosition + ".");
                            int selection = unmarkSelectedExercise(/* do not notify adapter */ false);
                            mDataSet.remove(selection);
                            mAdapter.notifyItemRemoved(selection);
                        }
                    });
                    builder.setNegativeButton(R.string.alert_delete_exercise_cancel,
                                              new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d(Util.TAG, "User cancelled exercise deletion.");
                            unmarkSelectedExercise(/* notify adapter */ true);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    mode.finish(); /* Action picked, so close the CAB */
                    return true;
                default:
                    return false;
            }
        }

        /* Called when the user exits the action mode */
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            /* If no ActionBar item was picked, we have to unmark the selection through here. */
            if (!mActionItemClicked) {
                unmarkSelectedExercise(/* notify adapter */ true);
            }
            mActionMode = null;
        }


    };
}
