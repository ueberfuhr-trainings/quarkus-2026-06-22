package de.schulung.customers.domain;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@ApplicationScoped
public class CustomersService {

  private final CustomersSink sink;

  public CustomersService(CustomersSink sink) {
    this.sink = sink;
  }


  public Stream<Customer> getCustomers() {
    return sink.findAll();
  }

  public Stream<Customer> getCustomersByState(CustomerState state) {
    return sink.findByState(state);
  }

  public Optional<Customer> getCustomerByUuid(UUID uuid) {
    return sink.findByUuid(uuid);
  }


  public void createCustomer(@NotNull @Valid Customer customer) {
    // TODO Validation Groups für UUID Validierung
    sink.save(customer);
  }

  public boolean customerExists(UUID uuid) {
    return sink.existsByUuid(uuid);
  }

  public void deleteCustomer(UUID uuid) {
    sink.deleteByUuid(uuid);
  }


}
