/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.dermobbbda.gymcoachbob;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

/** Utility functions that do not quite fit anywhere else. */
public class Util {
    /** Determine whether the two given Dates are on the same day. */
    public static boolean onSameDay(Date d1, Date d2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d1);
        int year1 = cal.get(Calendar.YEAR);
        int month1 = cal.get(Calendar.MONTH);
        int day1 = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(d2);
        int year2 = cal.get(Calendar.YEAR);
        int month2 = cal.get(Calendar.MONTH);
        int day2 = cal.get(Calendar.DAY_OF_MONTH);

        if ((year1 == year2) && (month1 == month2) && (day1 == day2)) {
            return true;
        }
        return false;
    }

    /** Return the number of days since the given Date. */
    private static int daysSinceDate(Date date) {
        Calendar cal = Calendar.getInstance();
        /* Convert time in 'milliseconds since Jan 1 1970 Midnight GMT' to number of days */
        long startDay = date.getTime() / 1000 / 60 / 60 / 24;
        long endDay = cal.getTime().getTime() / 1000 / 60 / 60 / 24;

        return (int) (endDay - startDay);
    }

    /* Return the time since the given date.
     * Example return values:
     *  in the future, today, yesterday, x days ago, ... */
    public static String timeSince(Context context, Date date) {
        String result;
        if (onSameDay(new Date(), date)) {
            result = context.getString(R.string.time_today);
        } else {
            int daysSinceLastExercise = daysSinceDate(date);
            if (daysSinceLastExercise < 0) {
                result = context.getString(R.string.time_future);
            /* Even if it is less than 24 hours since the last Session it was the previous day
             * as the case where it is on the same day is covered earlier. */
            } else if (daysSinceLastExercise <= 1) {
                result = context.getString(R.string.time_yesterday);
            } else {
                result = "" + daysSinceLastExercise + " " + context.getString(R.string.time_days_ago);
            }
        }
        return result;
    }
}
