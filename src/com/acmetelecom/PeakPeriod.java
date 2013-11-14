package com.acmetelecom;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 11/11/13
 * Time: 23:55
 */
public class PeakPeriod {
    private int startHour;
    private int endHour;

    public int getStartHour()
    {
        return startHour;
    }

    public int getEndHour()
    {
        return endHour;
    }

    public void setStartHour(int startHour)
    {
        this.startHour = startHour;
    }

    public void setEndHour(int endHour)
    {
        this.endHour = endHour;
    }

    public PeakPeriod(int startHour, int endHour)
    {
        setStartHour(startHour);
        setEndHour(endHour);
    }
}
