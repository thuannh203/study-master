package com.example.study.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class CustomerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    Flyway flyway;

    @BeforeEach
    public void setUp(){
        flyway.clean();
        flyway.migrate();
    }
	
    /**
     * Test findCustomer
     * Case: OK
     */
    @Test
    public void testCreateCustomer() {
        // setup
        HttpHeaders headers = new HttpHeaders();
        String customerCode = randomCode();
        RegisterCustomerRequest registerCustomerRequest = RegisterCustomerRequestFixtures.create(customerCode);

        // exercise
        ResponseEntity<String> actual = restTemplate
                .exchange("/customer", HttpMethod.POST, new HttpEntity<>(registerCustomerRequest, headers), String.class);

        // verify
        log.info("POST Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customer_code", is(customerCode))
                .assertThat("$.customer_name", is(registerCustomerRequest.getCustomerName()))
                .assertThat("$.sex", is(registerCustomerRequest.getSex()))
                .assertThat("$.age", is(registerCustomerRequest.getAge().intValue()))
                .assertThat("$.address", is(registerCustomerRequest.getAddress()));
    }

    /**
     * Test findCustomer
     * Case: Rollback
     * Input:
     *    rollbackFlag = true
     * Output:
     *    Cannot create Customer
     */
    @Test
    public void testCreateCustomer_rollback() {
        // setup
        HttpHeaders headers = new HttpHeaders();
        String customerCode = randomCode();
        RegisterCustomerRequest registerCustomerRequest = RegisterCustomerRequestFixtures.create(customerCode);
        // exercise
        ResponseEntity<String> actual = restTemplate
                .exchange("/customer?rollbackFlag=true", HttpMethod.POST, new HttpEntity<>(registerCustomerRequest, headers), String.class);

        // verify
        log.info("POST Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.BAD_REQUEST));

        actual = restTemplate
                .exchange("/customer/" + customerCode, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        // verify
        log.info("GET Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.NOT_FOUND));
        with(actual.getBody())
                .assertThat("$.status", is(404))
                .assertThat("$.error", is("Not Found"));
    }

    /**
     * Test findCustomer
     * Case: OK
     */
    @Test
    public void testFindCustomer() {
        // setup
        HttpHeaders headers = new HttpHeaders();
        String customerCode = createCustomer(headers);
        // exercise
        ResponseEntity<String> actual = restTemplate
                .exchange("/customer/" + customerCode, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        // verify
        log.info("GET Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customer_code", is(customerCode))
                .assertThat("$.customer_name", is("CustomerName"))
                .assertThat("$.sex", is("Male"))
                .assertThat("$.age", is(24))
                .assertThat("$.address", is("Address"));
    }

    /**
     * Test findCustomer
     * Case: OK
     */
    @Test
    public void testFindListCustomer1() {
        // setup
        HttpHeaders headers = new HttpHeaders();
        String customerCode1 = createCustomer(headers);
        String customerCode2 = createCustomer(headers);
        String customerCode3 = createCustomer(headers);
        String customerCode4 = createCustomer(headers);
        String customerCode5 = createCustomer(headers);
        String customerCode6 = createCustomer(headers);
        String customerCode7 = createCustomer(headers);
        String customerCode8 = createCustomer(headers);
        String customerCode9 = createCustomer(headers);
        // exercise
        ResponseEntity<String> actual = restTemplate
                .exchange("/customer?size=5&page=0", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        // verify
        log.info("GET Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customers", hasSize(5))
                .assertThat("$.customers[0].customer_code", is(customerCode1))
                .assertThat("$.customers[1].customer_code", is(customerCode2))
                .assertThat("$.customers[2].customer_code", is(customerCode3))
                .assertThat("$.customers[3].customer_code", is(customerCode4))
                .assertThat("$.customers[4].customer_code", is(customerCode5))
                .assertThat("$.current_page", is(0))
                .assertThat("$.last_page", is(1));

        actual = restTemplate
                .exchange("/customer?size=5&page=1", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        // verify
        log.info("GET Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customers", hasSize(4))
                .assertThat("$.customers[0].customer_code", is(customerCode6))
                .assertThat("$.customers[1].customer_code", is(customerCode7))
                .assertThat("$.customers[2].customer_code", is(customerCode8))
                .assertThat("$.customers[3].customer_code", is(customerCode9))
                .assertThat("$.current_page", is(1))
                .assertThat("$.last_page", is(1));

        actual = restTemplate
                .exchange("/customer", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        // verify
        log.info("GET Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customers", hasSize(9))
                .assertThat("$.customers[0].customer_code", is(customerCode1))
                .assertThat("$.customers[1].customer_code", is(customerCode2))
                .assertThat("$.customers[2].customer_code", is(customerCode3))
                .assertThat("$.customers[3].customer_code", is(customerCode4))
                .assertThat("$.customers[4].customer_code", is(customerCode5))
                .assertThat("$.customers[5].customer_code", is(customerCode6))
                .assertThat("$.customers[6].customer_code", is(customerCode7))
                .assertThat("$.customers[7].customer_code", is(customerCode8))
                .assertThat("$.customers[8].customer_code", is(customerCode9))
                .assertThat("$.current_page", is(0))
                .assertThat("$.last_page", is(0));
    }

    /**
     * Test findCustomer
     * Case: OK
     */
    @Test
    public void testFindListCustomer2() {
        // setup
        HttpHeaders headers = new HttpHeaders();
        // exercise
        ResponseEntity<String> actual = restTemplate
                .exchange("/customer", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        // verify
        log.info("GET Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customers", hasSize(0));
    }

    /**
     * Test findCustomer
     * Case: Throw NFE
     * Input:
     *    find not exist Customer
     * Output:
     *    404
     */
    @Test
    public void testFindCustomer_throwsNFE() {
        // setup
        HttpHeaders headers = new HttpHeaders();
        // exercise
        ResponseEntity<String> actual = restTemplate
                .exchange("/customer/customerCode", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        // verify
        log.info("GET Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.NOT_FOUND));
        with(actual.getBody())
                .assertThat("$.status", is(404))
                .assertThat("$.error", is("Not Found"));
    }

    /**
     * Test findCustomer
     * Case: OK
     * Scenario:
     *    Step 1: Create Customer
     *    Step 2: find Customer => Ok
     *    Step 3: Delete Customer
     *    Step 4: find Customer => Fail
     */
    @Test
    public void testDeleteCustomer() {
        // setup
        HttpHeaders headers = new HttpHeaders();
        String customerCode = createCustomer(headers);
        // exercise
        ResponseEntity<String> actual = restTemplate
                .exchange("/customer/" + customerCode, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        // verify
        log.info("GET Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customer_code", is(customerCode));

        // Delete customer
        actual = restTemplate
                .exchange("/customer/" + customerCode, HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        log.info("Delete Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customer_code", is(customerCode));

        // Get after delete
        actual = restTemplate
                .exchange("/customer/" + customerCode, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        // verify
        log.info("GET Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.NOT_FOUND));
        with(actual.getBody())
                .assertThat("$.status", is(404))
                .assertThat("$.error", is("Not Found"));
    }

    /**
     * Test findCustomer
     * Case: Throw NotFoundException
     * Input:
     *    delete not exist customer_code
     * Output:
     *    Throw 404
     */
    @Test
    public void testDeleteCustomer_ThrowsNFE() {
        // setup
        HttpHeaders headers = new HttpHeaders();
        // Delete customer
        ResponseEntity<String> actual = restTemplate
                .exchange("/customer/customerCode", HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        log.info("Delete Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.NOT_FOUND));

        // verify
        with(actual.getBody())
                .assertThat("$.status", is(404))
                .assertThat("$.error", is("Not Found"));
    }

    /**
     * Test findCustomer
     * Case: Throw IllegalArgumentException
     * Input:
     *    rollbackFlag = true
     * Output:
     *    Throw 400
     */
    @Test
    public void testDeleteCustomer_rollback() {
        // setup
        HttpHeaders headers = new HttpHeaders();
        String customerCode = createCustomer(headers);
        // Delete customer
        ResponseEntity<String> actual = restTemplate
                .exchange("/customer/{customerCode}?rollbackFlag=true", HttpMethod.DELETE,
                        new HttpEntity<>(headers), String.class, customerCode);
        log.info("Delete Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.BAD_REQUEST));

        // verify
        with(actual.getBody())
                .assertThat("$.status", is(400))
                .assertThat("$.error", is("Bad Request"));

        actual = restTemplate
                .exchange("/customer/" + customerCode, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        // verify
        log.info("GET Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customer_code", is(customerCode));
    }

    /**
     * Test findCustomer
     * Case: create Customer
     * Input:
     *    Put with not exist customer code
     * Output:
     *    create success
     */
    @Test
    public void testUpsertCustomer_createCustomer() {
        // setup
        HttpHeaders headers = new HttpHeaders();
        String customerCode = randomCode();
        RegisterCustomerRequest registerCustomerRequest = RegisterCustomerRequestFixtures.create(customerCode);

        // exercise
        ResponseEntity<String> actual = restTemplate
                .exchange("/customer/{customerCode}", HttpMethod.PUT, new HttpEntity<>(registerCustomerRequest, headers), String.class, customerCode);

        // verify
        log.info("POST Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customer_code", is(customerCode))
                .assertThat("$.customer_name", is(registerCustomerRequest.getCustomerName()))
                .assertThat("$.sex", is(registerCustomerRequest.getSex()))
                .assertThat("$.age", is(registerCustomerRequest.getAge().intValue()))
                .assertThat("$.address", is(registerCustomerRequest.getAddress()));

        actual = restTemplate
                .exchange("/customer/" + customerCode, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        // verify
        log.info("GET Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customer_code", is(customerCode));
    }

    /**
     * Test findCustomer
     * Case: update Customer
     * Input:
     *    Put with existed customer code
     * Output:
     *    create success
     */
    @Test
    public void testUpsertCustomer_updateCustomer() {
        // setup
        HttpHeaders headers = new HttpHeaders();
        String customerCode = createCustomer(headers);

        // verify
        ResponseEntity<String> actual = restTemplate
                .exchange("/customer/" + customerCode, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        // verify
        log.info("GET Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customer_code", is(customerCode))
                .assertThat("$.customer_name", is("CustomerName"))
                .assertThat("$.sex", is("Male"))
                .assertThat("$.age", is(24))
                .assertThat("$.address", is("Address"));

        // update
        RegisterCustomerRequest updateRequest = RegisterCustomerRequestFixtures.create(customerCode);
        updateRequest.setCustomerName("updatedName");
        updateRequest.setSex("Female");
        updateRequest.setAge(25L);
        updateRequest.setAddress("updatedAddress");
        // exercise
        actual = restTemplate
                .exchange("/customer/{customerCode}", HttpMethod.PUT, new HttpEntity<>(updateRequest, headers), String.class, customerCode);

        // verify
        log.info("POST Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customer_code", is(customerCode))
                .assertThat("$.customer_name", is(updateRequest.getCustomerName()))
                .assertThat("$.sex", is(updateRequest.getSex()))
                .assertThat("$.age", is(updateRequest.getAge().intValue()))
                .assertThat("$.address", is(updateRequest.getAddress()));

        actual = restTemplate
                .exchange("/customer/" + customerCode, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        // verify
        log.info("GET Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customer_code", is(customerCode))
                .assertThat("$.customer_name", is(updateRequest.getCustomerName()))
                .assertThat("$.sex", is(updateRequest.getSex()))
                .assertThat("$.age", is(updateRequest.getAge().intValue()))
                .assertThat("$.address", is(updateRequest.getAddress()));
    }

    /**
     * Test findCustomer
     * Case: rollback
     */
    @Test
    public void testUpsertCustomer_rollback() {
        // setup
        HttpHeaders headers = new HttpHeaders();
        String customerCode = createCustomer(headers);

        // verify
        ResponseEntity<String> actual = restTemplate
                .exchange("/customer/" + customerCode, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        // verify
        log.info("GET Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customer_code", is(customerCode))
                .assertThat("$.customer_name", is("CustomerName"))
                .assertThat("$.sex", is("Male"))
                .assertThat("$.age", is(24))
                .assertThat("$.address", is("Address"));

        // update
        RegisterCustomerRequest updateRequest = RegisterCustomerRequestFixtures.create(customerCode);
        updateRequest.setCustomerName("updatedName");
        updateRequest.setSex("Female");
        updateRequest.setAge(25L);
        updateRequest.setAddress("updatedAddress");
        // exercise
        actual = restTemplate
                .exchange("/customer/{customerCode}?rollbackFlag=true", HttpMethod.PUT, new HttpEntity<>(updateRequest, headers), String.class, customerCode);

        // verify
        log.info("POST Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        with(actual.getBody())
                .assertThat("$.status", is(400))
                .assertThat("$.error", is("Bad Request"));

        actual = restTemplate
                .exchange("/customer/" + customerCode, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        // verify
        log.info("GET Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        with(actual.getBody())
                .assertThat("$.customer_code", is(customerCode))
                .assertThat("$.customer_name", is("CustomerName"))
                .assertThat("$.sex", is("Male"))
                .assertThat("$.age", is(24))
                .assertThat("$.address", is("Address"));
    }

    private String createCustomer(HttpHeaders headers) {
        String customerCode = randomCode();
        RegisterCustomerRequest registerCustomerRequest = RegisterCustomerRequestFixtures.create(customerCode);

        // exercise
        ResponseEntity<String> actual = restTemplate
                .exchange("/customer", HttpMethod.POST, new HttpEntity<>(registerCustomerRequest, headers), String.class);

        // verify
        log.info("POST Customer response = {}", actual);
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
        return customerCode;
    }

    private String randomCode() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
