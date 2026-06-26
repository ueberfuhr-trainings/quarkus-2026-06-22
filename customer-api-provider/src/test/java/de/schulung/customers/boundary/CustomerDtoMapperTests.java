package de.schulung.customers.boundary;

import de.schulung.customers.domain.Customer;
import de.schulung.customers.domain.CustomerState;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
public class CustomerDtoMapperTests {

  @Inject
  CustomerDtoMapper mapper;

  @Test
  void when_map_null_to_customer_then_return_null() {
    assertThat(mapper.map((CustomerDto) null))
      .isNull();
  }

  @Test
  void when_map_null_string_to_customer_state_then_return_null() {
    assertThat(mapper.mapState((String) null))
      .isNull();
  }

  @Test
  void when_map_invalid_string_to_customer_state_then_throw_exception() {
    assertThatThrownBy(() -> mapper.mapState("gelbekatze"))
      .isNotNull();
  }

  @Test
  void when_map_null_to_customer_dto_then_return_null() {
    assertThat(mapper.map((Customer) null))
      .isNull();
  }

  @Test
  void when_map_null_state_to_string_then_return_null() {
    assertThat(mapper.mapState((CustomerState) null))
      .isNull();
  }

}
