package com.example.study.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String>, CustomerRepositoryCustom {

    Customer findByCustomerCode(@Param("customerCode") String customerCode);

    @Query("SELECT COUNT(c) FROM Customer c")
    long numbersOfCustomer();
}
