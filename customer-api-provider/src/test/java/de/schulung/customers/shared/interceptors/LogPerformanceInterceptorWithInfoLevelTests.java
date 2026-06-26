package de.schulung.customers.shared.interceptors;

import de.schulung.customers.testing.CaptureOutput;
import de.schulung.customers.testing.CaptureOutput.CapturedOutput;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the {@link LogPerformanceInterceptor} when the {@code log-performance}
 * category is explicitly pinned to {@code INFO} via {@link EnableLogPerformanceInfoProfile}.
 * <p>
 * This is the counterpart to {@link LogPerformanceInterceptorWithDebugLevelTests}: at
 * {@code INFO} level a performance message logged at {@code DEBUG} level (see
 * {@link LogPerformance#value()}) is filtered out and never reaches the output.
 */
@QuarkusTest
@TestProfile(EnableLogPerformanceInfoProfile.class)
@CaptureOutput
class LogPerformanceInterceptorWithInfoLevelTests {

  @Inject
  LogPerformanceTestBean bean;

  @Test
  void whenInfoLevel_thenDebugMessageFiltered(CapturedOutput output) {
    bean.doSomethingAtDebugLevel();

    assertThat(output.toString())
      .doesNotContain("Method doSomethingAtDebugLevel");
  }

}
