package com.example.study.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class CustomerConstrainTest {
    final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * Test not null constraints
     */
    @Test
    public void testNotNullConstraints() {
        // setup
        Customer request = new Customer();

        // exercise
        Set<ConstraintViolation<Customer>> actual = validator.validate(request);
        // verify
        Set<String> errorExpression = actual.stream()
                .map(violation -> violation.getPropertyPath().toString() + " " + violation.getMessage())
                .collect(Collectors.toSet());
        errorExpression.forEach(log::info);
        assertThat(errorExpression).containsExactlyInAnyOrder(
                "customerId must not be null",
                "customerCode must not be null");
        assertThat(actual).hasSize(2);
    }
}
