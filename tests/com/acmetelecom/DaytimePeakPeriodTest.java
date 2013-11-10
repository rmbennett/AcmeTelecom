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
        assertTrue(DaytimePeakPeriod.offPeak(time));
    }

    @Test
    public void testPeak() {
        Date time = new Date();
        time.setHours(15);
        assertFalse(DaytimePeakPeriod.offPeak(time));
    }
}
