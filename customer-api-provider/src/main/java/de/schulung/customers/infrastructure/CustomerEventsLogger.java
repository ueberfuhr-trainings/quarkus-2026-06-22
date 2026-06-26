package de.schulung.customers.infrastructure;

import de.schulung.customers.domain.events.CustomerCreatedEvent;
import de.schulung.customers.domain.events.CustomerDeletedEvent;
import de.schulung.customers.domain.events.CustomerReplacedEvent;
import io.quarkus.arc.log.LoggerName;
import io.quarkus.arc.properties.IfBuildProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.jboss.logging.Logger;

@ApplicationScoped
@IfBuildProperty(
  name = "application.customer-events.logger.enabled",
  stringValue = "true"
)
public class CustomerEventsLogger {

  @LoggerName("customer-events")
  Logger log;

  public void logCustomerCreatedEvent(
    @Observes
    CustomerCreatedEvent event
  ) {
    log.infof("Customer created: %s", event.customer().getUuid());
  }

  public void logCustomerReplacedEvent(
    @Observes
    CustomerReplacedEvent event
  ) {
    log.infof("Customer replaced: %s", event.customer().getUuid());
  }

  public void logCustomerDeletedEvent(
    @Observes
    CustomerDeletedEvent event
  ) {
    log.infof("Customer deleted: %s", event.uuid());
  }

}
