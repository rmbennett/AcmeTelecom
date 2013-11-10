package com.acmetelecom;

import com.acmetelecom.customer.Customer;

import java.util.List;

public class Bill {
    protected Customer customer;
    protected List<BillingSystem.LineItem> calls;
    protected String totalBill;

    public Bill(Customer customer, List<BillingSystem.LineItem> calls, String totalBill) {
        setCustomer(customer);
        setCalls(calls);
        setTotalBill(totalBill);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<BillingSystem.LineItem> getCalls() {
        return calls;
    }

    public void setCalls(List<BillingSystem.LineItem> calls) {
        this.calls = calls;
    }

    public String getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(String totalBill) {
        this.totalBill = totalBill;
    }

    public void send() {
        Printer printer = HtmlPrinter.getInstance();
        printer.printHeading(customer.getFullName(), customer.getPhoneNumber(), customer.getPricePlan());
        for (BillingSystem.LineItem call : calls) {
            printer.printItem(call.date(), call.callee(), call.durationMinutes(), MoneyFormatter.penceToPounds(call.cost()));
        }
        printer.printTotal(totalBill);
    }

}
