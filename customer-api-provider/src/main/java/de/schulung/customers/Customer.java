package de.schulung.customers;

import java.time.LocalDate;
import java.util.UUID;

public class Customer {

  private UUID uuid;
  private String name;
  private LocalDate birthdate;
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
