package com.acmetelecom;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

class DaytimePeakPeriod {
    private List<PeakTime> peakTimeList;

    public void addPeakTime(PeakTime peakTime)
    {
        //Add logic to order list and make sure peak times do not overlap
        peakTimeList.add(peakTime);

    }

    public void remotePeakTime(int pos)
    {
        peakTimeList.remove(pos);
    }

    private DaytimePeakPeriod() {

    }

    public int getTimeInSecondsInCallDuringPeak(int callStart, int callEnd)
    {

        int startPeakTime, endPeakTime;
        for (int i = 0; i < peakTimeList.size(); i++) {

            //Starts between this peakTime
            if (callStart > peakTimeList.get(i).getStartHour() && callStart <  peakTimeList.get(i).getEndHour())
                startPeakTime = i;

            //Starts between this peakTime and the next (out of peak)
            if (callStart > peakTimeList.get(i).getEndHour() && callStart < peakTimeList.get((i+1)%peakTimeList.size()).getStartHour())
                startPeakTime = (i+1)%peakTimeList.size();


            //Ends in this peakTime
            if (callEnd > peakTimeList.get(i).getStartHour() &&
                    callEnd <  peakTimeList.get(i).getEndHour())
                endPeakTime = i;


            //Ends between this peakTime and the next (out of peak)
            if (callEnd > peakTimeList.get(i).getEndHour() && callEnd < peakTimeList.get((i+1)%peakTimeList.size()).getStartHour())
                endPeakTime = (i+1)%peakTimeList.size();

        }
    }

    public static boolean offPeak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour < 7 || hour >= 19;
    }
}
