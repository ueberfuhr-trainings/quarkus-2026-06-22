package de.schulung.customers.shared.interceptors;

import io.quarkus.arc.log.LoggerName;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.jboss.logging.Logger;

@LogPerformance
@Interceptor
public class LogPerformanceInterceptor {

  @LoggerName("log-performance")
  Logger logger;

  @AroundInvoke
  public Object intercept(InvocationContext ctx) throws Exception {
    final var logLevel = AnnotationUtils
      .findAnnotation(ctx.getMethod(), LogPerformance.class)
      .map(LogPerformance::value)
      .orElse(Logger.Level.INFO);
    long start = System.currentTimeMillis();
    try {
      return ctx.proceed();
    } finally {
      long end = System.currentTimeMillis();
      logger.logf(
        logLevel,
        "Method %s took %dms",
        ctx.getMethod().getName(),
        end - start
      );
    }
  }
}
