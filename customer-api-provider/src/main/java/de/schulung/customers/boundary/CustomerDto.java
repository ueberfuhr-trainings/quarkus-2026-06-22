package de.schulung.customers.boundary;

import de.schulung.customers.boundary.validation.ValidCustomerState;
import de.schulung.customers.shared.validation.MinAge;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public class CustomerDto {

  private UUID uuid;
  @NotNull
  @Size(min = 3, max = 100)
  private String name;
  @NotNull
  @MinAge
  private LocalDate birthdate;
  @ValidCustomerState
  private String state;

  public UUID getUuid() {
    return uuid;
  }

  @JsonbTransient
  public CustomerDto setUuid(UUID uuid) {
    this.uuid = uuid;
    return this;
  }

  public String getName() {
    return name;
  }

  public CustomerDto setName(String name) {
    this.name = name;
    return this;
  }

  public LocalDate getBirthdate() {
    return birthdate;
  }

  public CustomerDto setBirthdate(LocalDate birthdate) {
    this.birthdate = birthdate;
    return this;
  }

  public String getState() {
    return state;
  }

  public CustomerDto setState(String state) {
    this.state = state;
    return this;
  }
}
