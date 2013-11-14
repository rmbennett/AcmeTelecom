package com.acmetelecom;

import com.acmetelecom.customer.CentralTariffDatabase;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class BillingSystemTest {
    BillingSystem billingSystem;
    String name;
    CallBuilder calls;
    HashMap<String, BigDecimal> expectedCost;
    List<Bill> bills;

    // Assert that the tariffs haven't changed
    @BeforeClass
    public static void assertExpectedTariffs() {
        HashMap<String, BigDecimal[]> expectedTariffs = new HashMap<String, BigDecimal[]>();
        BigDecimal[] standard = {
                new BigDecimal("0.200000000000000011102230246251565404236316680908203125"),
                new BigDecimal("0.5")
        };
        BigDecimal[] business = {
                new BigDecimal("0.299999999999999988897769753748434595763683319091796875"),
                new BigDecimal("0.299999999999999988897769753748434595763683319091796875")
        };
        BigDecimal[] leisure = {
                new BigDecimal("0.1000000000000000055511151231257827021181583404541015625"),
                new BigDecimal("0.8000000000000000444089209850062616169452667236328125")
        };

        expectedTariffs.put("Standard", standard);
        expectedTariffs.put("Business", business);
        expectedTariffs.put("Leisure", leisure);

        for (Tariff tariff : Tariff.values()) {
            BigDecimal[] expected = expectedTariffs.get(tariff.name());
            if (!expected[0].equals(tariff.offPeakRate()) || !expected[1].equals(tariff.peakRate())) {
                fail("Tariff for " + tariff.name() + " plan has changed -- tests needs update");
            }
        }

    }

    @Parameterized.Parameters
    public static Collection<Object[]> testData() throws ParseException {

        List<PeakPeriod> singlePeakPeriods = new ArrayList<PeakPeriod>();
        singlePeakPeriods.add(new PeakPeriod(7, 19));

        List<PeakPeriod> twoPeakPeriods = new ArrayList<PeakPeriod>();
        twoPeakPeriods.add(new PeakPeriod(6, 10));
        twoPeakPeriods.add(new PeakPeriod(20, 21));

        List<PeakPeriod> threePeakPeriods = new ArrayList<PeakPeriod>();
        threePeakPeriods.add(new PeakPeriod(6, 10));
        threePeakPeriods.add(new PeakPeriod(13, 14));
        threePeakPeriods.add(new PeakPeriod(20, 21));

        // Off-peak test data
//        ArrayList<Call> offPeakCalls = new ArrayList<Call>();
//
//        offPeakCalls.add(makeCall("1", "2", 1, 20, 00, 1, 20, 05));
//        offPeakCalls.add(makeCall("2", "1", 1, 20, 10, 1, 20, 15));
//        offPeakCalls.add(makeCall("3", "2", 1, 20, 20, 1, 20, 25));
//
//        HashMap<String, BigDecimal> offPeakExpectedCost = new HashMap<String, BigDecimal>();
//        offPeakExpectedCost.put("1", new BigDecimal(60));
//        offPeakExpectedCost.put("2", new BigDecimal(0));
//        offPeakExpectedCost.put("3", new BigDecimal(0));
//
//        // Peak test data
//        ArrayList<Call> peakCalls = new ArrayList<Call>();
//
//        peakCalls.add(makeCall("1", "2", 1, 16, 00, 1, 16, 5));
//        peakCalls.add(makeCall("2", "1", 1, 16, 10, 1, 16, 15));
//        peakCalls.add(makeCall("3", "2", 1, 16, 20, 1, 16, 25));
//
//        HashMap<String, BigDecimal> peakExpectedCost = new HashMap<String, BigDecimal>();
//        peakExpectedCost.put("1", new BigDecimal(150));
//        peakExpectedCost.put("2", new BigDecimal(90));
//        peakExpectedCost.put("3", new BigDecimal(240));

        // Hybrid test data
        CallBuilder hybridCalls = new CallBuilder();

        hybridCalls.setCaller("1").setCallee("2")
                .setStartDate("1-11-2013 06:00:00").setEndDate("1-11-2013 20:00:00")
                .setExpectedPeakTime(43200).add();

        hybridCalls.setCaller("2").setCallee("1")
                .setStartDate("1-11-2013 18:58:00").setEndDate("1-11-2013 19:03:00")
                .setExpectedPeakTime(120).add();

        hybridCalls.setCaller("3").setCallee("2")
                .setStartDate("1-11-2013 06:58:00").setEndDate("1-11-2013 07:03:00")
                .setExpectedPeakTime(180).add();

        HashMap<String, BigDecimal> hybridExpectedCost = new HashMap<String, BigDecimal>();
        hybridExpectedCost.put("1", new BigDecimal(23040));
        hybridExpectedCost.put("2", new BigDecimal(90));
        hybridExpectedCost.put("3", new BigDecimal(156));

        return Arrays.asList(new Object[][] {
//                { "Off-peak", offPeakCalls, offPeakExpectedCost, singlePeakPeriods },
//                { "Peak", peakCalls, peakExpectedCost, singlePeakPeriods },
                { "Hybrid", hybridCalls, hybridExpectedCost, singlePeakPeriods }
        });
    }

    public BillingSystemTest(String name, CallBuilder calls, HashMap<String, BigDecimal> expectedCost,
                             List<PeakPeriod> peakPeriods){
        billingSystem = new BillingSystem(MockCustomerDatabase.getInstance(),
                CentralTariffDatabase.getInstance(), peakPeriods);
        this.name = name;
        this.calls = calls;
        this.expectedCost = expectedCost;

        // "Make" the calls
        for (Call call : calls.calls()) {
            String caller = call.caller();
            String callee = call.callee();

            billingSystem.callInitiated(caller, callee, call.startTime().getTime());
            billingSystem.callCompleted(caller, callee, call.endTime().getTime());
        }

        // Get bills
        bills = billingSystem.createCustomerBills();
    }

    @Test
    public void testTotalBillCost() throws NoSuchFieldException {
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
            // Extract list of expected calls and their peak durations
            ArrayList<Map.Entry<Call, Integer>> expectedCalls = new ArrayList<Map.Entry<Call, Integer>>();
            for (Map.Entry<Call, Integer> pair : calls.callsExpectedPeakTimes().entrySet()) {
                if (pair.getKey().caller().equals(phoneNumber)) {
                    expectedCalls.add(pair);
                }
            }

            List<BillingSystem.LineItem> actualCalls = bill.getCalls();

            assertEquals(String.format("[%s] Customer %s number of calls", name, phoneNumber),
                    expectedCalls.size(), actualCalls.size());

            for (int i = 0; i < expectedCalls.size(); i++) {
                Call expected = expectedCalls.get(i).getKey();
                BillingSystem.LineItem actual = actualCalls.get(i);

                assertEquals(String.format("[%s] Customer %s callee", name, phoneNumber),
                        expected.callee(), actual.callee());
                assertEquals(String.format("[%s] Customer %s caller", name, phoneNumber),
                        expected.caller(), actual.caller());
                assertEquals(String.format("[%s] Customer %s peak-time", name, phoneNumber),
                        expectedCalls.get(i).getValue().intValue(), actual.getPeakCallTime());
            }
        }
    }

    /**
     * Helper nested classes
     */

    /**
     * Call Builder class to make it easier to make calls
     */
    private static class CallBuilder {
        public static DateFormat formatter = new SimpleDateFormat("d-M-y H:m:s");

        // "Holder" members
        private String caller, callee, startDate, endDate;
        private int expectedPeakTime;

        private LinkedHashMap<Call, Integer> calls;

        public CallBuilder() {
            caller = "";
            callee = "";
            startDate = "";
            endDate = "";

            calls = new LinkedHashMap<Call, Integer>();
        }

        private long getTimestamp(String date) throws ParseException {
            return formatter.parse(date).getTime();
        }

        public CallBuilder setCaller(String caller) {
            this.caller = caller;
            return this;
        }

        public CallBuilder setCallee(String callee) {
            this.callee = callee;
            return this;
        }

        public CallBuilder setStartDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public CallBuilder setEndDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public CallBuilder setExpectedPeakTime(int expectedPeakTime) {
            this.expectedPeakTime = expectedPeakTime;
            return this;
        }

        public CallBuilder add() throws ParseException {
            CallStart start = new CallStart(caller, callee, getTimestamp(startDate));
            CallEnd end = new CallEnd(caller, callee, getTimestamp(endDate));
            Call call = new Call(start, end);

            calls.put(call, expectedPeakTime);
            return this;
        }

        public Collection<Integer> expectedPeakTimes() {
            return calls.values();
        }

        public Collection<Call> calls() {
            return calls.keySet();
        }

        public LinkedHashMap<Call, Integer> callsExpectedPeakTimes() {
            return calls;
        }
    }
}
