package de.schulung.customers.persistence;

import de.schulung.customers.domain.Customer;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomerEntityMapper {

  public CustomerEntity map(Customer source) {
    return new CustomerEntity()
      .setUuid(source.getUuid())
      .setName(source.getName())
      .setBirthdate(source.getBirthdate())
      .setState(source.getState());
  }

  public Customer map(CustomerEntity source) {
    return new Customer()
      .setUuid(source.getUuid())
      .setName(source.getName())
      .setBirthdate(source.getBirthdate())
      .setState(source.getState());
  }

  public void copy(CustomerEntity source, Customer target) {
    target.setUuid(source.getUuid());
    target.setName(source.getName());
    target.setBirthdate(source.getBirthdate());
    target.setState(source.getState());
  }

}
