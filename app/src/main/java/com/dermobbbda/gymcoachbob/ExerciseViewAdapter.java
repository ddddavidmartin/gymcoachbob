package com.dermobbbda.gymcoachbob;

import android.app.Activity;
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
    public static final String TAG = "GCB";
    private static ActionMode mActionMode;
    private static Activity mActivity;
    private static ExerciseWrapper mDataSet;
    private static ExerciseViewAdapter mAdapter;
    /** The currently selected position / Exercise. */
    private static final int NO_POSITION_SELECTED = -1;
    /** Value to mark that no position is currently selected. */
    private static int mSelectedPosition = NO_POSITION_SELECTED;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
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

                    Intent intent = new Intent(mActivity, ViewExerciseActivity.class);
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

                    mSelectedPosition = pos;
                    Log.d(TAG, "Element " + mSelectedPosition + " long clicked.");

                    v.setSelected(true);
                    mActionMode = mActivity.startActionMode(mActionModeCallback);
                    return true;
                }
            });
            mTextView = (TextView) v.findViewById(R.id.exercise_row_item_text_view);
        }
    }

    public ExerciseViewAdapter(Activity activity, ExerciseWrapper exercises) {
        mDataSet = exercises;
        mActivity = activity;
        mAdapter = this;
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
        String name = mDataSet.get(position).getName();
        holder.mTextView.setText(name);
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
            switch (item.getItemId()) {
                case R.id.menu_context_delete_exercise:
                    Log.d(TAG, "Selected to delete the exercise on slot " + mSelectedPosition + ".");
                    mDataSet.remove(mSelectedPosition);
                    mAdapter.notifyItemRemoved(mSelectedPosition);
                    mSelectedPosition = NO_POSITION_SELECTED;
                    mode.finish(); /* Action picked, so close the CAB */
                    return true;
                default:
                    return false;
            }
        }

        /* Called when the user exits the action mode */
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };
}
