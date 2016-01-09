package com.dermobbbda.gymcoachbob;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class WorkoutViewAdapter extends RecyclerView.Adapter<WorkoutViewAdapter.ViewHolder> {
    private static List<Session> mDataSet;
    private static WorkoutViewAdapter mAdapter;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.exercise_row_item_text_view);
        }
    }

    public WorkoutViewAdapter(List<Session> sessions) {
        mDataSet = sessions;
        mAdapter = this;
    }

    /** Create new Views (called by the layout manager) */
    @Override
    public WorkoutViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercise_card_layout, parent, false);
        return new ViewHolder(v);
    }

    /** Replace the contents of a view (invoked by the layout manager) */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String name = "JustSomeExampleData";
        holder.mTextView.setText(name);
    }

    /** Return the size of your dataset (invoked by the layout manager) */
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
