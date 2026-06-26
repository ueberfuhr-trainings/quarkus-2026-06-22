package de.schulung.customers.infrastructure;

import de.schulung.customers.testing.CaptureOutput;
import de.schulung.customers.testing.CaptureOutput.CapturedOutput;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import static de.schulung.customers.testing.CustomersApiHelper.aCustomer;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestProfile(EnableCustomerEventsLoggingProfile.class)
@CaptureOutput
class CustomersEventsLoggerTests {

  @Test
  void whenCreateCustomer_thenLogEvent(CapturedOutput output) {
    final var newCustomer = aCustomer()
      .create()
      .andReturn();

    // async: use Awaitility
    assertThat(output.toString())
      .as(
        """
          Expected log output to contain 'Customer created' with id "%s"
          """.formatted(newCustomer.id())
      )
      .matches("(?si).*Customer created.*" + newCustomer.id() + ".*");

  }

}
