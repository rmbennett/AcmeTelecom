package com.acmetelecom;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rmb209
 * Date: 07/11/13
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
public class Runner {

    public static void main(String[] args) throws Exception {
        System.out.println("Running...");
        BillingSystem billingSystem = new BillingSystem();
        billingSystem.callInitiated("447722113434", "447766511332");
        sleepSeconds(2);
        billingSystem.callCompleted("447722113434", "447766511332");
        billingSystem.callInitiated("447722113434", "447711111111");
        sleepSeconds(1);
        billingSystem.callCompleted("447722113434", "447711111111");
        billingSystem.callInitiated("447777765432", "447711111111");
        sleepSeconds(1);
        billingSystem.callCompleted("447777765432", "447711111111");
        List<Bill> bills = billingSystem.createCustomerBills();

        for (Bill bill : bills) {
            bill.send();
        }
    }
    private static void sleepSeconds(int n) throws InterruptedException {
        Thread.sleep(n * 1000);
    }



}
