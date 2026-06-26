package de.schulung.customers.shared.interceptors;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

/**
 * Test-only CDI bean whose methods are annotated with {@link LogPerformance}
 * so that the {@link LogPerformanceInterceptor} is applied when they are called.
 */
@ApplicationScoped
public class LogPerformanceTestBean {

  @LogPerformance
  public String greet(String name) {
    return "Hello " + name;
  }

  @LogPerformance(Logger.Level.WARN)
  public void doSomethingAtWarnLevel() {
    // nothing to do, the interceptor logs the performance
  }

  @LogPerformance(Logger.Level.DEBUG)
  public void doSomethingAtDebugLevel() {
    // nothing to do, the interceptor logs the performance
  }

  @LogPerformance
  public void fail() {
    throw new IllegalStateException("boom");
  }

  public String notMeasured() {
    return "not measured";
  }

}
