package de.schulung.customers.infrastructure;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class EnableCustomerEventsLoggingProfile implements QuarkusTestProfile {

  @Override
  public Map<String, String> getConfigOverrides() {
    return Map.of(
      "application.customer-events.logger.enabled", "true"
    );
  }

}
