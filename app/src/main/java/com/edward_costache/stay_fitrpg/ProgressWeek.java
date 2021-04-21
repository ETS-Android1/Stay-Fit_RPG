package com.edward_costache.stay_fitrpg;

import java.util.HashMap;

/**
 * A class for defining a ProgressWeek, which has a name, and a HashMap of days.
 */
public class ProgressWeek {
    private String weekName;
    private HashMap<String, ProgressDay> days;

    //Empty constructed needed for FirebaseDatabase ValueEventListeners
    ProgressWeek()
    {

    }

    ProgressWeek(String weekName, HashMap<String, ProgressDay> days)
    {
        setWeekName(weekName);
        setDays(days);
    }

    public String getWeekName() {
        return weekName;
    }

    public void setWeekName(String weekName) {
        this.weekName = weekName;
    }

    public HashMap<String, ProgressDay> getDays() {
        return days;
    }

    public void setDays(HashMap<String, ProgressDay> days) {
        this.days = days;
    }
}
