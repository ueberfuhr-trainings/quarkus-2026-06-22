package de.schulung.customers.domain;

import de.schulung.customers.domain.events.CustomerCreatedEvent;
import de.schulung.customers.domain.events.CustomerDeletedEvent;
import de.schulung.customers.domain.events.CustomerReplacedEvent;
import de.schulung.customers.shared.interceptors.FireEvent;
import de.schulung.customers.shared.interceptors.LogPerformance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.ConvertGroup;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger.Level;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@RequiredArgsConstructor
@ApplicationScoped
public class CustomersService {

  private final CustomersSink sink;

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
  public void createCustomer(
    @NotNull
    @Valid
    @ConvertGroup(to = ValidationGroups.Create.class)
    Customer customer
  ) {
    sink.save(customer);
  }

  @FireEvent(CustomerReplacedEvent.class)
  public void updateCustomer(
    @NotNull
    @Valid
    @ConvertGroup(to = ValidationGroups.Update.class)
    Customer customer
  ) {
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
