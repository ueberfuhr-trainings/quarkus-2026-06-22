package de.schulung.customers;


import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class OpenApiTests {

  @Test
  void when_get_openapi_then_return_yml() {
    given()
      .when()
      .get("/openapi.yml")
      .then()
      .statusCode(200)
      .contentType("application/x-yaml");
  }

}
