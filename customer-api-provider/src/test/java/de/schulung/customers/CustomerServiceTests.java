package de.schulung.customers;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class CustomerServiceTests {

  @Inject
  CustomersService customersService;

  @Test
  void given_invalid_customer_when_create_customer_then_throw_exception() {
    var customer = new Customer();
    // TODO AssertJ
    Assertions.assertThrows(
      Exception.class,
      () -> customersService.createCustomer(customer)
    );
  }

  @Test
  void given_null_customer_when_create_customer_then_throw_exception() {
    // TODO AssertJ
    Assertions.assertThrows(
      ValidationException.class,
      () -> customersService.createCustomer(null)
    );
  }


}
