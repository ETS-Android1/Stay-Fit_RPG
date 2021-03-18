package com.edward_costache.stay_fitrpg.util;

import android.content.Context;
import android.view.View;

import com.edward_costache.stay_fitrpg.ProfileActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public abstract class Util {

    public final long MILLS_IN_DAY = 86400000;

    public static long getToday() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

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
        Snackbar.make(context, view, "Not yet implemented G!", Snackbar.LENGTH_INDEFINITE).setAction("Snm G!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }
}
