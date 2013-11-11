package com.acmetelecom;

import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a mock class to produce a list of mock customers for testing purposes
 */

public class MockCustomerDatabase implements CustomerDatabase {

    private static MockCustomerDatabase instance = new MockCustomerDatabase();
    private  List<Customer> customers = new ArrayList<Customer>();


    // Private constructor for singleton paradigm
    private MockCustomerDatabase() {
        MockCustomers standard = MockCustomers.Standard;
        MockCustomers business = MockCustomers.Business;
        MockCustomers leisure = MockCustomers.Leisure;

        Customer customerStandard = new Customer(standard.getFullName(), standard.getPhoneNumber(), standard.getPricePlan());
        Customer customerBusiness = new Customer(business.getFullName(), business.getPhoneNumber(), business.getPricePlan());
        Customer customerLeisure = new Customer(leisure.getFullName(), leisure.getPhoneNumber(), leisure.getPricePlan());

        customers.add(customerStandard);
        customers.add(customerBusiness);
        customers.add(customerLeisure);
    }

    public static MockCustomerDatabase getInstance(){
        return instance;
    }

    @Override
    public List<Customer> getCustomers() {
        return customers;
    }

    // Convenience method for test
    public Customer getCustomer(String phoneNumber) {
        for (Customer customer : customers) {
            if (customer.getPhoneNumber().equals(phoneNumber)) {
                return customer;
            }
        }

        return null;
    }

    public static enum MockCustomers {
        Standard("Standard Customer", "1", "Standard"),
        Business("Business Customer", "2", "Business"),
        Leisure("Leisure Customer", "3", "Leisure");

        private String fullName, phoneNumber, pricePlan;

        MockCustomers(String fullName, String phoneNumber, String pricePlan) {
            this.fullName = fullName;
            this.phoneNumber = phoneNumber;
            this.pricePlan = pricePlan;

        }

        public String getFullName() {
            return fullName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getPricePlan() {
            return pricePlan;
        }

    }
}
