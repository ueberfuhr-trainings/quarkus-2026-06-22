package de.schulung.customers.shared.interceptors;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

/**
 * Test profile that raises the log level of the {@code log-performance} category to
 * {@code DEBUG}, so that performance messages logged at {@code DEBUG} level (see
 * {@link LogPerformance#value()}) are actually emitted instead of being filtered out
 * by the default {@code INFO} level.
 */
public class EnableLogPerformanceDebugProfile implements QuarkusTestProfile {

  @Override
  public Map<String, String> getConfigOverrides() {
    return Map.of(
      "quarkus.log.category.\"log-performance\".level", "DEBUG"
    );
  }

}
