package com.acmetelecom;

import com.acmetelecom.customer.Customer;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class BillTest {

    @Test
    public void sendTest() throws NoSuchFieldException {
        // Parameters for test
        String caller = "1";
        String callee = "2";
        long startTime = 0;
        long callDuration = 1000;
        long peakTime = 500;
        BigDecimal callCost = new BigDecimal(100);


        // Setup objects
        Printer printer = MockPrinter.getInstance();
        Customer customer = MockCustomerDatabase.getInstance()
                .getCustomer(caller);
        CallStart callStart = new CallStart(caller, callee, startTime);
        CallEnd callEnd = new CallEnd(caller, callee, startTime + callDuration);
        Call call = new Call(callStart, callEnd);

        ArrayList<Bill.LineItem> lines = new ArrayList<Bill.LineItem>();
        Bill.LineItem line = new Bill.LineItem(call, callCost, peakTime);
        lines.add(line);

        Bill bill = new Bill(customer, lines, callCost);

        String output = bill.send(printer);


        // Generate expected output
        String expectedOutput = "Standard Customer 1 Standard\n" +
                "01/01/70 01:00 2 0:01 1.00\n" +
                "1.00\n";

        assertEquals("Bill output", expectedOutput, output);
    }
}
