package de.schulung.customers.domain;

import de.schulung.customers.shared.validation.MinAge;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class Customer {

  @NotNull(groups = {ValidationGroups.Update.class})
  @Null(groups = {ValidationGroups.Create.class})
  private UUID uuid;
  @NotNull
  @Size(min = 3, max = 100)
  private String name;
  @NotNull
  @MinAge
  private LocalDate birthdate;
  private CustomerState state;

}
