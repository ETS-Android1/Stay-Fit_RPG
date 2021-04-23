package com.edward_costache.stay_fitrpg.util;

import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Edward Costache
 */
public abstract class Util {
    /**
     * The Calendar Abstract classes uses an offset from the Epoch January 1, 1970 00:00:00.000 GMT (Gregorian) to calculate time using milliseconds.
     * @return The time in milliseconds for the start of the day. Only returns the milliseconds for 00:00:00.
     */
    public static long getToday() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * Works the same as getToday().
     * @return What the milliseconds would be for the start of the next day. Only returns the milliseconds for 00:00:01. An extra second
     * is added to make sure it returns a time in milliseconds for the next day and not today.
     */
    public static long getTomorrow() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 1);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, 1);
        return c.getTimeInMillis();
    }

    /**
     * A method for displaying a simple Snackbar message when a tester clicks on a button which does not yet have a function
     * @param context the current context the user is looking at, otherwise known as Activity.
     * @param view the root container of the context.
     */
    public static void displayNotImplemented(Context context, View view)
    {
        Snackbar.make(context, view, "Not yet implemented", Snackbar.LENGTH_LONG).setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //closes the Snackbar
            }
        }).show();
    }


    /**
     *
     * @return The number of the current week of the year. i.e 52 weeks in a year so for example 01/01/2021 to 08/01/2021 is the 1st week of the year.
     * Result is cast as a string.
     */
    public static String getCurrentWeekOfYear()
    {
        Calendar calendar = new GregorianCalendar();
        return Integer.toString(calendar.get(Calendar.WEEK_OF_YEAR));
    }

    /**
     * The formatter is used to present the date by the day name, day and month. i.e Fri, 01-01
     * applying the formatter to a date object that is similar to this, Wed May 02 14:14:06 EEST 2012 would result in Wed, 02-05
     * @return A string representing today's date as formatted date.
     */
    public static String getTodayAsStringFormat()
    {
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat formatterForDay = new SimpleDateFormat("E, dd-MM");
        return formatterForDay.format(calendar.getTime());
    }
}
