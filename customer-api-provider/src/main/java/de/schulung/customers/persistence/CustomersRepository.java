package de.schulung.customers.persistence;

import de.schulung.customers.domain.CustomerState;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CustomersRepository
  implements PanacheRepositoryBase<CustomerEntity, UUID> {

  public List<CustomerEntity> findAllByState(CustomerState state) {
    return list("state", state);
  }

  public boolean existsByUuid(UUID uuid) {
    return count("uuid", uuid) > 0;
  }

}
