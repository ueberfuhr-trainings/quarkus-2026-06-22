package de.schulung.customers.persistence;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class PanachePersistenceDeactivationProfile
  implements QuarkusTestProfile {

  @Override
  public Map<String, String> getConfigOverrides() {
    return Map.of("application.persistence.type", "memory");
  }
}
