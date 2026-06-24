package de.schulung.customers;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class MinAgeValidator
  implements ConstraintValidator<MinAge, LocalDate> {

  @Override
  public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
    // TODO implement
    return false;
  }

}
