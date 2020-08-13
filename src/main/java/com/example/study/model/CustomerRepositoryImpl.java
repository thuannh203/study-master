package com.example.study.model;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Repository
public class CustomerRepositoryImpl implements CustomerRepositoryCustom{

    @Autowired
    @Qualifier(value = "entityManagerFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private CustomerRepository customerRepository;

    public void create(Customer customer){
        try {
            Session session = this.sessionFactory.getCurrentSession();
            session.save(customer);
        }catch (HibernateException e){
            log.error("save failed!");
            throw e;
        }
    }

    public long count(){
        return customerRepository.numbersOfCustomer();
    }

    public void upsert(Customer customer, Customer customerToUpdate){
        try {
            Session session = this.sessionFactory.getCurrentSession();
            Optional.ofNullable(customer).ifPresent(session::evict);
            session.saveOrUpdate(customerToUpdate);
        }catch (HibernateException e){
            log.error("save failed!");
            throw e;
        }
    }

    public Customer findCustomer(String customerCode){
        return customerRepository.findByCustomerCode(customerCode);
    }

    public void delete(Customer deleteCustomer) {
        try {
            Session session = this.sessionFactory.getCurrentSession();
            session.delete(deleteCustomer);
        }catch (HibernateException e){
            log.error("Delete failed!");
            throw e;
        }
    }
	
    public Page<Customer> findCustomer(Pageable pageable){
        return customerRepository.findAll(pageable);
    }

}
