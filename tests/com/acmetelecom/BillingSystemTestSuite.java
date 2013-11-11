package com.acmetelecom;


import org.junit.runners.Suite;
import org.junit.runner.RunWith;

@RunWith(Suite.class)
@Suite.SuiteClasses({BillingSystemTest.class, BillingSystemHTMLOutputTest.class})
public class BillingSystemTestSuite {
    // Test suite to first test that the BillingSystem class returns the right value first
    // and then to test that the HTML output is as expected
}
