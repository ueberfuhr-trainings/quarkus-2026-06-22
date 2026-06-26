package de.schulung.customers.infrastructure;

import de.schulung.customers.testing.CaptureOutput;
import de.schulung.customers.testing.CaptureOutput.CapturedOutput;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestProfile(EnableCustomerEventsLoggingProfile.class)
@CaptureOutput
class CustomersEventsLoggerTests {

  @Test
  void whenCreateCustomer_thenLogEvent(CapturedOutput output) {
    final var newCustomerUuid = given()
      .contentType(ContentType.JSON)
      .body("""
        {
          "name": "Tom Mayer",
          "birthdate": "2006-06-23",
          "state": "active"
        }
        """)
      .accept(ContentType.JSON)
      .when()
      .post("/customers")
      .then()
      .statusCode(201)
      .extract().path("uuid");

    // async: use Awaitility
    assertTrue(
      output.toString().matches("(?si).*Customer created.*" + newCustomerUuid + ".*"),
      """
        Expected log output to contain 'Customer created' with id "%s"
        """.formatted(newCustomerUuid)
    );

  }

}
