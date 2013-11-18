package com.acmetelecom;

public interface Printer {

    String printHeading(String name, String phoneNumber, String pricePlan);

    String printItem(String time, String callee, String duration, String cost);

    String printTotal(String total);
}
