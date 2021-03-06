package com.acmetelecom;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

class PeakPeriodCalculator {
    private List<PeakPeriod> peakPeriodList;

    public void addPeakTime(PeakPeriod peakTime)
    {
        //Should probably add logic to order list and make sure peak times do not overlap
        peakPeriodList.add(peakTime);
    }

    public void removePeakTime(int pos)
    {
        peakPeriodList.remove(pos);
    }

    public PeakPeriodCalculator() {
        this(new ArrayList<PeakPeriod>());
    }

    public PeakPeriodCalculator(List<PeakPeriod> peakPeriods)  {
        peakPeriodList = peakPeriods;
    }

    public int getTimeInSecondsInCallDuringPeak(Call call)
    {
        int fullBillingsDays = (int) Math.floor(call.durationSeconds() / (24 * 60 * 60));
        int startPeakIndex = 0;
        int endPeakIndex = 0;

        int fullPeakTimePerDay = 0;
        for (int i = 0; i < peakPeriodList.size(); i++)
            fullPeakTimePerDay += (peakPeriodList.get(i).getEndHour() - peakPeriodList.get(i).getStartHour()) * 3600000;

        if (call.durationSeconds() % (24 * 60 * 60) == 0)
            return (fullPeakTimePerDay * fullBillingsDays) / 1000;

        //Date.getHours is deprecated from java.date package. Accepted method is to use the (verbose) Calender package
        //Find the start time for the last day
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(new Date(call.startTime().getTime() + (fullBillingsDays * 12 * 60 * 60)));

        int startTime = calStart.get(Calendar.HOUR_OF_DAY);
        boolean startOutOfPeakTime = true;

        //Find the first peak period the call could hit and set startOutOfPeakTime to whether it will hit it or not
        for (int i = 0; i < peakPeriodList.size(); i++) {

            //Starts between this peakTime
            if (startTime >= peakPeriodList.get(i).getStartHour() &&
                    startTime <  peakPeriodList.get(i).getEndHour())
            {
                startPeakIndex= i;
                startOutOfPeakTime = false;
                break;
            }
            //Starts after this peakTime. Point to the next peak period, as in the final iteration, we will know what the
            //next period index is
            if (startTime >= peakPeriodList.get(i).getEndHour() /*&&
                    startTime < peakPeriodList.get((i+1)% peakPeriodList.size()).getStartHour()*/)
            {
                startPeakIndex = Math.max((i+1),0);
            }
        }

        //Find the last peak period the call could hit and set endOutOfPeakTime to whether it will hit it or not
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(call.endTime());
        int endTime = calEnd.get(Calendar.HOUR_OF_DAY);
        boolean endOutOfPeakTime = true;

        //Find the last peak period the call could hit and set endOutOfPeakTime to whether it will hit it or not
        for (int i = 0; i < peakPeriodList.size(); i++) {
            //Ends in this peakTime
            if (endTime >= peakPeriodList.get(i).getStartHour() &&
                    endTime <  peakPeriodList.get(i).getEndHour())
            {
                endPeakIndex = i;
                endOutOfPeakTime = false;
                break;
            }

            //Ends after this peakTime. Point to the next peak period, as in the final iteration, we will know what the
            //next period index is
            if (endTime >= peakPeriodList.get(i).getEndHour())
                endPeakIndex = Math.min((i+1),peakPeriodList.size());
        }

        //At this point in time, we have the start and end indexes of either what peak period to start/end in and/or the
        //peak period index immediately following

        //Call was completely outside peak period (between 2 peak periods)
        if (endOutOfPeakTime && startOutOfPeakTime && startPeakIndex == endPeakIndex && (endTime > startTime))
            return (fullBillingsDays * fullPeakTimePerDay)/1000;

        //Check if we rolled over a day
        int totalPeriodsToTransverse;
        if (startTime > endTime)
            totalPeriodsToTransverse = peakPeriodList.size() - startPeakIndex + endPeakIndex;
        else
            totalPeriodsToTransverse = endPeakIndex - startPeakIndex;

        int peakPeriodForLastDay = 0;
        for ( int i = startPeakIndex; i < (startPeakIndex + ((!endOutOfPeakTime) ? totalPeriodsToTransverse + 1: totalPeriodsToTransverse)); i++ )
            peakPeriodForLastDay += (peakPeriodList.get(i%peakPeriodList.size()).getEndHour() - peakPeriodList.get(i%peakPeriodList.size()).getStartHour())* 3600000;

        //Previously we summed up all peak periods entered or overlapped.
        // If we start of finish within a peak time we now make corrections for the amount of time spent in that peak period
        if (!startOutOfPeakTime)
            peakPeriodForLastDay -= (calStart.get(Calendar.HOUR_OF_DAY) * 3600000 + calStart.get(Calendar.MINUTE) * 60000 + calStart.get(Calendar.SECOND) * 1000 - peakPeriodList.get(startPeakIndex).getStartHour() * 3600000);

        if (!endOutOfPeakTime)
            peakPeriodForLastDay -= (peakPeriodList.get(endPeakIndex).getEndHour()*3600000 - (calEnd.get(Calendar.HOUR_OF_DAY)*3600000 + calEnd.get(Calendar.MINUTE) * 60000 + calEnd.get(Calendar.SECOND) * 1000));

        return (fullBillingsDays * fullPeakTimePerDay + peakPeriodForLastDay)/1000;
    }


}
