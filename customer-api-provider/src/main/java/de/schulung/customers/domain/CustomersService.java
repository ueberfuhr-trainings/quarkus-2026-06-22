package de.schulung.customers.domain;

import de.schulung.customers.domain.events.CustomerCreatedEvent;
import de.schulung.customers.domain.events.CustomerDeletedEvent;
import de.schulung.customers.shared.interceptors.FireEvent;
import de.schulung.customers.shared.interceptors.LogPerformance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.jboss.logging.Logger.Level;

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


  @LogPerformance(Level.DEBUG)
  @FireEvent(CustomerCreatedEvent.class)
  public void createCustomer(@NotNull @Valid Customer customer) {
    // TODO Validation Groups für UUID Validierung
    sink.save(customer);
  }

  public boolean customerExists(UUID uuid) {
    return sink.existsByUuid(uuid);
  }

  @FireEvent(CustomerDeletedEvent.class)
  public void deleteCustomer(UUID uuid) {
    sink.deleteByUuid(uuid);
  }


}
