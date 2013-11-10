package com.acmetelecom;

import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;

/**
 * Mock Tariff Database for testing
 */
class MockTariffDatabase implements TariffLibrary {

    private static MockTariffDatabase instance = new MockTariffDatabase();

    private MockTariffDatabase() {

    }

    public static MockTariffDatabase getInstance() {
        return instance;
    }

    @Override
    public Tariff tarriffFor(Customer customer) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
