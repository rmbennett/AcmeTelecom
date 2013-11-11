package com.acmetelecom;

import com.acmetelecom.customer.CentralTariffDatabase;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.Customer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BillingSystemTest {
    BillingSystem billingSystem;

    @Before
    public void setUp() {
        billingSystem = new BillingSystem(MockCustomerDatabase.getInstance(), CentralTariffDatabase.getInstance());
    }

    private long getTimestamp(int hour, int minute) {
        Date date = new Date();
        date.setDate(1);
        date.setMonth(3);
        date.setYear(2013);
        date.setHours(hour);
        date.setMinutes(minute);
        return date.getTime();

    }

    private Call makeCall(String caller, String callee, int startHour, int startMinute, int endHour, int endMinute) {
        CallStart start = new CallStart(caller, callee, getTimestamp(startHour, startMinute));
        CallEnd end = new CallEnd(caller, callee, getTimestamp(endHour, endMinute));
        return new Call(start, end);
    }

    private List<Bill> testCalls(List<Call> calls, boolean nonOverlap) throws NoSuchFieldException {
        // Key of the hash map is the phone number
        HashMap<String, BigDecimal> expectedCost = new HashMap<String, BigDecimal>();
        MockCustomerDatabase customerDatabase = MockCustomerDatabase.getInstance();

        // "Make" the calls
        for (Call call : calls) {
            String caller = call.caller();
            Customer customer = customerDatabase.getCustomer(caller);
            Tariff tariff = Tariff.valueOf(customer.getPricePlan());
            if (expectedCost.get(caller) == null) {
                expectedCost.put(caller, new BigDecimal(0));
            }

            BigDecimal newCost;
            int peakDuration, offPeakDuration;
            BigDecimal peakCost, offPeakCost;

            if (DaytimePeakPeriod.offPeak(call.startTime()) && DaytimePeakPeriod.offPeak(call.endTime()) &&
                    call.durationSeconds() < 12 * 60 * 60) {
                // Off-peak charges
                peakDuration = 0;
                offPeakDuration = call.durationSeconds();
            } else if (DaytimePeakPeriod.offPeak(call.startTime()) && DaytimePeakPeriod.offPeak(call.endTime()) &&
                    call.durationSeconds() >= 12 * 60 * 60 && nonOverlap ) {
                peakDuration = (int) (((getTimestamp(DaytimePeakPeriod.peakEndTime, 00) -
                        getTimestamp(DaytimePeakPeriod.peakStartTime, 00)) / 1000));
                offPeakDuration = call.durationSeconds() - peakDuration;
            } else if (!DaytimePeakPeriod.offPeak(call.startTime()) && DaytimePeakPeriod.offPeak(call.endTime()) &&
                    nonOverlap) {
                long peakEndTime = getTimestamp(DaytimePeakPeriod.peakEndTime, 00);
                peakDuration = (int) (((peakEndTime - call.startTime().getTime()) / 1000));
                offPeakDuration = call.durationSeconds() - peakDuration;
            } else if (DaytimePeakPeriod.offPeak(call.startTime()) && !DaytimePeakPeriod.offPeak(call.endTime()) &&
                    nonOverlap) {
                long peakStartTime = getTimestamp(DaytimePeakPeriod.peakStartTime, 00);
                peakDuration = (int) (((call.endTime().getTime() - peakStartTime) / 1000));
                offPeakDuration = call.durationSeconds() - peakDuration;
            } else {
                peakDuration = call.durationSeconds();
                offPeakDuration = 0;
            }

            peakCost = new BigDecimal(peakDuration).multiply(tariff.peakRate());
            offPeakCost = new BigDecimal(offPeakDuration).multiply(tariff.offPeakRate());
            newCost = expectedCost.get(caller).add(peakCost.add(offPeakCost));

            newCost = newCost.setScale(0, RoundingMode.HALF_UP);
            expectedCost.put(caller, newCost);

            billingSystem.callInitiated(caller, call.callee(), call.startTime().getTime());
            billingSystem.callCompleted(caller, call.callee(), call.endTime().getTime());
        }

        // Assert that bill costs matches what we expect
        List<Bill> bills = billingSystem.createCustomerBills();
        for (Bill bill : bills) {
            Customer customer = bill.getCustomer();
            BigDecimal expected = expectedCost.get(customer.getPhoneNumber());
            if (expected == null) {
                expected = new BigDecimal(0);
            }
            assertEquals(expected, bill.getTotalBill());
        }

        return bills;
    }

    @Test
    public void testOffPeakCalls() throws NoSuchFieldException {
        ArrayList<Call> calls = new ArrayList<Call>();

        calls.add(makeCall("1", "2", 20, 00, 20, 05));
        calls.add(makeCall("2", "1", 20, 10, 10, 15));
        calls.add(makeCall("3", "2", 20, 20, 10, 25));

        testCalls(calls, false);
    }

    @Test
    public void testPeakCalls() throws NoSuchFieldException {
        ArrayList<Call> calls = new ArrayList<Call>();

        calls.add(makeCall("1", "2", 16, 00, 16, 5));
        calls.add(makeCall("2", "1", 16, 10, 16, 15));
        calls.add(makeCall("3", "2", 16, 20, 16, 25));

        testCalls(calls, false);
    }

    @Test
    public void testOverlapCalls() throws NoSuchFieldException {
        ArrayList<Call> calls = new ArrayList<Call>();

        calls.add(makeCall("1", "2", 19, 58, 20, 03));
        calls.add(makeCall("2", "1", 19, 58, 20, 03));
        calls.add(makeCall("3", "2", 6, 58, 7, 03));

        testCalls(calls, false);

    }

    @Test
    public void testLineItems() throws NoSuchFieldException {

        ArrayList<Call> calls = new ArrayList<Call>();

        calls.add(makeCall("2", "3", 18, 35, 18, 40));
        calls.add(makeCall("2", "1", 18, 59, 19, 04));
        calls.add(makeCall("2", "1", 19, 10, 19, 15));

        List<Bill> bills = testCalls(calls, false);

        for (Bill bill : bills) {
            if (bill.getCustomer().getPricePlan().equals("Business")) {
                List<BillingSystem.LineItem> items = bill.getCalls();
                assertEquals(calls.size(), items.size());

                for (int i = 0; i < calls.size(); i++) {
                    Call call = calls.get(i);
                    BillingSystem.LineItem item = items.get(i);

                    assertEquals(item.callee(), call.callee());
                    assertEquals(item.caller(), call.caller());
                    assertEquals("5:00", item.durationMinutes());

                }
                break;
            }
        }

    }

    @Test
    public void testNonOverlapCalls() throws NoSuchFieldException {
        ArrayList<Call> calls = new ArrayList<Call>();

        calls.add(makeCall("1", "2", 6, 00, 20, 00));
        calls.add(makeCall("2", "1", 18, 58, 19, 03));
        calls.add(makeCall("3", "2", 6, 58, 7, 03));
        calls.add(makeCall("2", "3", 8, 00, 11, 00));
        calls.add(makeCall("1", "3", 6, 00, 6, 10));
        calls.add(makeCall("3", "1", 20, 00, 22, 00));

        testCalls(calls, true);
    }
}
