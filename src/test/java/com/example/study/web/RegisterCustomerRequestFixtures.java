package com.example.study.web;

public class RegisterCustomerRequestFixtures {

    public static RegisterCustomerRequest create(String customerCode){
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setCustomerCode(customerCode);
        request.setCustomerName("CustomerName");
        request.setSex("Male");
        request.setAge(24L);
        request.setAddress("Address");
        return request;
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
