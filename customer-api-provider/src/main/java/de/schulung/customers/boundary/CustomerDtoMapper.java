package de.schulung.customers.boundary;

import de.schulung.customers.domain.Customer;
import de.schulung.customers.domain.CustomerState;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

@ApplicationScoped
public class CustomerDtoMapper {

  public Customer map(CustomerDto source) {
    return new Customer()
      .setUuid(source.getUuid())
      .setName(source.getName())
      .setBirthdate(source.getBirthdate())
      .setState(mapState(source.getState()));
  }

  public CustomerState mapState(String state) {
    return null == state ? null : switch (state) {
      case "active" -> CustomerState.ACTIVE;
      case "locked" -> CustomerState.LOCKED;
      case "disabled" -> CustomerState.DISABLED;
      default -> throw new BadRequestException();
    };
  }


  public CustomerDto map(Customer source) {
    return new CustomerDto()
      .setUuid(source.getUuid())
      .setName(source.getName())
      .setBirthdate(source.getBirthdate())
      .setState(source.getState().name().toLowerCase());
  }

  public String mapState(CustomerState state) {
    return null == state ? null : switch (state) {
      case ACTIVE -> "active";
      case LOCKED -> "locked";
      case DISABLED -> "disabled";
    };
  }

}
