package com.acmetelecom;

import com.acmetelecom.customer.Customer;

import java.math.BigDecimal;
import java.util.List;

public class Bill {
    protected Customer customer;
    protected List<BillingSystem.LineItem> calls;
    protected BigDecimal totalBill;

    public Bill(Customer customer, List<BillingSystem.LineItem> calls, BigDecimal totalBill) {
        this.customer = customer;
        this.calls = calls;
        this.totalBill = totalBill;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<BillingSystem.LineItem> getCalls() {
        return calls;
    }

    public BigDecimal getTotalBill() {
        return totalBill;
    }

    public void send() {
        Printer printer = HtmlPrinter.getInstance();
        printer.printHeading(customer.getFullName(), customer.getPhoneNumber(), customer.getPricePlan());
        for (BillingSystem.LineItem call : calls) {
            printer.printItem(call.date(), call.callee(), call.durationMinutes(), MoneyFormatter.penceToPounds(call.cost()));
        }
        printer.printTotal(MoneyFormatter.penceToPounds(totalBill));
    }

}
