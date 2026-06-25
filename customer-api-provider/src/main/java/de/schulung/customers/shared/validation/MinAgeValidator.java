package de.schulung.customers.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class MinAgeValidator
  implements ConstraintValidator<MinAge, LocalDate> {

  private MinAge constraintAnnotation;

  @Override
  public void initialize(MinAge constraintAnnotation) {
    this.constraintAnnotation = constraintAnnotation;
  }

  @Override
  public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
    return null == value ||
      LocalDate
        .now()
        .minus(constraintAnnotation.value(), constraintAnnotation.unit())
        .plusDays(1)
        .isAfter(value);
  }

}
