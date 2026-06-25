package de.schulung.customers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@ApplicationScoped
public class CustomersService {

  private final CustomersRepository repo;

  public CustomersService(CustomersRepository repo) {
    this.repo = repo;
  }

  public Stream<Customer> getCustomers() {
    return repo
      .findAll()
      .stream();
  }

  public Stream<Customer> getCustomersByState(String state) {
    return repo
      .findAllByState(state)
      .stream();
  }

  public Optional<Customer> getCustomerByUuid(UUID uuid) {
    return repo
      .findByIdOptional(uuid);
  }

  @Transactional
  public void createCustomer(@NotNull @Valid Customer customer) {
    repo.persist(customer);
  }

  public boolean customerExists(UUID uuid) {
    return repo.existsByUuid(uuid);
  }

  @Transactional
  public void deleteCustomer(UUID uuid) {
    repo.deleteById(uuid);
  }


}
