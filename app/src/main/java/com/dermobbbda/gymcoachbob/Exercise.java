/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.dermobbbda.gymcoachbob;

import android.content.Context;
import android.text.format.DateFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Exercise implements Serializable {
    /* The last time the dates of the Exercises where checked.
     * See datesHaveChangedSinceLastCheck. */
    private static Date mLastDateCheck = null;

    /* Name of the exercise */
    private String mName;
    private List<Session> mSessions;
    /* The default number of Sessions to allocate for an Exercise. */
    private static final int DEFAULT_CAPACITY = 0;
    /* The weight used for the last Session that was added. */
    private double mLastWeight = 0;
    /* The number of repetitions used for the last Session that was added. */
    private int mLastRepetitions = 0;

    Exercise(String name) {
        this(name, DEFAULT_CAPACITY);
    }

    /** Create an Exercise with the given name and enough space allocated to save capacity Sessions. */
    Exercise(String name, int capacity) {
        mName = name;
        ArrayList<Session> sessions = new ArrayList<Session>();
        if (capacity > DEFAULT_CAPACITY) {
            /* Setting the size up front avoids reallocations when Sessions are added. */
            sessions.ensureCapacity(capacity);
        }
        mSessions = sessions;
    }

    public String name() {
        return mName;
    }

    public List<Session> sessions() {
        return mSessions;
    }

    /** Add the given Session to the Exercise.
     *  Use syncExercisesOnFile=true to update the Exercises on file after adding the Session.
     *  Returns the position at which the Session was inserted. */
    public int add(Session session, boolean syncExercisesOnFile) {
        mSessions.add(0, session);
        Collections.sort(mSessions);
        int position = mSessions.indexOf(session);

        boolean setLastWeightAndRepetitions;
        /* We initialise the 'new Session' dialog with the weight and repetitions of the most recently
         * added Session. For this reason we only set these values if the new Session is at the top of
         * the list (i.e. at position 0) or on the same day as the Session at the top of the list. */
        if (position == 0) {
            setLastWeightAndRepetitions = true;
        } else {
            Date mostRecentDate = mSessions.get(0).date();
            setLastWeightAndRepetitions = Util.onSameDay(session.date(), mostRecentDate);
        }
        if (setLastWeightAndRepetitions) {
            mLastWeight = session.weight();
            mLastRepetitions = session.repetitions();
        }

        /* As we are modifying the Sessions directly, we have to notify the Exercise backend
         * about the change. */
        if (syncExercisesOnFile) {
            ExerciseWrapper.notifyExercisesChanged();
        }

        return position;
    }

    @Override
    public String toString() {
        return "Exercise: " + mName;
    }

    /* Return the weight that was used for the last Session. */
    public double lastWeight() {
        return mLastWeight;
    }

    /** Return the number of repetitions that were used for the last Session. */
    public int lastRepetitions() {
        return mLastRepetitions;
    }

    /** Return the most recent Session of this Exercise.
     *  Returns null if there are no Sessions for this Exercise. */
    private Session mostRecentSession() {
        if (mSessions.size() == 0) {
            return null;
        }

        Session lastSession = null;
        for (Session s : mSessions) {
            if ((lastSession == null) || s.date().after(lastSession.date())) {
                lastSession = s;
            /* As soon as we come across a Session that is older, we know that no newer one will
             * follow anymore, as only Sessions on the same day are in ascending order. */
            } else if (s.date().before(lastSession.date())) {
                break;
            }
        }

        return lastSession;
    }

    /** Return a String describing the time since the most recent Session. */
    public String timeSinceMostRecentSession(Context context) {
        Session lastSession = mostRecentSession();
        String last = context.getString(R.string.exercise_last);
        String timeSinceLast;
        if (lastSession == null) {
            timeSinceLast = last  + ": " + context.getString(R.string.time_never);
        } else {
            timeSinceLast = last + ": " + Util.timeSince(context, lastSession.date());
        }
        return timeSinceLast;
    }

    /** Return a String describing the time the Session took place.
     *  If the previous Session was on the same day, an empty String is returned.
     *  If the previous Session took place at an earlier day, the number of days since then will be
     *  included in the returned String. */
    public String timeOfSession(int position) {
        Session session = mSessions.get(position);
        String dateString = DateFormat.format("dd/MM/yyyy", session.date()).toString();

        int days;
        /* The topmost Session always shows the date it was done. */
        if (position == 0) {
            days = Util.daysSinceDate(session.date());
        } else {
            /* The previous Session sits above the current one. If it was on the same day, we do not
             * print the date again. */
            Date previousDate = mSessions.get(position - 1).date();
            if (Util.onSameDay(previousDate, session.date())) {
                return "";

            /* For Session that are not the top ones, we print both the Date and the number of days
             * since the respective previous Session, as it makes it clear how much times has passed
             * between Sessions. */
            } else {
                days = Util.daysBetweenDates(session.date(), previousDate);
            }
        }

        dateString += "  +" + days + "d";
        return dateString;
    }

    /** Return whether the Exercise dates have changed since the last time this method was called.
     *  This allows the caller to check whether it should update any Exercises it is showing. */
    public static boolean datesHaveChangedSinceLastCheck() {
        boolean res = false;
        Date currentDate = new Date();

        /* Rather than checking whether any of the Exercises have actually changed we simply check
         * against the current date, as all 'days since' information needs to be updated on a date
         * change. */
        if ((mLastDateCheck != null) && !Util.onSameDay(mLastDateCheck, currentDate)) {
            res = true;
        }
        mLastDateCheck = currentDate;

        return res;
    }

    /** Return whether the Session below the given position needs to be updated after an update
     *  of the given Session. */
    public boolean nextSessionNeedsUpdate(int position) {
        boolean res = false;
        if (position < (mSessions.size() - 1)) {
            Session one = mSessions.get(position);
            Session other = mSessions.get(position + 1);

            if (!Util.onSameDay(one.date(), other.date())) {
                res = true;
            }
        }
        return res;
    }
}

/** A workout session, i.e. a number of repetitions of an Exercise at a specific date. */
class Session implements Serializable, Comparable<Session> {
    /** The time when the Session was done. */
    private Date mDate;
    /** Number of repetitions of the set. */
    private int mRepetitions;
    /** Weight used for the set. */
    private double mWeight;

    Session(Date date, double weight, int repetitions) {
        mDate = date;
        mWeight = weight;
        mRepetitions = repetitions;
    }

    /** Return the time at which the Session took place. */
    public Date date() {
        return mDate;
    }

    /** Return the weight used for this Session. */
    public double weight() {
        return mWeight;
    }

    /** Return the number of repetitions of this Session. */
    public int repetitions() {
        return mRepetitions;
    }

    /** Compare this Session to another to determine relative ordering.
     *  Sessions on the same day are ordered chronologically in the order they are added.
     *  Sessions on different days are sorted with the most recent one first.
     *  This way the latest day of exercises is always at the top, but the Sessions for a given
     *  day are listed in the order performed.
     */
    public int compareTo(Session s) {
        Date thisDate = date();
        Date otherDate = s.date();
        boolean onSameDay = Util.onSameDay(thisDate, otherDate);

        if (onSameDay && (thisDate.after(otherDate)) ||
            (!onSameDay && thisDate.before(otherDate))) {
            return 1;
        } else if ((onSameDay && thisDate.before(otherDate)) ||
                   (!onSameDay && thisDate.after(otherDate))) {
            return -1;
        } else {
            return 0;
        }
    }
}
