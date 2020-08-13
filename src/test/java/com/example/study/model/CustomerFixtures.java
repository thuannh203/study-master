package com.example.study.model;

import java.util.UUID;

public class CustomerFixtures {

    public static Customer create(String customerCode){
        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID().toString());
        customer.setCustomerCode(customerCode);
        customer.setCustomerName("CustomerName");
        customer.setSex("Male");
        customer.setAge(24);
        customer.setAddress("Address");
        return customer;
    }

    public static String createJson(String customerCode){
        String json = "{"
                + "'customer_code':'" + customerCode + "',"
                + "'customer_name':'CustomerName',"
                + "'sex':'Male',"
                + "'age':24,"
                + "'address':'Address'"
                + "}";
        return json;
    }
}
