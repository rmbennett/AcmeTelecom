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


    // Private constructor for singleton paradigm
    private MockCustomerDatabase() {

    }

    public static MockCustomerDatabase getInstance(){
        return instance;
    }

    @Override
    public List<Customer> getCustomers() {
        List<Customer> list = new ArrayList<Customer>();

        MockCustomers standard = MockCustomers.Standard;
        MockCustomers business = MockCustomers.Business;
        MockCustomers leisure = MockCustomers.Leisure;

        Customer customerStandard = new Customer(standard.getFullName(), standard.getPhoneNumber(), standard.getPricePlan());
        Customer customerBusiness = new Customer(business.getFullName(), business.getPhoneNumber(), business.getPricePlan());
        Customer customerLeisure = new Customer(leisure.getFullName(), leisure.getPhoneNumber(), leisure.getPricePlan());

        list.add(customerStandard);
        list.add(customerBusiness);
        list.add(customerLeisure);

        return list;
    }

    // Static helper methods
    public static boolean isType(Customer customer, MockCustomers type) {
        return customer.getPhoneNumber() == type.getPhoneNumber() && customer.getFullName() == type.getPhoneNumber() &&
                customer.getPricePlan() == type.getPricePlan();
    }

    public static boolean isStandard(Customer customer) {
        return isType(customer, MockCustomers.Standard);
    }

    public static boolean isBusiness(Customer customer) {
        return isType(customer, MockCustomers.Business);
    }

    public static boolean isLeisure(Customer customer) {
        return isType(customer, MockCustomers.Leisure);
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
