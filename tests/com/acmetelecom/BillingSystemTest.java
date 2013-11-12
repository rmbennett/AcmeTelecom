package com.acmetelecom;

import com.acmetelecom.customer.CentralTariffDatabase;
import com.acmetelecom.customer.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class BillingSystemTest {
    BillingSystem billingSystem;
    String name;
    ArrayList<Call> calls;
    HashMap<String, BigDecimal> expectedCost;
    List<Bill> bills;

    /** Helper Methods **/

    @SuppressWarnings("deprecation")
    private static long getTimestamp(int hour, int minute) {
        Date date = new Date();
        date.setDate(1);
        date.setMonth(3);
        date.setYear(2013);
        date.setHours(hour);
        date.setMinutes(minute);
        return date.getTime();

    }

    private static Call makeCall(String caller, String callee, int startHour, int startMinute, int endHour, int endMinute) {
        CallStart start = new CallStart(caller, callee, getTimestamp(startHour, startMinute));
        CallEnd end = new CallEnd(caller, callee, getTimestamp(endHour, endMinute));
        return new Call(start, end);
    }

    /*
        Each set of data in the test data consists of a 3-tuple:
            - 1: Name
            - 2: List of calls
            - 3: Hash map of expected cost

     */

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {

        // Off-peak test data
        ArrayList<Call> offPeakCalls = new ArrayList<Call>();

        offPeakCalls.add(makeCall("1", "2", 20, 00, 20, 05));
        offPeakCalls.add(makeCall("2", "1", 20, 10, 10, 15));
        offPeakCalls.add(makeCall("3", "2", 20, 20, 10, 25));

        HashMap<String, BigDecimal> offPeakExpectedCost = new HashMap<String, BigDecimal>();
        offPeakExpectedCost.put("1", new BigDecimal(60));
        offPeakExpectedCost.put("2", new BigDecimal(0));
        offPeakExpectedCost.put("3", new BigDecimal(0));

        // Peal test data
        ArrayList<Call> peakCalls = new ArrayList<Call>();

        peakCalls.add(makeCall("1", "2", 16, 00, 16, 5));
        peakCalls.add(makeCall("2", "1", 16, 10, 16, 15));
        peakCalls.add(makeCall("3", "2", 16, 20, 16, 25));

        HashMap<String, BigDecimal> peakExpectedCost = new HashMap<String, BigDecimal>();
        peakExpectedCost.put("1", new BigDecimal(150));
        peakExpectedCost.put("2", new BigDecimal(90));
        peakExpectedCost.put("3", new BigDecimal(240));

        return Arrays.asList(new Object[][] {
                { "Off-peak", offPeakCalls, offPeakExpectedCost },
                { "Peak", peakCalls, peakExpectedCost }
        });
    }

    public BillingSystemTest(String name, ArrayList<Call> calls, HashMap<String, BigDecimal> expectedCost){
        billingSystem = new BillingSystem(MockCustomerDatabase.getInstance(), CentralTariffDatabase.getInstance());
        this.name = name;
        this.calls = calls;
        this.expectedCost = expectedCost;

        // "Make" the calls
        for (Call call : calls) {
            String caller = call.caller();
            String callee = call.callee();

            billingSystem.callInitiated(caller, callee, call.startTime().getTime());
            billingSystem.callCompleted(caller, callee, call.endTime().getTime());
        }

        // Get bills
        bills = billingSystem.createCustomerBills();
    }

    @Test
    public void testCalls() throws NoSuchFieldException {
        for (Bill bill : bills) {
            Customer customer = bill.getCustomer();
            BigDecimal expected = expectedCost.get(customer.getPhoneNumber());
            assertEquals(String.format("[%s] Customer %s cost", name, customer.getPhoneNumber()),
                    expected, bill.getTotalBill());
        }
    }


    @Test
    public void testLineItems() throws NoSuchFieldException {
        for (Bill bill : bills) {
            String phoneNumber = bill.getCustomer().getPhoneNumber();
            // Extract list of expected calls
            ArrayList<Call> expectedCalls = new ArrayList<Call>();
            for (Call call : calls) {
                if (call.caller().equals(phoneNumber)) {
                    expectedCalls.add(call);
                }
            }

            List<BillingSystem.LineItem> actualCalls = bill.getCalls();

            assertEquals(String.format("[%s] Customer %s number of calls", name, phoneNumber),
                    expectedCalls.size(), actualCalls.size());

            for (int i = 0; i < expectedCalls.size(); i++) {
                Call expected = expectedCalls.get(i);
                BillingSystem.LineItem actual = actualCalls.get(i);

                assertEquals(expected.callee(), actual.callee());
                assertEquals(expected.caller(), actual.caller());
            }
        }

    }

//    @Test
//    public void testOverlapCalls() throws NoSuchFieldException {
//        ArrayList<Call> calls = new ArrayList<Call>();
//
//        calls.add(makeCall("1", "2", 19, 58, 20, 03));
//        calls.add(makeCall("2", "1", 19, 58, 20, 03));
//        calls.add(makeCall("3", "2", 6, 58, 7, 03));
//
//        testCalls(calls, false);
//
//    }

//    @Test
//    public void testNonOverlapCalls() throws NoSuchFieldException {
//        ArrayList<Call> calls = new ArrayList<Call>();
//
//        calls.add(makeCall("1", "2", 6, 00, 20, 00));
//        calls.add(makeCall("2", "1", 18, 58, 19, 03));
//        calls.add(makeCall("3", "2", 6, 58, 7, 03));
//        calls.add(makeCall("2", "3", 8, 00, 11, 00));
//        calls.add(makeCall("1", "3", 6, 00, 6, 10));
//        calls.add(makeCall("3", "1", 20, 00, 22, 00));
//
//        testCalls(calls, true);
//    }
}
