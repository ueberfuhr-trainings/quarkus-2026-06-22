package de.schulung.customers.testing;

import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({
  ElementType.FIELD,
  ElementType.PARAMETER,
  ElementType.METHOD,
  ElementType.TYPE
})
public @interface TableChanges {

  @Nonbinding
  String[] value() default {};
}
