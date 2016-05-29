/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.dermobbbda.gymcoachbob;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class WeightBasedSessionViewAdapter extends RecyclerView.Adapter<WeightBasedSessionViewAdapter.ViewHolder> {
    private static Exercise mExercise;
    private static List<ExerciseSession> mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mDateTextView;
        public TextView mWeightTextView;
        public TextView mRepetionsTextView;

        public ViewHolder(View v) {
            super(v);
            mDateTextView = (TextView) v.findViewById(R.id.session_date_textview);
            mWeightTextView = (TextView) v.findViewById(R.id.session_weight_textview);
            mRepetionsTextView = (TextView) v.findViewById(R.id.session_repetitions_textview);
        }
    }

    public WeightBasedSessionViewAdapter(Exercise exercise) {
        mExercise = exercise;
        mDataSet = exercise.sessions();
    }

    /** Create new Views (called by the layout manager) */
    @Override
    public WeightBasedSessionViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weight_based_session_card_layout, parent, false);
        return new ViewHolder(v);
    }

    /** Replace the contents of a view (invoked by the layout manager) */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WeightBasedExerciseSession session = (WeightBasedExerciseSession) mDataSet.get(position);
        holder.mDateTextView.setText(mExercise.timeOfSession(position));
        holder.mRepetionsTextView.setText(String.valueOf(session.repetitions()));
        holder.mWeightTextView.setText(String.valueOf(session.weight()));
    }

    /** Return the size of your dataset (invoked by the layout manager) */
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
