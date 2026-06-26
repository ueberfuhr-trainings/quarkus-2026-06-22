package de.schulung.customers.domain;

import de.schulung.customers.domain.events.CustomerCreatedEvent;
import de.schulung.customers.domain.events.CustomerDeletedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@ApplicationScoped
public class CustomersService {

  private final CustomersSink sink;
  private final Event<Object> customerEventPublisher;

  public CustomersService(CustomersSink sink, Event<Object> customerEventPublisher) {
    this.sink = sink;
    this.customerEventPublisher = customerEventPublisher;
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
    customerEventPublisher.fire(new CustomerCreatedEvent(customer));
  }

  public boolean customerExists(UUID uuid) {
    return sink.existsByUuid(uuid);
  }

  public void deleteCustomer(UUID uuid) {
    sink.deleteByUuid(uuid);
    customerEventPublisher.fire(new CustomerDeletedEvent(uuid));
  }


}
