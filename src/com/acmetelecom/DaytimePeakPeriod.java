package com.acmetelecom;

import java.util.Calendar;
import java.util.Date;

class DaytimePeakPeriod {

    //Time in hours
    public static final int peakStartTime = 7;
    public static final int peakEndTime = 19;

    private DaytimePeakPeriod() {

    }

    public static boolean offPeak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour < peakStartTime || hour >= peakEndTime;
    }
}
