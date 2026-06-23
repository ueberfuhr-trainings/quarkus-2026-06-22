package de.schulung.customers.boundary;

import de.schulung.customers.boundary.validation.ValidCustomerState;
import de.schulung.customers.shared.validation.MinAge;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CustomerDto {

  @Setter(onMethod_ = @JsonbTransient)
  private UUID uuid;
  @NotNull
  @Size(min = 3, max = 100)
  private String name;
  @NotNull
  @MinAge
  private LocalDate birthdate;
  @ValidCustomerState
  private String state;

}
