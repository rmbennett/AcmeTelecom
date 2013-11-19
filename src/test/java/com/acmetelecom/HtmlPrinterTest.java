package com.acmetelecom;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HtmlPrinterTest {
    private Printer printer;
    @Before
    public void setUp() throws Exception {
        printer = HtmlPrinter.getInstance();
    }

    @Test
    public void testHeading() throws Exception {
        String name = "Name";
        String phoneNumber = "1234";
        String plan = "Standard";

        String actual = printer.heading(name, phoneNumber, plan);

        String expectedFormat = "<html>\n" +
                "<head></head>\n" +
                "<body>\n" +
                "<h1>\n" +
                "Acme Telecom\n" +
                "</h1>\n" +
                "<h2>%s/%s - Price Plan: %s</h2>\n" +
                "<table border=\"1\"><tr><th width=\"160\">" +
                "Time</th><th width=\"160\">Number</th><th width=\"160\">" +
                "Duration</th><th width=\"160\">Cost</th></tr>\n";

        String expected = String.format(expectedFormat, name,
                phoneNumber, plan);

        assertEquals(expected, actual);
    }

    @Test
    public void testItem() throws Exception {
        String date = "Date";
        String callee = "1234";
        String duration = "1";
        String cost = "£100";

        String actual = printer.item(date, callee, duration, cost);

        String expectedFormat = "<tr><td>%s</td><td>%s</td><td>%s</td>" +
                "<td>%s</td></tr>\n";

        String expected = String.format(expectedFormat, date,
                callee, duration, cost);

        assertEquals(expected, actual);
    }

    @Test
    public void testTotal() throws Exception {
        String total = "£100";

        String actual = printer.total(total);

        String expectedFormat = "</table>\n" +
                "<h2>Total: %s</h2>\n" +
                "</body>\n" +
                "</html>\n";

        String expected = String.format(expectedFormat, total);

        assertEquals(actual, expected);
    }
}
