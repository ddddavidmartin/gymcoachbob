/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public abstract class Exercise implements Serializable {
    static final int TYPE_WEIGHT_BASED = 0;
    static final int TYPE_TIME_BASED = 1;
    /* Name of the exercise */
    private String mName;
    List<ExerciseSession> mSessions;
    int mExerciseType;
    /* The last time the dates of the Exercises where checked.
     * See datesHaveChangedSinceLastCheck. */
    private static Date mLastDateCheck = null;
    /* The number of the first Session in a day. */
    private static final int FIRST_SESSION_NUMBER = 1;

    /* The default number of Sessions to allocate for an Exercise. */
    private static final int DEFAULT_CAPACITY = 0;

    Exercise(String name) {
        this(name, DEFAULT_CAPACITY);
    }

    /** Create an Exercise with the given name and enough space allocated to save capacity Sessions. */
    Exercise(String name, int capacity) {
        mName = name;
        ArrayList<ExerciseSession> sessions = new ArrayList<ExerciseSession>();
        if (capacity > DEFAULT_CAPACITY) {
            /* Setting the size up front avoids reallocations when Sessions are added. */
            sessions.ensureCapacity(capacity);
        }
        mSessions = sessions;
    }

    public String name() {
        return mName;
    }

    int type() {
        return mExerciseType;
    }

    List<ExerciseSession> sessions() {
        return mSessions;
    }

    /** Add the given Session to the Exercise.
     *  Use syncExercisesOnFile=true to update the Exercises on file after adding the Session.
     *  Returns the position at which the Session was inserted, and if required,
     *  a range of Sessions that need to be updated. */
    public PositionWithRange add(Context context, ExerciseSession session, boolean syncExercisesOnFile) {
        mSessions.add(0, session);
        Collections.sort(mSessions);
        final int position = mSessions.indexOf(session);
        int itemCount = 0;

        /* We start counting at the first Session on a day. */
        if ((position == 0) || !Util.onSameDay(session.date(), mSessions.get(position - 1).date())) {
            session.mNumber = FIRST_SESSION_NUMBER;
        /* Otherwise we simply continue from the previous one. */
        } else {
            session.mNumber = mSessions.get(position - 1).number() + 1;
        }

        /* If the next Session under the current one is not on the same day, then we have to update
         * it as for example number of days passed since the current and the next Session may have
          * changed. */
        if (position < mSessions.size() - 1) {
            ExerciseSession next = mSessions.get(position + 1);

            if (!Util.onSameDay(session.date(), next.date())) {
                itemCount++;
            }
        }

        /* If we add a Session in the middle of a day, we have to update all following Sessions of
         * on that day to avoid duplicates. */
        if (position < mSessions.size() - 1) {
            for (int i = position + 1; i < mSessions.size(); i++) {
                ExerciseSession nextSession = mSessions.get(i);

                if (Util.onSameDay(nextSession.date(), session.date())) {
                    nextSession.mNumber = nextSession.mNumber + 1;
                    itemCount++;
                } else {
                    break;
                }
            }
        }

        /* As we are modifying the Sessions directly, we have to notify the Exercise backend
         * about the change. */
        if (syncExercisesOnFile) {
            ExerciseWrapper.notifyExercisesChanged(context);
        }

        /* If additional items have changed, then they will always start at the next Session at
         * position + 1, as they either are on the same day or the next item on a different day. */
        final int rangeStart = itemCount > 0 ? position + 1 : PositionWithRange.UNSET;

        return new PositionWithRange(position, rangeStart, itemCount);
    }

    @Override
    public String toString() {
        return "Exercise: " + mName;
    }

    /** Return the most recent Session of this Exercise.
     *  Returns null if there are no Sessions for this Exercise. */
    private ExerciseSession mostRecentSession() {
        if (mSessions.size() == 0) {
            return null;
        }

        ExerciseSession lastSession = null;
        for (ExerciseSession s : mSessions) {
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
    String timeSinceMostRecentSession(Context context) {
        ExerciseSession lastSession = mostRecentSession();
        String timeSinceLast;
        if (lastSession == null) {
            timeSinceLast = context.getString(R.string.time_never);
        } else {
            timeSinceLast = Util.timeSince(context, lastSession.date());
        }
        return timeSinceLast;
    }

    /** Return a String describing the time the Session took place.
     *  If the previous Session was on the same day, an empty String is returned.
     *  If the previous Session took place at an earlier day, the number of days since then will be
     *  included in the returned String. */
    String timeOfSession(Context context, int position) {
        ExerciseSession session = mSessions.get(position);
        String dateString = Util.dateString(context, session.date());

        int days;
        /* The topmost Session always shows the date it was done. */
        if (position == 0) {
            days = Util.daysSinceDate(session.date());
        } else {
            /* The previous Session sits above the current one. If it was on the same day, we do not
             * print the date again. */
            Date previousDate = mSessions.get(position - 1).date();
            if (Util.onSameDay(previousDate, session.date())) {
                return "         ...";

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
    static boolean datesHaveChangedSinceLastCheck() {
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
}
