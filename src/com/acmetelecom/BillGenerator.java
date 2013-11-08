package com.acmetelecom;

import com.acmetelecom.customer.Customer;

import java.util.List;

public class BillGenerator {

    // This class only has static methods. Make it non-inheritable, and instantiable
    private BillGenerator() {
    }

    public static void send(Customer customer, List<BillingSystem.LineItem> calls, String totalBill) {

        Printer printer = HtmlPrinter.getInstance();
        printer.printHeading(customer.getFullName(), customer.getPhoneNumber(), customer.getPricePlan());
        for (BillingSystem.LineItem call : calls) {
            printer.printItem(call.date(), call.callee(), call.durationMinutes(), MoneyFormatter.penceToPounds(call.cost()));
        }
        printer.printTotal(totalBill);
    }

}
