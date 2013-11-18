package com.acmetelecom;

public class MockPrinter implements Printer {

    public static final String headingFormat = "%s %s %s\n";
    public static final String itemFormat = "%s %s %s %s\n";
    public static final String totalFormat = "%s\n";

    public static MockPrinter instance = new MockPrinter();

    private MockPrinter() {

    }

    public static MockPrinter getInstance() {
        return instance;
    }

    @Override
    public String heading(String name, String phoneNumber, String pricePlan) {
        return String.format(headingFormat, name, phoneNumber, pricePlan);
    }

    @Override
    public String item(String time, String callee, String duration, String cost) {
        return String.format(itemFormat, time, callee, duration, cost);
    }

    @Override
    public String total(String total) {
        return String.format(totalFormat, total);
    }
}
