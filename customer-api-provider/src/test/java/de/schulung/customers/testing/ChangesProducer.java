package de.schulung.customers.testing;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import org.assertj.db.type.AssertDbConnectionFactory;
import org.assertj.db.type.Changes;

import javax.sql.DataSource;
import java.util.stream.Stream;

@Dependent
public class ChangesProducer {

  @Produces
  @Dependent
  @TableChanges
  public Changes produceChanges(
    DataSource dataSource,
    InjectionPoint injectionPoint
  ) {

    var changes = AssertDbConnectionFactory
      .of(dataSource)
      .create()
      .changes();

    Stream.of(
        injectionPoint
          .getAnnotated()
          .getAnnotation(TableChanges.class)
          .value()
      )
      .forEach(changes::table);

    return changes.build();
  }
}
