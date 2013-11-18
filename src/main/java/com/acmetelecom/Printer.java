package com.acmetelecom;

public interface Printer {

    String heading(String name, String phoneNumber, String pricePlan);

    String item(String time, String callee, String duration, String cost);

    String total(String total);
}
