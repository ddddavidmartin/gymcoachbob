/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.dermobbbda.gymcoachbob;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;


public class SessionViewAdapter extends RecyclerView.Adapter<SessionViewAdapter.ViewHolder> {
    private static List<Session> mDataSet;

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
    }

    /** Create new Views (called by the layout manager) */
    @Override
    public SessionViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_card_layout, parent, false);
        return new ViewHolder(v);
    }

    /** Determine the format of the date for the Session at the given position.
     *  For example we only show the date for the topmost Session on a given day, as it makes
     *  the list of Sessions more readable. */
    private String determineDateFormat(int position) {
        Session session = mDataSet.get(position);
        String date = DateFormat.format("dd/MM/yyyy", session.date()).toString();

        /* The topmost Session always shows the date it was done. */
        if (position == 0) {
            return date;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(mDataSet.get(position - 1).date());
        int previousYear = cal.get(Calendar.YEAR);
        int previousMonth = cal.get(Calendar.MONTH);
        int previousDay = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(session.date());
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH);
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        /* The previous Session sits above the current one. If it was on the same day, we do not
         * print the date again. */
        if ((currentYear == previousYear) && (currentMonth == previousMonth) && (currentDay == previousDay)) {
            date = "";
        }
        return date;
    }

    /** Replace the contents of a view (invoked by the layout manager) */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Session session = mDataSet.get(position);
        String date = determineDateFormat(position);
        holder.mDateTextView.setText(date);
        holder.mRepetionsTextView.setText(String.valueOf(session.repetitions()));
        holder.mWeightTextView.setText(String.valueOf(session.weight()));
    }

    /** Return the size of your dataset (invoked by the layout manager) */
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
