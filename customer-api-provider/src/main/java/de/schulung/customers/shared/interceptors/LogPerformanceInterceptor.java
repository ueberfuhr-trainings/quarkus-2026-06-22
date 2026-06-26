package de.schulung.customers.shared.interceptors;

import io.quarkus.arc.log.LoggerName;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.jboss.logging.Logger;

@Interceptor
@LogPerformance
public class LogPerformanceInterceptor {

  @LoggerName("performance")
  Logger logger;

  @AroundInvoke
  public Object measureAndLogPerformance(InvocationContext invocationContext) throws Exception {
    final var startTime = System.currentTimeMillis();
    try {
      return invocationContext.proceed();
    } finally {
      final var endTime = System.currentTimeMillis();
      logger
        .infof(
          "Method %s took %d ms",
          invocationContext.getMethod().getName(),
          endTime - startTime
        );
    }
  }

}
