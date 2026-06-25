package de.schulung.customers.persistence;

import de.schulung.customers.domain.CustomerState;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter(autoApply = true)
public class CustomerStateConverter
  implements AttributeConverter<CustomerState, String> {

  @Override
  public String convertToDatabaseColumn(CustomerState state) {
    return null == state ? null : switch (state) {
      case ACTIVE -> "active";
      case LOCKED -> "locked";
      case DISABLED -> "disabled";
    };
  }

  @Override
  public CustomerState convertToEntityAttribute(String state) {
    return null == state ? null : switch (state) {
      case "active" -> CustomerState.ACTIVE;
      case "locked" -> CustomerState.LOCKED;
      case "disabled" -> CustomerState.DISABLED;
      default -> throw new IllegalArgumentException();
    };
  }

}
