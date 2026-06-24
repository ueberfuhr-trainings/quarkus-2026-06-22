package de.schulung.customers;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public class Customer {

  private UUID uuid;
  @NotNull
  @Size(min = 3, max = 100)
  private String name;
  @NotNull
  private LocalDate birthdate;
  @Pattern(regexp = "active|locked|disabled")
  private String state;

  public UUID getUuid() {
    return uuid;
  }

  public Customer setUuid(UUID uuid) {
    this.uuid = uuid;
    return this;
  }

  public String getName() {
    return name;
  }

  public Customer setName(String name) {
    this.name = name;
    return this;
  }

  public LocalDate getBirthdate() {
    return birthdate;
  }

  public Customer setBirthdate(LocalDate birthdate) {
    this.birthdate = birthdate;
    return this;
  }

  public String getState() {
    return state;
  }

  public Customer setState(String state) {
    this.state = state;
    return this;
  }
}
