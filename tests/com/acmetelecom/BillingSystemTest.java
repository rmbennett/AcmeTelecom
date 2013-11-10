package com.acmetelecom;

import com.acmetelecom.customer.CentralTariffDatabase;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

public class BillingSystemTest {

    BillingSystem billingSystem;

    @Before
    public void setUp() {
        billingSystem = new BillingSystem(MockCustomerDatabase.getInstance(), CentralTariffDatabase.getInstance());
    }

    /*
        Five minutes of off-peak calls for all three plans
        Total:
            - Standard: 60p
            - Business: 90p
            - Leisure: 30p
     */
    @Test
    public void testOffPeakCalls() {
        billingSystem.callInitiated("1", "2", getTimestamp(9, 0));
        billingSystem.callCompleted("1", "2", getTimestamp(9, 5));

        billingSystem.callInitiated("2", "1", getTimestamp(9, 10));
        billingSystem.callCompleted("2", "1", getTimestamp(9, 15));


        billingSystem.callInitiated("3", "2", getTimestamp(9, 20));
        billingSystem.callCompleted("3", "2", getTimestamp(9, 25));

        List<Bill> bills = billingSystem.createCustomerBills();

        for (Bill bill : bills) {
            if (MockCustomerDatabase.isStandard(bill.getCustomer())) {
                assertEquals("0.60", bill.getTotalBill());
            } else if (MockCustomerDatabase.isBusiness(bill.getCustomer())) {
                assertEquals("0.90", bill.getTotalBill());
            } else if (MockCustomerDatabase.isLeisure((bill.getCustomer()))) {
                assertEquals("0.30", bill.getTotalBill());
            }
        }
    }

    @Test
    public void testPeakCalls() {

    }

    @Test
    public void testOverlapCalls() {

    }

    @Test
    public void testIncompleteCalls() {

    }

    private long getTimestamp(int hour, int minute) {
        Date date = new Date();
        date.setDate(1);
        date.setMonth(3);
        date.setYear(2013);
        date.setHours(hour);
        date.setMinutes(minute);
        return date.getTime();

    }
}
