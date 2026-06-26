package de.schulung.customers.testing;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.MessageFormat;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Annotation to enable capturing of output produced during test execution.
 * <p>
 * When applied to a test method or class, any output (such as log messages) produced
 * during the test's execution is captured, allowing it to be accessed and verified.
 * <p>
 * This annotation works in conjunction with the {@link OutputCaptureExtension}, which
 * handles the logic for capturing and providing access to the captured output.
 * <p>
 * The annotation can be applied at the test method or class level.
 * <p>
 * Target: {@link ElementType#METHOD}, {@link ElementType#TYPE}
 * Retention: {@link RetentionPolicy#RUNTIME}
 *
 * @see OutputCaptureExtension
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
  ElementType.METHOD,
  ElementType.TYPE
})
@ExtendWith(CaptureOutput.OutputCaptureExtension.class)
public @interface CaptureOutput {

  @SuppressWarnings("NullableProblems")
  record CapturedOutput(Supplier<String> output)
    implements CharSequence {

    @Override
    public int length() {
      return output.get().length();
    }

    @Override
    public char charAt(int index) {
      return output.get().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
      return output.get().subSequence(start, end);
    }

    @Override
    public String toString() {
      return output.get();
    }
  }

  class OutputCaptureExtension
    implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private Handler handler;
    private CapturedOutput capturedOutput;

    @Override
    public void beforeEach(
      @NonNull
      ExtensionContext context
    ) {

      final StringBuilder sb = new StringBuilder();
      this.handler = new Handler() {

        @Override
        public void publish(LogRecord record) {
          final var message = MessageFormat
            .format(
              record.getMessage(),
              record.getParameters()
            );
          sb.append(message);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }
      };
      this.capturedOutput = new CapturedOutput(sb::toString);

      Logger
        .getLogger("")
        .addHandler(this.handler);
    }

    @Override
    public void afterEach(
      @NonNull
      ExtensionContext context
    ) {
      Logger
        .getLogger("")
        .removeHandler(this.handler);
      this.handler = null;
      this.capturedOutput = null;
    }

    @Override
    public boolean supportsParameter(
      ParameterContext parameterContext,
      @NonNull
      ExtensionContext context
    ) {
      return parameterContext
        .getParameter()
        .getType()
        .equals(CapturedOutput.class);
    }

    @Override
    public Object resolveParameter(
      @NonNull
      ParameterContext parameterContext,
      @NonNull
      ExtensionContext context) {
      return this.capturedOutput;
    }

  }


}
