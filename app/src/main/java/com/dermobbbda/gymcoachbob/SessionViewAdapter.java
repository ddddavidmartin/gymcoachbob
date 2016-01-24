package com.dermobbbda.gymcoachbob;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class SessionViewAdapter extends RecyclerView.Adapter<SessionViewAdapter.ViewHolder> {
    private static List<Session> mDataSet;
    private static SessionViewAdapter mAdapter;

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

    public SessionViewAdapter(List<Session> sessions) {
        mDataSet = sessions;
        mAdapter = this;
    }

    /** Create new Views (called by the layout manager) */
    @Override
    public SessionViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_card_layout, parent, false);
        return new ViewHolder(v);
    }

    /** Replace the contents of a view (invoked by the layout manager) */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Session session = mDataSet.get(position);
        DateFormat df = new DateFormat();
        String date = df.format("dd/MM/yyyy", session.date()).toString();
        holder.mDateTextView.setText(date);
        /* Access to the respective Session members does not quite work yet, we have to improve
         * and reorganise the Session class for this. For now simply put in hardcoded values. */
        holder.mRepetionsTextView.setText("15");
        holder.mWeightTextView.setText("20");
    }

    /** Return the size of your dataset (invoked by the layout manager) */
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
