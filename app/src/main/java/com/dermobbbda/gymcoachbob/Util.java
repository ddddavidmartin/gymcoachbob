/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.dermobbbda.gymcoachbob;

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
}
