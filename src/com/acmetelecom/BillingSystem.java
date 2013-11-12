package com.acmetelecom;

import com.acmetelecom.customer.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class BillingSystem {

    private List<CallEvent> callLog = new ArrayList<CallEvent>();
    private CustomerDatabase customerDatabase;
    private TariffLibrary tariffDatabase;

    public BillingSystem(CustomerDatabase customerDatabase, TariffLibrary tariffDatabase) {
        this.customerDatabase = customerDatabase;
        this.tariffDatabase = tariffDatabase;
    }

    public BillingSystem() {
        this(CentralCustomerDatabase.getInstance(),
                CentralTariffDatabase.getInstance());
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
        List<LineItem> items = new ArrayList<LineItem>();

        for (Call call : calls) {

            Tariff tariff = tariffDatabase.tarriffFor(customer);

            BigDecimal cost;

            //New changes in regulations means customer can only be charged for period they are in the peak period
            int totalCallTime = call.durationSeconds();
            int fullBillingsDays = (int) Math.floor(totalCallTime / (12 * 60 * 60));
            //First check if new system runs for longer than 24 hours. If it does, bill for full 24 hour amount
            int remainder = totalCallTime % (12 * 60 * 60);
            //Return remainder of time - this is the tricky part to bill


            if (DaytimePeakPeriod.offPeak(call.startTime()) && DaytimePeakPeriod.offPeak(call.endTime()) && call.durationSeconds() < 12 * 60 * 60) {
                cost = new BigDecimal(call.durationSeconds()).multiply(tariff.offPeakRate());
            } else {
                //Check if this call was
                cost = new BigDecimal(call.durationSeconds()).multiply(tariff.peakRate());
            }

            cost = cost.setScale(0, RoundingMode.HALF_UP);
            BigDecimal callCost = cost;
            totalBill = totalBill.add(callCost);
            items.add(new LineItem(call, callCost));
        }

        return new Bill(customer, items, totalBill);
    }

    static class LineItem {
        private Call call;
        private BigDecimal callCost;

        public LineItem(Call call, BigDecimal callCost) {
            this.call = call;
            this.callCost = callCost;
        }

        public String date() {
            return call.date();
        }

        public String caller() {
            return call.caller();
        }

        public String callee() {
            return call.callee();
        }

        public String durationMinutes() {
            return "" + call.durationSeconds() / 60 + ":" + String.format("%02d", call.durationSeconds() % 60);
        }

        public BigDecimal cost() {
            return callCost;
        }
    }
}
