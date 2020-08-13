package com.example.study.model;

import com.example.study.exception.NotFoundException;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ValidationException;
import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class CustomerServiceTest {

    @Mock
    CustomerRepositoryImpl customerRepositoryCustom;

    @Mock
    Validator validator;

    CustomerService sut;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        sut = new CustomerService(customerRepositoryCustom, validator);
    }

    /**
     * Test create
     * Case: validate model throws ValidationException
     */
    @Test
    public void testCreate_throwsVE(){
        // setup
        Customer customer = CustomerFixtures.create("customerCode");
        when(validator.validate(any())).thenThrow(ValidationException.class);
        // exercise
        Throwable thrown = catchThrowable(() -> sut.create(customer, true));
        // verify
        assertThat(thrown)
                .isInstanceOf(ValidationException.class);
    }

    /**
     * Test create
     * Case: validate model throws IllegalArgumentException
     */
    @Test
    public void testCreate_throwsIAE1(){
        // setup
        Customer customer = CustomerFixtures.create("customerCode");
        when(validator.validate(any())).thenThrow(IllegalArgumentException.class);
        // exercise
        Throwable thrown = catchThrowable(() -> sut.create(customer, true));
        // verify
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test create
     * Case: create model fail
     */
    @Test
    public void testCreate_throwsHE(){
        // setup
        Customer customer = CustomerFixtures.create("customerCode");
        doThrow(HibernateException.class).when(customerRepositoryCustom).create(any(Customer.class));
        // exercise
        Throwable thrown = catchThrowable(() -> sut.create(customer, true));
        // verify
        assertThat(thrown)
                .isInstanceOf(HibernateException.class);
    }

    /**
     * Test create
     * Case: rollback flag is true
     * Input:
     *    rollbackFlag = true
     * Output:
     *    Throw IllegalArgumentException
     */
    @Test
    public void testCreate_throwsIAE2(){
        // setup
        Customer customer = CustomerFixtures.create("customerCode");
        // exercise
        Throwable thrown = catchThrowable(() -> sut.create(customer, true));
        // verify
        assertThat(thrown)
                .hasMessage("Test Rollback")
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test create
     * Case: OK
     */
    @Test
    public void testCreate(){
        // setup
        Customer customer = CustomerFixtures.create("customerCode");
        // exercise
        Customer actual = sut.create(customer, false);
        // verify
        assertThat(actual).isEqualTo(customer);
    }

    /**
     * Test Delete
     * Case: OK
     */
    @Test
    public void testDelete(){
        // setup
        String customerCode = "customerCode";
        Customer customer = CustomerFixtures.create(customerCode);
        when(customerRepositoryCustom.findCustomer(anyString())).thenReturn(customer);
        // exercise
        Customer actual = sut.deleteCustomer(customerCode, false);
        // verify
        assertThat(actual).isEqualTo(customer);
    }

    /**
     * Test Delete
     * Case: Throw NFE
     * Input:
     *    customerRepositoryCustom.findCustomer return null
     * Output:
     *    Throw NFE
     */
    @Test
    public void testDelete_throwsNFE() {
        // setup
        String customerCode = "customerCode";
        when(customerRepositoryCustom.findCustomer(anyString())).thenReturn(null);
        // exercise
        Throwable actual = catchThrowable(() -> sut.deleteCustomer(customerCode, false));
        // verify
        assertThat(actual)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Not exist");
    }

    /**
     * Test Delete
     * Case: Throw HibernateException
     * Input:
     *    customerRepositoryCustom.findCustomer throw HibernateException
     * Output:
     *    Throw HibernateException
     */
    @Test
    public void testDelete_throwsHE() {
        // setup
        String customerCode = "customerCode";
        doThrow(HibernateException.class).when(customerRepositoryCustom).findCustomer(anyString());
        // exercise
        Throwable actual = catchThrowable(() -> sut.deleteCustomer(customerCode, false));
        // verify
        assertThat(actual)
                .isInstanceOf(HibernateException.class);
    }

    /**
     * Test Delete
     * Case: Throw IllegalArgumentException
     * Input:
     *    rollbackFlag = true
     * Output:
     *    Throw IllegalArgumentException
     */
    @Test
    public void testDelete_throwsIAE() {
        // setup
        String customerCode = "customerCode";
        when(customerRepositoryCustom.findCustomer(anyString())).thenReturn(CustomerFixtures.create(customerCode));
        // exercise
        Throwable actual = catchThrowable(() -> sut.deleteCustomer(customerCode, true));
        // verify
        assertThat(actual)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Test Rollback");
    }

    /**
     * Test Upsert
     * Case: Throw IllegalArgumentException
     * Input:
     *    validate throw IllegalArgumentException
     * Output:
     *    Throw IllegalArgumentException
     */
    @Test
    public void testUpsert_throwsIAE1() {
        // setup
        String customerCode = "customerCode";
        Customer customer = CustomerFixtures.create(customerCode);
        doThrow(IllegalArgumentException.class).when(validator).validate(any(Customer.class));

        // exercise
        Throwable actual = catchThrowable(() -> sut.upsert(customerCode, customer, true));
        // verify
        assertThat(actual)
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test Upsert
     * Case: Throw ValidationException
     * Input:
     *    validate throw ValidationException
     * Output:
     *    Throw ValidationException
     */
    @Test
    public void testUpsert_throwsVE() {
        // setup
        String customerCode = "customerCode";
        Customer customer = CustomerFixtures.create(customerCode);
        doThrow(ValidationException.class).when(validator).validate(any(Customer.class));

        // exercise
        Throwable actual = catchThrowable(() -> sut.upsert(customerCode, customer, true));
        // verify
        assertThat(actual)
                .isInstanceOf(ValidationException.class);
    }

    /**
     * Test Upsert
     * Case: Throw ValidationException
     * Input:
     *    validate throw ValidationException
     * Output:
     *    Throw ValidationException
     */
    @Test
    public void testUpsert_throwsHE() {
        // setup
        String customerCode = "customerCode";
        Customer customer = CustomerFixtures.create(customerCode);
        doThrow(HibernateException.class).when(customerRepositoryCustom)
                .upsert(any(), any());

        // exercise
        Throwable actual = catchThrowable(() -> sut.upsert(customerCode, customer, false));
        // verify
        assertThat(actual)
                .isInstanceOf(HibernateException.class);
    }

    /**
     * Test Upsert
     * Case: Throw IllegalArgumentException
     * Input:
     *    rollbackFlag = true
     * Output:
     *    Throw IllegalArgumentException
     */
    @Test
    public void testUpsert_throwsIAE2() {
        // setup
        String customerCode = "customerCode";
        Customer customer = CustomerFixtures.create(customerCode);
        // exercise
        Throwable actual = catchThrowable(() -> sut.upsert(customerCode, customer, true));
        // verify
        assertThat(actual)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Test Rollback");
    }

    /**
     * Test Upsert
     * Case: OK
     */
    @Test
    public void testUpsert() {
        // setup
        String customerCode = "customerCode";
        Customer customer = CustomerFixtures.create(customerCode);
        // exercise
        sut.upsert(customerCode, customer, false);
    }
}
