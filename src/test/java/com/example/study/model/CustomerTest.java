package com.example.study.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerTest {

    JacksonTester<Customer> json;

    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        JacksonTester.initFields(this, mapper);
        assert json != null;
    }

    @Test
    public void testJsonToValue() throws Exception {
        // setup
        String requestJson = CustomerFixtures.createJson("customerCode");
        Customer expected = CustomerFixtures.create("customerCode");
        expected.setCustomerId(null);
        // exercise
        ObjectContent<Customer> actual = json.parse(requestJson);

        //verify
        actual.assertThat().isEqualTo(expected);
    }

    @Test
    public void testValueToJson() throws Exception {
        // setup
        Customer request = CustomerFixtures.create("customerCode");

        // exercise
        JsonContent<Customer> actual = json.write(request);

        // verify
        assertThat(actual).extractingJsonPathStringValue("@.customer_code").isEqualTo("customerCode");
        assertThat(actual).extractingJsonPathStringValue("@.customer_name").isEqualTo("CustomerName");
        assertThat(actual).extractingJsonPathStringValue("@.sex").isEqualTo("Male");
        assertThat(actual).extractingJsonPathNumberValue("@.age").isEqualTo(24);
        assertThat(actual).extractingJsonPathStringValue("@.address").isEqualTo("Address");
    }
}
