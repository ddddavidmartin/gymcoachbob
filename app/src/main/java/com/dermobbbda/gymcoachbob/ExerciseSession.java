package com.dermobbbda.gymcoachbob;

import java.io.Serializable;
import java.util.Date;

/** A workout session, i.e. a number of repetitions of an Exercise at a specific date. */
class ExerciseSession implements Serializable, Comparable<ExerciseSession> {
    /** The time when the ExerciseSession was done. */
    protected Date mDate;

    ExerciseSession(Date date) {
        mDate = date;
    }

    /** Return the time at which the ExerciseSession took place. */
    public Date date() {
                 return mDate;
                              }

    /** Compare this ExerciseSession to another to determine relative ordering.
     *  ExerciseSessions on the same day are ordered chronologically in the order they are added.
     *  ExerciseSessions on different days are sorted with the most recent one first.
     *  This way the latest day of exercises is always at the top, but the ExerciseSessions for a given
     *  day are listed in the order performed.
     */
    public int compareTo(ExerciseSession s) {
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
