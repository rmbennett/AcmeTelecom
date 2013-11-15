package com.acmetelecom;

import com.acmetelecom.customer.Customer;

import java.math.BigDecimal;
import java.util.List;

public class Bill {
    protected Customer customer;
    protected List<LineItem> calls;
    protected BigDecimal totalBill;

    public Bill(Customer customer, List<LineItem> calls, BigDecimal totalBill) {
        this.customer = customer;
        this.calls = calls;
        this.totalBill = totalBill;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<LineItem> getCalls() {
        return calls;
    }

    public BigDecimal getTotalBill() {
        return totalBill;
    }

    public void send() {
        Printer printer = HtmlPrinter.getInstance();
        printer.printHeading(customer.getFullName(), customer.getPhoneNumber(), customer.getPricePlan());
        for (LineItem call : calls) {
            printer.printItem(call.date(), call.callee(), call.durationMinutes(), MoneyFormatter.penceToPounds(call.cost()));
        }
        printer.printTotal(MoneyFormatter.penceToPounds(totalBill));
    }

    static class LineItem {
        private Call call;
        private BigDecimal callCost;

        private int peakCallTime;

        public LineItem(Call call, BigDecimal callCost, int peakCallTime) {
            this.call = call;
            this.callCost = callCost;
            this.peakCallTime = peakCallTime;
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

        public int getPeakCallTime() {
            return peakCallTime;
        }
    }
}
