package de.schulung.customers.shared.interceptors;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

/**
 * Test profile that explicitly pins the log level of the {@code log-performance} category
 * to {@code INFO}.
 * <p>
 * Pinning the level is necessary because JUL logger levels are global, static state: a
 * previously running test (e.g. with {@link EnableLogPerformanceDebugProfile}) may have
 * raised the category to {@code DEBUG}, and that level is not reset to its default when
 * the Quarkus application restarts. Setting it explicitly makes the "DEBUG is filtered"
 * assertion deterministic regardless of test execution order.
 */
public class EnableLogPerformanceInfoProfile implements QuarkusTestProfile {

  @Override
  public Map<String, String> getConfigOverrides() {
    return Map.of(
      "quarkus.log.category.\"log-performance\".level", "INFO"
    );
  }

}
