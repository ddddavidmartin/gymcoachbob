package com.dermobbbda.gymcoachbob;

import java.io.Serializable;
import java.util.Date;

/** A workout session, i.e. a number of repetitions of an Exercise at a specific date. */
class ExerciseSession implements Serializable, Comparable<ExerciseSession> {
    /** The time when the ExerciseSession was done. */
    private Date mDate;
    /** Number of repetitions of the set. */
    private int mRepetitions;
    /** Weight used for the set. */
    private double mWeight;

    ExerciseSession(Date date, double weight, int repetitions) {
        mDate = date;
        mWeight = weight;
        mRepetitions = repetitions;
    }

    /** Return the time at which the ExerciseSession took place. */
    public Date date() {
                 return mDate;
                              }

    /** Return the weight used for this ExerciseSession. */
    public double weight() {
                     return mWeight;
                                    }

    /** Return the number of repetitions of this ExerciseSession. */
    public int repetitions() {
                       return mRepetitions;
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
