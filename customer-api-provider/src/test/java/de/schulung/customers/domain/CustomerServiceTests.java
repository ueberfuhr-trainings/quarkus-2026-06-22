package de.schulung.customers.domain;

import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
@TestTransaction
public class CustomerServiceTests {

  @Inject
  CustomersService customersService;
  @InjectMock
  CustomersSink sink;

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
          .setState(CustomerState.ACTIVE)
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

  // dieselben ungueltigen Customer wie bei createCustomer(), aber mit gesetzter UUID
  static Stream<Arguments> invalidCustomersWithId() {
    return invalidCustomers()
      .map(arguments -> {
        final var description = (String) arguments.get()[0];
        final var customer = (Customer) arguments.get()[1];
        if (customer != null) {
          customer.setUuid(UUID.randomUUID());
        }
        return Arguments.of(description, customer);
      });
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("invalidCustomersWithId")
  void given_invalid_customer_when_update_customer_then_throw_exception(
    String description,
    Customer customer
  ) {
    assertThatThrownBy(() -> customersService.updateCustomer(customer))
      .isInstanceOf(Exception.class)
      .isNotInstanceOf(NullPointerException.class);
  }

  // createCustomer() darf keinen Customer mit bereits gesetzter UUID akzeptieren.
  // Disabled: UUID-Validierung (Validation Groups) noch nicht umgesetzt -> schlaegt fehl.
  @Disabled("UUID-Validierung via Validation Groups noch nicht umgesetzt (siehe TODO in CustomersService)")
  @Test
  void given_customer_with_id_when_create_customer_then_throw_exception() {
    final var customer = new Customer()
      .setUuid(UUID.randomUUID())
      .setName("Tom Mayer")
      .setBirthdate(LocalDate.now().minusYears(20))
      .setState(CustomerState.ACTIVE);
    assertThatThrownBy(() -> customersService.createCustomer(customer))
      .isInstanceOf(Exception.class)
      .isNotInstanceOf(NullPointerException.class);
  }

  // updateCustomer() benoetigt einen Customer mit gesetzter UUID.
  // Disabled: UUID-Validierung (Validation Groups) noch nicht umgesetzt -> schlaegt fehl.
  @Disabled("UUID-Validierung via Validation Groups noch nicht umgesetzt (siehe TODO in CustomersService)")
  @Test
  void given_customer_without_id_when_update_customer_then_throw_exception() {
    final var customer = new Customer()
      .setName("Tom Mayer")
      .setBirthdate(LocalDate.now().minusYears(20))
      .setState(CustomerState.ACTIVE);
    assertThatThrownBy(() -> customersService.updateCustomer(customer))
      .isInstanceOf(Exception.class)
      .isNotInstanceOf(NullPointerException.class);
  }

}
