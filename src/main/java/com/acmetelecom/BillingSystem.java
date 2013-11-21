package com.acmetelecom;

import com.acmetelecom.customer.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class BillingSystem {

    private List<CallEvent> callLog = new ArrayList<CallEvent>();
    private CustomerDatabase customerDatabase;
    private TariffLibrary tariffDatabase;
    private List<PeakPeriod> peakPeriods;
    public BillingSystem(CustomerDatabase customerDatabase, TariffLibrary tariffDatabase, List<PeakPeriod> peakPeriods) {
        this.customerDatabase = customerDatabase;
        this.tariffDatabase = tariffDatabase;
        this.peakPeriods = peakPeriods;
    }

    public BillingSystem() {
        this(CentralCustomerDatabase.getInstance(),
                CentralTariffDatabase.getInstance(),
                new ArrayList<PeakPeriod>());
        // "Default" peak period
        this.peakPeriods.add(new PeakPeriod(7, 19));
    }


    public void callInitiated(String caller, String callee, long timeStamp) {
        callLog.add(new CallStart(caller, callee, timeStamp));
    }

    public void callInitiated(String caller, String callee) {
        callInitiated(caller, callee, timeNow());
    }

    public void callCompleted(String caller, String callee, long timeStamp) {
        callLog.add(new CallEnd(caller, callee, timeStamp));
    }

    public void callCompleted(String caller, String callee) {
        callCompleted(caller, callee, timeNow());
    }

    public List<Bill> createCustomerBills() {
        List<Bill> bills = new ArrayList<Bill>();
        List<Customer> customers = customerDatabase.getCustomers();
        for (Customer customer : customers) {
            bills.add(createBillFor(customer));
        }
        callLog.clear();
        return bills;
    }

    private long timeNow() {
        return System.currentTimeMillis();
    }

    private Bill createBillFor(Customer customer) {
        List<CallEvent> customerEvents = new ArrayList<CallEvent>();
        for (CallEvent callEvent : callLog) {
            if (callEvent.getCaller().equals(customer.getPhoneNumber())) {
                customerEvents.add(callEvent);
            }
        }

        List<Call> calls = new ArrayList<Call>();

        CallEvent start = null;
        for (CallEvent event : customerEvents) {
            if (event instanceof CallStart) {
                start = event;
            }
            if (event instanceof CallEnd && start != null) {
                calls.add(new Call(start, event));
                start = null;
            }
        }

        BigDecimal totalBill = new BigDecimal(0);
        List<Bill.LineItem> items = new ArrayList<Bill.LineItem>();

        for (Call call : calls) {

            Tariff tariff = tariffDatabase.tarriffFor(customer);
            BigDecimal cost;

            // New changes in regulations means customer can only be charged for peak tariff price for the period they are in the peak period
            int peakCallTime = new PeakPeriodCalculator(peakPeriods).getTimeInSecondsInCallDuringPeak(call);

            cost = new BigDecimal(call.durationSeconds() - peakCallTime).multiply(tariff.offPeakRate());
            cost = cost.add(new BigDecimal(peakCallTime).multiply(tariff.peakRate()));
            cost = cost.setScale(0, RoundingMode.HALF_UP);
            BigDecimal callCost = cost;
            totalBill = totalBill.add(callCost);
            items.add(new Bill.LineItem(call, callCost, peakCallTime));
        }

        return new Bill(customer, items, totalBill);
    }

}
