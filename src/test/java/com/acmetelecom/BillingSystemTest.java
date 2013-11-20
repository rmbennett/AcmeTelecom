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

        List<PeakPeriod> multiPeakPeriods = new ArrayList<PeakPeriod>();
        multiPeakPeriods.add(new PeakPeriod(6, 10));
        multiPeakPeriods.add(new PeakPeriod(20, 21));

        List<PeakPeriod> continuousPeakPeriods = new ArrayList<PeakPeriod>();
        continuousPeakPeriods.add(new PeakPeriod(0, 8));
        continuousPeakPeriods.add(new PeakPeriod(20, 24));

        // SinglePeakPeriodSingleDayNonOverlapping test data
        CallBuilder singlePeakSingleDayNonOverlappingCalls = new CallBuilder();

        singlePeakSingleDayNonOverlappingCalls.setCaller("1").setCallee("2")
                .setStartDate("1-11-2013 06:00:00").setEndDate("1-11-2013 06:38:00")
                .setExpectedPeakTime(0).add();

        singlePeakSingleDayNonOverlappingCalls.setCaller("2").setCallee("1")
                .setStartDate("1-11-2013 12:00:00").setEndDate("1-11-2013 15:00:00")
                .setExpectedPeakTime(10800).add();

        singlePeakSingleDayNonOverlappingCalls.setCaller("3").setCallee("2")
                .setStartDate("1-11-2013 20:00:00").setEndDate("1-11-2013 22:00:00")
                .setExpectedPeakTime(0).add();

        HashMap<String, BigDecimal> singlePeakSingleDayNonOverlappingCallsExpectedCost = new HashMap<String, BigDecimal>();
        singlePeakSingleDayNonOverlappingCallsExpectedCost.put("1", new BigDecimal(456));
        singlePeakSingleDayNonOverlappingCallsExpectedCost.put("2", new BigDecimal(3240));
        singlePeakSingleDayNonOverlappingCallsExpectedCost.put("3", new BigDecimal(720));

        // singlePeakSingleDayOverlapping test data
        CallBuilder singlePeakSingleDayOverlappingCalls = new CallBuilder();

        singlePeakSingleDayOverlappingCalls.setCaller("1").setCallee("2")
                .setStartDate("1-11-2013 06:00:00").setEndDate("1-11-2013 20:00:00")
                .setExpectedPeakTime(43200).add();

        singlePeakSingleDayOverlappingCalls.setCaller("2").setCallee("1")
                .setStartDate("1-11-2013 18:58:00").setEndDate("1-11-2013 19:03:00")
                .setExpectedPeakTime(120).add();

        singlePeakSingleDayOverlappingCalls.setCaller("3").setCallee("2")
                .setStartDate("1-11-2013 06:58:00").setEndDate("1-11-2013 07:03:00")
                .setExpectedPeakTime(180).add();

        HashMap<String, BigDecimal> singlePeakSingleDayOverlappingCallsExpectedCost = new HashMap<String, BigDecimal>();
        singlePeakSingleDayOverlappingCallsExpectedCost.put("1", new BigDecimal(23040));
        singlePeakSingleDayOverlappingCallsExpectedCost.put("2", new BigDecimal(90));
        singlePeakSingleDayOverlappingCallsExpectedCost.put("3", new BigDecimal(156));

        // SinglePeakPeriodMultiDayNonOverlapping test data
        CallBuilder singlePeakMultiDayNonOverlappingCalls = new CallBuilder();

        singlePeakMultiDayNonOverlappingCalls.setCaller("1").setCallee("2")
                .setStartDate("1-11-2013 20:00:00").setEndDate("2-11-2013 05:12:00")
                .setExpectedPeakTime(0).add();

        singlePeakMultiDayNonOverlappingCalls.setCaller("2").setCallee("1")
                .setStartDate("1-11-2013 12:00:00").setEndDate("1-11-2013 15:00:00")
                .setExpectedPeakTime(10800).add();

        singlePeakMultiDayNonOverlappingCalls.setCaller("3").setCallee("2")
                .setStartDate("1-11-2013 23:00:00").setEndDate("2-11-2013 1:00:00")
                .setExpectedPeakTime(0).add();

        HashMap<String, BigDecimal> singlePeakMultiDayNonOverlappingCallsExpectedCost = new HashMap<String, BigDecimal>();
        singlePeakMultiDayNonOverlappingCallsExpectedCost.put("1", new BigDecimal(6624));
        singlePeakMultiDayNonOverlappingCallsExpectedCost.put("2", new BigDecimal(3240));
        singlePeakMultiDayNonOverlappingCallsExpectedCost.put("3", new BigDecimal(720));

        // singlePeakMultiDayOverlapping test data
        CallBuilder singlePeakMultiDayOverlappingCalls = new CallBuilder();

        singlePeakMultiDayOverlappingCalls.setCaller("1").setCallee("2")
                .setStartDate("1-11-2013 06:00:00").setEndDate("3-11-2013 20:00:00")
                .setExpectedPeakTime(129600).add();

        singlePeakMultiDayOverlappingCalls.setCaller("2").setCallee("1")
                .setStartDate("1-11-2013 12:00:00").setEndDate("2-11-2013 12:00:00")
                .setExpectedPeakTime(43200).add();

        singlePeakMultiDayOverlappingCalls.setCaller("3").setCallee("2")
                .setStartDate("1-11-2013 15:00:00").setEndDate("2-11-2013 06:00:00")
                .setExpectedPeakTime(14400).add();

        HashMap<String, BigDecimal> singlePeakMultiDayOverlappingCallsExpectedCost = new HashMap<String, BigDecimal>();
        singlePeakMultiDayOverlappingCallsExpectedCost.put("1", new BigDecimal(83520));
        singlePeakMultiDayOverlappingCallsExpectedCost.put("2", new BigDecimal(25920));
        singlePeakMultiDayOverlappingCallsExpectedCost.put("3", new BigDecimal(15480));



        // MultiPeakPeriodSingleDayNonOverlapping test data
        CallBuilder multiPeakSingleDayNonOverlappingCalls = new CallBuilder();

        multiPeakSingleDayNonOverlappingCalls.setCaller("1").setCallee("2")
                .setStartDate("1-11-2013 13:00:00").setEndDate("1-11-2013 19:00:00")
                .setExpectedPeakTime(0).add();

        multiPeakSingleDayNonOverlappingCalls.setCaller("2").setCallee("1")
                .setStartDate("1-11-2013 7:00:00").setEndDate("1-11-2013 9:30:00")
                .setExpectedPeakTime(9000).add();

        multiPeakSingleDayNonOverlappingCalls.setCaller("3").setCallee("2")
                .setStartDate("1-11-2013 20:15:00").setEndDate("1-11-2013 20:45:00")
                .setExpectedPeakTime(1800).add();

        HashMap<String, BigDecimal> multiPeakSingleDayNonOverlappingCallsExpectedCost = new HashMap<String, BigDecimal>();
        multiPeakSingleDayNonOverlappingCallsExpectedCost.put("1", new BigDecimal(4320));
        multiPeakSingleDayNonOverlappingCallsExpectedCost.put("2", new BigDecimal(2700));
        multiPeakSingleDayNonOverlappingCallsExpectedCost.put("3", new BigDecimal(1440));

        // multiPeakSingleDayOverlapping test data
        CallBuilder multiPeakSingleDayOverlappingCalls = new CallBuilder();

        multiPeakSingleDayOverlappingCalls.setCaller("1").setCallee("2")
                .setStartDate("1-11-2013 05:00:00").setEndDate("1-11-2013 22:00:00")
                .setExpectedPeakTime(18000).add();

        multiPeakSingleDayOverlappingCalls.setCaller("2").setCallee("1")
                .setStartDate("1-11-2013 12:00:00").setEndDate("1-11-2013 20:30:00")
                .setExpectedPeakTime(1800).add();

        multiPeakSingleDayOverlappingCalls.setCaller("3").setCallee("2")
                .setStartDate("1-11-2013 08:00:00").setEndDate("1-11-2013 20:10:00")
                .setExpectedPeakTime(7800).add();

        HashMap<String, BigDecimal> multiPeakSingleDayOverlappingCallsExpectedCost = new HashMap<String, BigDecimal>();
        multiPeakSingleDayOverlappingCallsExpectedCost.put("1", new BigDecimal(17640));
        multiPeakSingleDayOverlappingCallsExpectedCost.put("2", new BigDecimal(9180));
        multiPeakSingleDayOverlappingCallsExpectedCost.put("3", new BigDecimal(9840));

        // multiPeakPeriodMultiDayNonOverlapping test data
        CallBuilder multiPeakMultiDayNonOverlappingCalls = new CallBuilder();

        multiPeakMultiDayNonOverlappingCalls.setCaller("1").setCallee("2")
                .setStartDate("1-11-2013 13:00:00").setEndDate("1-11-2013 19:00:00")
                .setExpectedPeakTime(0).add();

        multiPeakMultiDayNonOverlappingCalls.setCaller("2").setCallee("1")
                .setStartDate("1-11-2013 22:00:00").setEndDate("2-11-2013 5:00:00")
                .setExpectedPeakTime(0).add();

        multiPeakMultiDayNonOverlappingCalls.setCaller("3").setCallee("2")
                .setStartDate("1-11-2013 7:00:00").setEndDate("1-11-2013 9:30:00")
                .setExpectedPeakTime(9000).add();

        HashMap<String, BigDecimal> multiPeakMultiDayNonOverlappingCallsExpectedCost = new HashMap<String, BigDecimal>();
        multiPeakMultiDayNonOverlappingCallsExpectedCost.put("1", new BigDecimal(4320));
        multiPeakMultiDayNonOverlappingCallsExpectedCost.put("2", new BigDecimal(7560));
        multiPeakMultiDayNonOverlappingCallsExpectedCost.put("3", new BigDecimal(7200));

        // multiPeakMultiDayOverlapping test data
        CallBuilder multiPeakMultiDayOverlappingCalls = new CallBuilder();

        multiPeakMultiDayOverlappingCalls.setCaller("1").setCallee("2")
                .setStartDate("1-11-2013 05:00:00").setEndDate("2-11-2013 22:00:00")
                .setExpectedPeakTime(36000).add();

        multiPeakMultiDayOverlappingCalls.setCaller("2").setCallee("1")
                .setStartDate("1-11-2013 12:00:00").setEndDate("2-11-2013 12:00:00")
                .setExpectedPeakTime(18000).add();

        multiPeakMultiDayOverlappingCalls.setCaller("3").setCallee("2")
                .setStartDate("1-11-2013 20:30:00").setEndDate("2-11-2013 08:00:00")
                .setExpectedPeakTime(9000).add();

        HashMap<String, BigDecimal> multiPeakMultiDayOverlappingCallsExpectedCost = new HashMap<String, BigDecimal>();
        multiPeakMultiDayOverlappingCallsExpectedCost.put("1", new BigDecimal(40320));
        multiPeakMultiDayOverlappingCallsExpectedCost.put("2", new BigDecimal(25920));
        multiPeakMultiDayOverlappingCallsExpectedCost.put("3", new BigDecimal(10440));

        // continuousPeakMultiDayOverlapping test data
        CallBuilder continuousPeakMultiDayOverlappingCalls = new CallBuilder();

        continuousPeakMultiDayOverlappingCalls.setCaller("1").setCallee("2")
                .setStartDate("1-11-2013 21:00:00").setEndDate("2-11-2013 07:00:00")
                .setExpectedPeakTime(36000).add();

        continuousPeakMultiDayOverlappingCalls.setCaller("2").setCallee("1")
                .setStartDate("1-11-2013 19:00:00").setEndDate("2-11-2013 09:00:00")
                .setExpectedPeakTime(43200).add();

        continuousPeakMultiDayOverlappingCalls.setCaller("3").setCallee("2")
                .setStartDate("1-11-2013 19:00:00").setEndDate("2-11-2013 06:00:00")
                .setExpectedPeakTime(36000).add();

        HashMap<String, BigDecimal> continuousPeakMultiDayOverlappingCallsExpectedCost = new HashMap<String, BigDecimal>();
        continuousPeakMultiDayOverlappingCallsExpectedCost.put("1", new BigDecimal(18000));
        continuousPeakMultiDayOverlappingCallsExpectedCost.put("2", new BigDecimal(15120));
        continuousPeakMultiDayOverlappingCallsExpectedCost.put("3", new BigDecimal(29160));

        // peakPeriodMinuteAccuracy test data
        CallBuilder peakPeriodMinuteAccuracyCalls = new CallBuilder();

        peakPeriodMinuteAccuracyCalls.setCaller("1").setCallee("2")
                .setStartDate("1-11-2013 06:00:00").setEndDate("1-11-2013 10:00:00")
                .setExpectedPeakTime(14400).add();

        peakPeriodMinuteAccuracyCalls.setCaller("2").setCallee("1")
                .setStartDate("1-11-2013 10:00:00").setEndDate("1-11-2013 20:00:00")
                .setExpectedPeakTime(0).add();

        peakPeriodMinuteAccuracyCalls.setCaller("3").setCallee("2")
                .setStartDate("1-11-2013 20:00:00").setEndDate("1-11-2013 21:00:00")
                .setExpectedPeakTime(3600).add();

        HashMap<String, BigDecimal> peakPeriodMinuteAccuracyCallsExpectedCost = new HashMap<String, BigDecimal>();
        peakPeriodMinuteAccuracyCallsExpectedCost.put("1", new BigDecimal(7200));
        peakPeriodMinuteAccuracyCallsExpectedCost.put("2", new BigDecimal(10800));
        peakPeriodMinuteAccuracyCallsExpectedCost.put("3", new BigDecimal(2880));


        return Arrays.asList(new Object[][] {
            { "singlePeakSingleDayNonOverlappingCalls", singlePeakSingleDayNonOverlappingCalls,
                    singlePeakSingleDayNonOverlappingCallsExpectedCost, singlePeakPeriods },
            { "singlePeakSingleDayOverlappingCalls", singlePeakSingleDayOverlappingCalls,
                    singlePeakSingleDayOverlappingCallsExpectedCost, singlePeakPeriods },
            { "singlePeakMultiDayNonOverlappingCalls", singlePeakMultiDayNonOverlappingCalls,
                    singlePeakMultiDayNonOverlappingCallsExpectedCost, singlePeakPeriods },
            { "singlePeakMultiDayOverlappingCalls", singlePeakMultiDayOverlappingCalls,
                    singlePeakMultiDayOverlappingCallsExpectedCost, singlePeakPeriods },
            { "multiPeakSingleDayNonOverlappingCalls", multiPeakSingleDayNonOverlappingCalls,
                    multiPeakSingleDayNonOverlappingCallsExpectedCost, multiPeakPeriods },
            { "multiPeakSingleDayOverlappingCalls", multiPeakSingleDayOverlappingCalls,
                    multiPeakSingleDayOverlappingCallsExpectedCost, multiPeakPeriods },
            { "multiPeakMultiDayNonOverlappingCalls", multiPeakMultiDayNonOverlappingCalls,
                    multiPeakMultiDayNonOverlappingCallsExpectedCost, multiPeakPeriods },
            { "multiPeakMultiDayOverlappingCalls", multiPeakMultiDayOverlappingCalls,
                    multiPeakMultiDayOverlappingCallsExpectedCost, multiPeakPeriods },
            { "continuousPeakMultiDayOverlappingCalls", continuousPeakMultiDayOverlappingCalls,
                    continuousPeakMultiDayOverlappingCallsExpectedCost, continuousPeakPeriods },
            { "peakPeriodMinuteAccuracyCalls", peakPeriodMinuteAccuracyCalls,
                    peakPeriodMinuteAccuracyCallsExpectedCost, multiPeakPeriods }
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

            List<Bill.LineItem> actualCalls = bill.getCalls();

            assertEquals(String.format("[%s] Customer %s number of calls", name, phoneNumber),
                    expectedCalls.size(), actualCalls.size());

            for (int i = 0; i < expectedCalls.size(); i++) {
                Call expected = expectedCalls.get(i).getKey();
                Bill.LineItem actual = actualCalls.get(i);

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
        public static Locale locale = new Locale("en", "GB");
        public static DateFormat formatter = new SimpleDateFormat("d-M-y H:m:s",
                locale);

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
