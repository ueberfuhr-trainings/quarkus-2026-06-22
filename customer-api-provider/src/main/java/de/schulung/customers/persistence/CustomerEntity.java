package de.schulung.customers.persistence;

import de.schulung.customers.domain.CustomerState;
import de.schulung.customers.shared.validation.MinAge;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

@Entity(name = "Customer") // JPQL: SELECT c FROM Customer c
@Table(name = "customers")
public class CustomerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID uuid;
  @NotNull
  @Size(min = 3, max = 100)
  private String name;
  @NotNull
  @MinAge
  @Column(name = "day_of_birth")
  private LocalDate birthdate;
  private CustomerState state;

  public UUID getUuid() {
    return uuid;
  }

  public CustomerEntity setUuid(UUID uuid) {
    this.uuid = uuid;
    return this;
  }

  public String getName() {
    return name;
  }

  public CustomerEntity setName(String name) {
    this.name = name;
    return this;
  }

  public LocalDate getBirthdate() {
    return birthdate;
  }

  public CustomerEntity setBirthdate(LocalDate birthdate) {
    this.birthdate = birthdate;
    return this;
  }

  public CustomerState getState() {
    return state;
  }

  public CustomerEntity setState(CustomerState state) {
    this.state = state;
    return this;
  }
}
