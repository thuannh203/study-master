package com.example.study.model;

import com.example.study.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class CustomerService {

    @Autowired
    private CustomerRepositoryImpl customerRepositoryCustom;

    @Autowired
    private Validator validator;

    @Transactional
    public Customer create(Customer customer, boolean rollbackFlag){
        validator.validate(customer);
        customerRepositoryCustom.create(customer);
        if(rollbackFlag){
            IllegalArgumentException e = new IllegalArgumentException("Test Rollback");
            log.error("Rollback transaction");
            throw e;
        }
        return customer;
    }

    @Transactional
    public Customer upsert(String customerCode, Customer customerToUpdate, boolean rollbackFlag){
        Customer customer = customerRepositoryCustom.findCustomer(customerCode);
        if(customer!=null){
            customerToUpdate.setCustomerId(customer.getCustomerId());
        }
        validator.validate(customerToUpdate);
        customerRepositoryCustom.upsert(customer, customerToUpdate);
        if(rollbackFlag){
            IllegalArgumentException e = new IllegalArgumentException("Test Rollback");
            log.error("Rollback transaction");
            throw e;
        }
        return customerToUpdate;
    }

    @Transactional(readOnly = true)
    public Customer findCustomer(String customerCode){
        return customerRepositoryCustom.findCustomer(customerCode);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findCustomer(Pageable pageable){
        long totalPage = customerRepositoryCustom.count();
        long quotient = totalPage / pageable.getPageSize();
        long lastPage = totalPage % pageable.getPageSize() == 0 ?
                quotient : quotient+1;
        Map<String, Object> data = new HashMap<>();
        List<Customer> customerList = Optional.ofNullable(customerRepositoryCustom.findCustomer(pageable)).
                orElseThrow(() -> new NotFoundException("Cannot find customer"))
                .getContent();
        data.put("customers", customerList);
        data.put("last_page", lastPage-1);
        return data;
    }

    @Transactional
    public Customer deleteCustomer(String customerCode, boolean rollbackFlag) {
        Customer deleteCustomer = Optional.ofNullable(customerRepositoryCustom.findCustomer(customerCode))
                .orElseThrow(() -> new NotFoundException("Not exist"));
        customerRepositoryCustom.delete(deleteCustomer);
        if(rollbackFlag){
            IllegalArgumentException e = new IllegalArgumentException("Test Rollback");
            log.error("Rollback transaction");
            throw e;
        }
        return deleteCustomer;
    }
}
