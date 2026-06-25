package de.schulung.customers.persistence;

import de.schulung.customers.domain.Customer;
import de.schulung.customers.domain.CustomersSink;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@ApplicationScoped
@Typed(CustomersSink.class)
public class CustomersSinkPanacheImpl
  implements CustomersSink {

  private final CustomersRepository repo;

  public CustomersSinkPanacheImpl(CustomersRepository repo) {
    this.repo = repo;
  }

  @Override
  public Stream<Customer> findAll() {
    return repo
      .findAll()
      .stream();
  }

  @Override
  public Stream<Customer> findByState(String state) {
    return repo
      .findAllByState(state)
      .stream();
  }

  @Override
  public Optional<Customer> findByUuid(UUID uuid) {
    return repo
      .findByIdOptional(uuid);
  }

  @Transactional
  @Override
  public void save(Customer customer) {
    repo.persist(customer);
  }

  @Override
  public boolean existsByUuid(UUID uuid) {
    return repo.existsByUuid(uuid);
  }

  @Transactional
  @Override
  public void deleteByUuid(UUID uuid) {
    repo.deleteById(uuid);
  }
}
