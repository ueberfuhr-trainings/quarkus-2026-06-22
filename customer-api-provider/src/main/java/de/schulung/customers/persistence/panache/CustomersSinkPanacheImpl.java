package de.schulung.customers.persistence.panache;

import de.schulung.customers.domain.Customer;
import de.schulung.customers.domain.CustomerState;
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
  private final CustomerEntityMapper mapper;

  public CustomersSinkPanacheImpl(CustomersRepository repo, CustomerEntityMapper mapper) {
    this.repo = repo;
    this.mapper = mapper;
  }


  @Override
  public Stream<Customer> findAll() {
    return repo
      .findAll()
      .stream()
      .map(mapper::map);
  }

  @Override
  public Stream<Customer> findByState(CustomerState state) {
    return repo
      .findAllByState(state)
      .stream()
      .map(mapper::map);
  }

  @Override
  public Optional<Customer> findByUuid(UUID uuid) {
    return repo
      .findByIdOptional(uuid)
      .map(mapper::map);
  }

  @Transactional
  @Override
  public void save(Customer customer) {
    final var entity = mapper.map(customer);
    repo.persist(entity);
    mapper.copy(entity, customer);
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
