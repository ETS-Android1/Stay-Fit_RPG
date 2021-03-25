package com.edward_costache.stay_fitrpg.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.edward_costache.stay_fitrpg.ProfileActivity;
import com.edward_costache.stay_fitrpg.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public abstract class Util {

    public final long MILLS_IN_DAY = 86400000;

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

    public static void displayNotImplemented(Context context, View view)
    {
        Snackbar.make(context, view, "Not yet implemented G!", Snackbar.LENGTH_LONG).setAction("Snm G!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }
}
