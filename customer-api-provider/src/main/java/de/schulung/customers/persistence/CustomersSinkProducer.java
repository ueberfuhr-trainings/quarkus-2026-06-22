package de.schulung.customers.persistence;

import de.schulung.customers.domain.CustomersSink;
import de.schulung.customers.persistence.inmemory.CustomersSinkInMemoryImpl;
import de.schulung.customers.persistence.panache.CustomerEntityMapper;
import de.schulung.customers.persistence.panache.CustomersRepository;
import de.schulung.customers.persistence.panache.CustomersSinkPanacheImpl;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

//@Dependent
public class CustomersSinkProducer {

  @Inject
  @ConfigProperty(name = "application.persistence.type")
  String persistenceType;

  @Inject
  CustomersRepository repo;
  @Inject
  CustomerEntityMapper mapper;

  //@Produces
  //@ApplicationScoped
  public CustomersSink customersSink() {
    if (null == persistenceType || "panache".equals(persistenceType)) {
      return new CustomersSinkPanacheImpl(repo, mapper);
    } else {
      return new CustomersSinkInMemoryImpl();
    }
  }

}
