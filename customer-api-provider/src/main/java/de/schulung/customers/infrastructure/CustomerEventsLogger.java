package de.schulung.customers.infrastructure;

import de.schulung.customers.domain.events.CustomerCreatedEvent;
import io.quarkus.arc.log.LoggerName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CustomerEventsLogger {

  @LoggerName("customer-events")
  Logger log;

  public void logCustomerCreatedEvent(
    @Observes
    CustomerCreatedEvent event
  ) {
    log.infof("Customer created: %s", event.customer().getUuid());
  }

}
