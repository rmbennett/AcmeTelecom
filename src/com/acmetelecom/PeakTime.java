package com.acmetelecom;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 11/11/13
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class PeakTime {
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

    PeakTime(int startHour, int endHour)
    {
        setStartHour(startHour);
        setEndHour(endHour);
    }
}
