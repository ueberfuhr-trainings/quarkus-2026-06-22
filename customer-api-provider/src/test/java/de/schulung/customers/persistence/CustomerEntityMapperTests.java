package de.schulung.customers.persistence;

import de.schulung.customers.domain.Customer;
import de.schulung.customers.persistence.panache.CustomerEntity;
import de.schulung.customers.persistence.panache.CustomerEntityMapper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class CustomerEntityMapperTests {

  @Inject
  CustomerEntityMapper mapper;

  @Test
  void when_map_null_to_customer_then_return_null() {
    assertThat(mapper.map((CustomerEntity) null))
      .isNull();
  }

  @Test
  void when_map_null_to_customer_entity_then_return_null() {
    assertThat(mapper.map((Customer) null))
      .isNull();
  }

}
