/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

/** Utility functions that do not quite fit anywhere else. */
public class Util {
    /** Tag for log prints. */
    public static final String TAG = "GCB";

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

    /** Return a normalised version of the given Date.
     *  The returned Date is set to 00h:00m:00s^ on the same day.
     *  (^ the time in Dates is stored as milliseconds since Midnight Jan 1 1970) */
    private static Date normaliseDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day, /* hour */ 0, /* minute */ 0, /* second */ 0);
        return cal.getTime();
    }

    /** Return the number of days since the given Date. */
    public static int daysSinceDate(Date date) {
        return daysBetweenDates(date, new Date());
    }

    /** Return the number of days between the two Dates.
     *  d1 should be before d2 for a positive value of days to be returned. */
    public static int daysBetweenDates(Date d1, Date d2) {
        d1 = normaliseDate(d1);
        d2 = normaliseDate(d2);
        /* Convert time in 'milliseconds since Jan 1 1970 Midnight GMT' to number of days */
        long startDay = d1.getTime() / 1000 / 60 / 60 / 24;
        long endDay = d2.getTime() / 1000 / 60 / 60 / 24;

        return (int) (endDay - startDay);
    }

    /* Return the time since the given date.
     * Example return values:
     *  in the future, today, yesterday, x days ago, ... */
    public static String timeSince(Context context, Date date) {
        String result;
        int daysSinceLastExercise = daysSinceDate(date);
        if (daysSinceLastExercise < 0) {
            result = context.getString(R.string.time_future);
        } else if (daysSinceLastExercise == 0) {
            result = context.getString(R.string.time_today);
        } else if (daysSinceLastExercise == 1) {
            result = context.getString(R.string.time_yesterday);
        } else {
            result = "" + daysSinceLastExercise + " " + context.getString(R.string.time_days_ago);
        }
        return result;
    }

    /** Return a String representation of the given Date. */
    public static String dateString(Context context, Date date) {
        /* This call for getDateFormat respects the currently set locale on the device and
         * returns an appropriately formatted date. */
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        return dateFormat.format(date);
    }
}
