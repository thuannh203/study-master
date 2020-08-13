package com.example.study.web;

import com.example.study.common.CommonTimestamp;
import com.example.study.model.Customer;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class RegisterCustomerRequest {
    @JsonProperty("customer_code")
    @NotNull
    private String customerCode;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("sex")
    private String sex;

    @JsonProperty("age")
    private Long age;

    @JsonProperty("address")
    private String address;

    public Customer get() {
        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID().toString());
        customer.setCustomerCode(this.customerCode);
        customer.setCustomerName(this.customerName);
        customer.setSex(this.sex);
        customer.setAge(this.age);
        customer.setAddress(this.address);
        return customer;
    }
}
