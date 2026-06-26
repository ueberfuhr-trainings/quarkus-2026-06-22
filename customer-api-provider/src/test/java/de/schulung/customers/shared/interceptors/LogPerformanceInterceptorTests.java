package de.schulung.customers.shared.interceptors;

import de.schulung.customers.testing.CaptureOutput;
import de.schulung.customers.testing.CaptureOutput.CapturedOutput;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for the {@link LogPerformanceInterceptor}.
 * <p>
 * The approach: a dedicated test-only CDI bean ({@link LogPerformanceTestBean}) is
 * annotated with {@link LogPerformance}. When its methods are invoked, the interceptor
 * is applied and writes a performance log message. We capture the console/log output
 * via {@link CaptureOutput} and verify that the expected message appears.
 */
@QuarkusTest
@CaptureOutput
class LogPerformanceInterceptorTests {

  @Inject
  LogPerformanceTestBean bean;

  @Test
  void whenAnnotatedMethodCalled_thenLogsMethodNameAndDuration(CapturedOutput output) {
    bean.greet("Tom");

    assertThat(output.toString())
      .matches("(?si).*Method greet took \\d+ms.*");
  }

  @Test
  void whenAnnotatedMethodThrows_thenStillLogsPerformance(CapturedOutput output) {
    assertThatThrownBy(() -> bean.fail())
      .isInstanceOf(IllegalStateException.class);

    // the interceptor logs in a finally block, so the measurement happens even on error
    assertThat(output.toString())
      .matches("(?si).*Method fail took \\d+ms.*");
  }

  @Test
  void whenMethodLogsAtWarnLevel_thenMessageAppears(CapturedOutput output) {
    bean.doSomethingAtWarnLevel();

    assertThat(output.toString())
      .matches("(?si).*Method doSomethingAtWarnLevel took \\d+ms.*");
  }

  @Test
  void whenMethodLogsAtDebugLevel_thenMessageFilteredAtDefaultLevel(CapturedOutput output) {
    bean.doSomethingAtDebugLevel();

    // the default log level is INFO, so the DEBUG message of the interceptor is suppressed
    assertThat(output.toString())
      .doesNotContain("Method doSomethingAtDebugLevel");
  }

  @Test
  void whenMethodNotAnnotated_thenNoPerformanceLog(CapturedOutput output) {
    bean.notMeasured();

    assertThat(output.toString())
      .doesNotContain("Method notMeasured");
  }

}
