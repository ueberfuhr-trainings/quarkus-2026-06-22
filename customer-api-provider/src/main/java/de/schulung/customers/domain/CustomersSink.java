package de.schulung.customers.domain;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface CustomersSink {

  Stream<Customer> findAll();

  default Stream<Customer> findByState(String state) {
    return findAll()
      .filter(customer -> state.equals(customer.getState()));
  }

  default Optional<Customer> findByUuid(UUID uuid) {
    return findAll()
      .filter(customer -> uuid.equals(customer.getUuid()))
      .findFirst();
  }

  void save(Customer customer);

  default boolean existsByUuid(UUID uuid) {
    return findByUuid(uuid)
      .isPresent();
  }

  void deleteByUuid(UUID uuid);

}
