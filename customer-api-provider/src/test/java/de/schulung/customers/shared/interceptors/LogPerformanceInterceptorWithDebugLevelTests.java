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
 * category is explicitly configured to {@code DEBUG} via
 * {@link EnableLogPerformanceDebugProfile}.
 * <p>
 * This is the counterpart to
 * {@link LogPerformanceInterceptorTests#whenMethodLogsAtDebugLevel_thenMessageFilteredAtDefaultLevel}:
 * once the level is raised, the {@code DEBUG} performance message is emitted and can be
 * captured.
 */
@QuarkusTest
@TestProfile(EnableLogPerformanceDebugProfile.class)
@CaptureOutput
class LogPerformanceInterceptorWithDebugLevelTests {

  @Inject
  LogPerformanceTestBean bean;

  @Test
  void whenDebugLevelEnabled_thenDebugMessageAppears(CapturedOutput output) {
    bean.doSomethingAtDebugLevel();

    assertThat(output.toString())
      .matches("(?si).*Method doSomethingAtDebugLevel took \\d+ms.*");
  }

}
