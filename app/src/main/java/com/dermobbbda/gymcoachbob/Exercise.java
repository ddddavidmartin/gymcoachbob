package com.dermobbbda.gymcoachbob;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Exercise implements Serializable {
    /* Name of the exercise */
    private String mName;
    private List<Session> mSessions;
    /* The default number of Sessions to allocate for an Exercise. */
    private static final int DEFAULT_CAPACITY = 0;
    /* The weight used for the last Session that was added. */
    private int mLastWeight = 0;
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

    public String getName() {
        return mName;
    }

    public List<Session> getSessions() {
        return mSessions;
    }

    /** Add the given Session to the Exercise.
     *  Use syncExercisesOnFile=true to update the Exercises on file after adding the Session.
     *  Returns the position at which the Session was inserted. */
    public int add(Session session, boolean syncExercisesOnFile) {
        mSessions.add(session);
        Collections.sort(mSessions);
        int position = mSessions.indexOf(session);
        mLastWeight = session.weight();
        mLastRepetitions = session.repetitions();

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
    public int lastWeight() {
        return mLastWeight;
    }

    /** Return the number of repetitions that were used for the last Session. */
    public int lastRepetitions() {
        return mLastRepetitions;
    }
}

/** A workout session, i.e. a number of repetitions of an Exercise at a specific date. */
class Session implements Serializable, Comparable<Session> {
    /** The time when the Session was done. */
    private Date mDate;
    /** Number of repetitions of the set. */
    private int mRepetitions;
    /** Weight used for the set. */
    private int mWeight;

    Session(Date date, int weight, int repetitions) {
        mDate = date;
        mWeight = weight;
        mRepetitions = repetitions;
    }

    /** Return the time at which the Session took place. */
    public Date date() {
        return mDate;
    }

    /** Return the weight used for this Session. */
    public int weight() {
        return mWeight;
    }

    /** Return the number of repetitions of this Session. */
    public int repetitions() {
        return mRepetitions;
    }

    /** Compare this Session to another to determine relative ordering. */
    public int compareTo(Session s) {
        return mDate.compareTo(s.date());
    }
}
