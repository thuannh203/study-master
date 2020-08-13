package com.example.study.web;

import com.example.study.exception.NotFoundException;
import com.example.study.model.Customer;
import com.example.study.model.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.validation.ValidationException;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mvc;

    @Mock
    private CustomerService customerService;

    private ObjectMapper objectMapper = new ObjectMapper();

    CustomerController sut;


    protected HttpMessageConverter<?>[] httpMessageConverter() {
        return new HttpMessageConverter[]{
                new MappingJackson2HttpMessageConverter(objectMapper),
                new StringHttpMessageConverter(),
        };
    }

    /**
     * Create {@link MockMvc} for metropolis controller test.
     *
     * @param controllers system under test
     * @return created {@link MockMvc}
     */
    protected MockMvc mockMvcFor(Object... controllers) {
        return MockMvcBuilders.standaloneSetup(controllers)
                .setMessageConverters(httpMessageConverter())
                .build();
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sut = new CustomerController(customerService);
        mvc = mockMvcFor(sut);
    }

    @Test
    public void testRegisterCustomer() throws Exception {
        // setup
        RegisterCustomerRequest request = RegisterCustomerRequestFixtures.create("customerCode");
        Customer customer = request.get();
        when(customerService.create(any(Customer.class), anyBoolean())).thenReturn(customer);

        // exercise
        this.mvc.perform(post("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                // verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer_code", is(customer.getCustomerCode())));
    }

    @Test
    public void testRegisterServiceThrowException() throws Exception {
        assertRegisterServiceThrowException(IllegalArgumentException.class, status().isBadRequest());
        assertRegisterServiceThrowException(ValidationException.class, status().isBadRequest());
        assertRegisterServiceThrowException(HibernateException.class, status().isConflict());
    }

    public void assertRegisterServiceThrowException(Class<? extends Throwable> throwable, ResultMatcher httpStatus) throws Exception {
        // setup
        RegisterCustomerRequest request = RegisterCustomerRequestFixtures.create("customerCode");
        when(customerService.create(any(Customer.class), anyBoolean())).thenThrow(throwable);
        // exercise
        this.mvc.perform(post("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                // verify
                .andExpect(httpStatus);
    }

    @Test
    public void testFindCustomer() throws Exception {
        // setup
        RegisterCustomerRequest request = RegisterCustomerRequestFixtures.create("customerCode");
        Customer customer = request.get();
        when(customerService.findCustomer(anyString())).thenReturn(customer);

        // exercise
        this.mvc.perform(get("/customer/customerCode"))
                .andDo(print())
                // verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer_code", is(customer.getCustomerCode())));
    }

    @Test
    public void testFindCustomer_throwNFE() throws Exception {
        // setup
        when(customerService.findCustomer(anyString())).thenReturn(null);

        // exercise
        this.mvc.perform(get("/customer/customerCode"))
                .andDo(print())
                // verify
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        // exercise
        this.mvc.perform(delete("/customer/customerCode"))
                .andDo(print())
                // verify
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteServiceThrowException() throws Exception {
        assertDeleteServiceThrowException(NotFoundException.class, status().isNotFound());
        assertDeleteServiceThrowException(IllegalArgumentException.class, status().isBadRequest());
        assertDeleteServiceThrowException(HibernateException.class, status().isConflict());
    }

    public void assertDeleteServiceThrowException(Class<? extends Throwable> throwable, ResultMatcher httpStatus) throws Exception {
        // setup
        doThrow(throwable).when(customerService).deleteCustomer(anyString(), anyBoolean());
        // exercise
        this.mvc.perform(delete("/customer/customerCode"))
                .andDo(print())
                // verify
                .andExpect(httpStatus);
    }

    @Test
    public void testUpsertCustomer() throws Exception {
        // setup
        RegisterCustomerRequest request = RegisterCustomerRequestFixtures.create("customerCode");
        Customer customer = request.get();
        when(customerService.upsert(anyString(), any(Customer.class), anyBoolean())).thenReturn(customer);
        // exercise
        this.mvc.perform(put("/customer/customerCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                // verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer_code", is(customer.getCustomerCode())));
    }

    @Test
    public void testUpsertServiceThrowException() throws Exception {
        assertUpsertServiceThrowException(IllegalArgumentException.class, status().isBadRequest());
        assertUpsertServiceThrowException(ValidationException.class, status().isBadRequest());
        assertUpsertServiceThrowException(HibernateException.class, status().isConflict());
    }

    public void assertUpsertServiceThrowException(Class<? extends Throwable> throwable, ResultMatcher httpStatus) throws Exception {
        // setup
        RegisterCustomerRequest request = RegisterCustomerRequestFixtures.create("customerCode");
        Customer customer = request.get();
        when(customerService.upsert(anyString(), any(Customer.class), anyBoolean())).thenThrow(throwable);
        // exercise
        this.mvc.perform(put("/customer/customerCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                // verify
                .andExpect(httpStatus);
    }

}
