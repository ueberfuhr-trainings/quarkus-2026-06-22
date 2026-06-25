package de.schulung.customers.domain;

import de.schulung.customers.persistence.Customer;
import de.schulung.customers.persistence.CustomersRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
@TestTransaction
public class CustomerServiceTests {

  @Inject
  CustomersService customersService;
  @InjectMock
  CustomersRepository repo;

  static Stream<Arguments> invalidCustomers() {
    return Stream.of(
      Arguments.of(
        "empty customer",
        new Customer()
      ),
      Arguments.of(
        "null customer",
        null
      ),
      Arguments.of(
        "customer without birthdate",
        new Customer()
          .setName("Tom Mayer")
          .setState("active")
      )
    );
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("invalidCustomers")
  void given_invalid_customer_when_create_customer_then_throw_exception(
    String description,
    Customer customer
  ) {
    assertThatThrownBy(() -> customersService.createCustomer(customer))
      .isInstanceOf(Exception.class)
      .isNotInstanceOf(NullPointerException.class);
  }

}
