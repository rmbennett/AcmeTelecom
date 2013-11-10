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

    private Call makeCall(String caller, String callee, int hour, int minute, int duration) {
        CallStart start = new CallStart(caller, callee, getTimestamp(hour, minute));

        minute += duration;
        if (minute >= 60) {
            minute %= 60;
            hour++;
        }

        CallEnd end = new CallEnd(caller, callee, getTimestamp(hour, minute));
        return new Call(start, end);
    }

    private List<Bill> testCalls(List<Call> calls) {
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

            if (DaytimePeakPeriod.offPeak(call.startTime()) && DaytimePeakPeriod.offPeak(call.endTime())) {
                // Off-peak charges
                newCost = expectedCost.get(caller).add(
                        new BigDecimal(call.durationSeconds()).multiply(tariff.offPeakRate()));
            } else {
                newCost = expectedCost.get(caller).add(
                        new BigDecimal(call.durationSeconds()).multiply(tariff.peakRate()));
            }

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
    public void testOffPeakCalls() {
        ArrayList<Call> calls = new ArrayList<Call>();

        calls.add(makeCall("1", "2", 20, 0, 5));
        calls.add(makeCall("2", "1", 20, 10, 5));
        calls.add(makeCall("3", "2", 20, 20, 5));

        testCalls(calls);
    }

    @Test
    public void testPeakCalls() {
        ArrayList<Call> calls = new ArrayList<Call>();

        calls.add(makeCall("1", "2", 16, 0, 5));
        calls.add(makeCall("2", "1", 16, 10, 5));
        calls.add(makeCall("3", "2", 16, 20, 5));

        testCalls(calls);
    }

    @Test
    public void testOverlapCalls() {
        ArrayList<Call> calls = new ArrayList<Call>();

        calls.add(makeCall("1", "2", 19, 58, 5));
        calls.add(makeCall("2", "1", 19, 58, 5));
        calls.add(makeCall("3", "2", 6, 58, 5));

        testCalls(calls);

    }

    @Test
    public void testLineItems() {

        ArrayList<Call> calls = new ArrayList<Call>();

        calls.add(makeCall("2", "3", 18, 35, 5));
        calls.add(makeCall("2", "1", 18, 59, 5));
        calls.add(makeCall("2", "1", 19, 10, 5));

        List<Bill> bills = testCalls(calls);

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
}
