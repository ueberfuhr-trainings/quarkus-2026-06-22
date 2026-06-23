package de.schulung.customers;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@QuarkusTest
public class CustomersApiWithMockedServiceTests {

  @InjectMock
  CustomersService customersService;

  // Setup: CustomerService tut so, als ob es den Customer nicht gibt
  // Test: GET /customers/{uuid} -> 404
  @Test
  void when_get_customer_by_uuid_not_existing_then_return_not_found() {
    final var newCustomerUuid = UUID.randomUUID();

    when(customersService.getCustomerByUuid(newCustomerUuid))
      .thenReturn(Optional.empty());

    // Test
    given()
      .accept(ContentType.JSON)
      .pathParam("uuid", newCustomerUuid)
      .when()
      .get("/customers/{uuid}")
      .then()
      .statusCode(404);
  }

  // POST /customers mit Accept: application/xml -> 406
  @Test
  void when_post_customers_with_invalid_accept_then_return_not_acceptable() {
    given()
      .contentType(ContentType.JSON)
      .body("""
        {
          "name": "Tom Mayer",
          "birthdate": "2006-06-23",
          "state": "active"
        }
        """)
      .accept(ContentType.XML)
      .when()
      .post("/customers")
      .then()
      .statusCode(406);

    verifyNoInteractions(customersService);

  }

}
