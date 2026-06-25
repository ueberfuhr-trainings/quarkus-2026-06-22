package de.schulung.customers.persistence.inmemory;

import de.schulung.customers.domain.Customer;
import de.schulung.customers.domain.CustomersSink;
import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@DefaultBean
@ApplicationScoped
@Typed(CustomersSink.class)
public class CustomersSinkInMemoryImpl
  implements CustomersSink {

  private final Map<UUID, Customer> customers = new ConcurrentHashMap<>();

  @Override
  public Stream<Customer> findAll() {
    return customers
      .values()
      .stream();
  }

  @Override
  public Optional<Customer> findByUuid(UUID uuid) {
    return Optional
      .ofNullable(customers.get(uuid));
  }

  @Override
  public void save(Customer customer) {
    if (customer.getUuid() == null) {
      customer.setUuid(UUID.randomUUID());
    }
    customers.put(customer.getUuid(), customer);
  }

  @Override
  public boolean existsByUuid(UUID uuid) {
    return customers.containsKey(uuid);
  }

  @Override
  public void deleteByUuid(UUID uuid) {
    customers.remove(uuid);
  }
}
