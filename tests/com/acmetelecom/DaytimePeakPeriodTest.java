package com.acmetelecom;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;

/**
 * Unit test for class
 */
public class DaytimePeakPeriodTest {
    @Test
    public void testOffPeak() {
        Date time = new Date();
        time.setHours(21);
        //assertTrue("Hour 21 is off-peak", DaytimePeakPeriod.offPeak(time));
    }

    @Test
    public void testPeak() {
        Date time = new Date();
        time.setHours(15);
        //assertFalse("Hour 15 is peak", DaytimePeakPeriod.offPeak(time));
    }

    @Test
    public void testValidPeakRange() {
        assertTrue("Invalid peak period range", DaytimePeakPeriod.peakStartTime < DaytimePeakPeriod.peakEndTime);
    }
}
