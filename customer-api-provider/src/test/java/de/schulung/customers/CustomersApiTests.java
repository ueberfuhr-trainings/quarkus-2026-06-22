package de.schulung.customers;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
public class CustomersApiTests {

  // GET /customers + Accept: application/json -> 200 + JSON-Array
  @Test
  void when_get_customers_then_return_json_array() {
    given()
      .accept(ContentType.JSON)
      .when()
      .get("/customers")
      .then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("", is(instanceOf(List.class)));
  }

  // GET /customers + Accept: application/xml -> 406
  @Test
  void when_get_customers_as_xml_then_deny() {
    given()
      .accept(ContentType.XML)
      .when()
      .get("/customers")
      .then()
      .statusCode(406);
  }

  // POST /customers mit JSON -> 201 + Customer als JSON mit UUID
  @Test
  void when_post_customers_then_return_created_and_customer_with_uuid() {
    given()
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
      .contentType(ContentType.JSON)
      .body("uuid", is(instanceOf(String.class)))
      .body("name", is(equalTo("Tom Mayer")))
      .body("birthdate", is(equalTo("2006-06-23")))
      .body("state", is(equalTo("active")));
  }

  // POST /customers mit XML -> 415
  @Test
  void when_post_customers_with_xml_then_return_unsupported_mediatype() {
    given()
      .contentType(ContentType.XML)
      .body("<customer />")
      .accept(ContentType.JSON)
      .when()
      .post("/customers")
      .then()
      .statusCode(415);
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
  }

  // Setup: POST /customers mit Customer als JSON -> 201
  // Test: GET /customers -> 200 + Customer im Array
  @Test
  void given_created_customer_when_get_customers_then_customer_is_in_array() {
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

    given()
      .accept(ContentType.JSON)
      .when()
      .get("/customers")
      .then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      // see https://github.com/rest-assured/rest-assured/wiki/usage#json-example
      .body(
        "find { it.uuid == '%s' }".formatted(newCustomerUuid),
        allOf(
          hasEntry("name", "Tom Mayer"),
          hasEntry("birthdate", "2006-06-23"),
          hasEntry("state", "active")
        )
      );

  }

  // Setup: POST /customers mit Customer als JSON -> 201 + UUID
  // Test: GET /customers/{uuid} -> 200 + Customer
  @Test
  void given_created_customer_when_get_customer_by_uuid_then_customer_is_returned() {
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

    given()
      .accept(ContentType.JSON)
      .pathParam("uuid", newCustomerUuid)
      .when()
      .get("/customers/{uuid}")
      .then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("uuid", is(equalTo(newCustomerUuid)))
      .body("name", is(equalTo("Tom Mayer")))
      .body("birthdate", is(equalTo("2006-06-23")))
      .body("state", is(equalTo("active")));

  }

  @Test
  void given_created_customer_when_get_customer_by_uuid_as_xml_then_not_acceptable() {
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

    given()
      .accept(ContentType.XML)
      .pathParam("uuid", newCustomerUuid)
      .when()
      .get("/customers/{uuid}")
      .then()
      .statusCode(406);

  }

  // Setup: POST /customers mit Customer als JSON -> 201 + Location-Header
  // Test: GET {location} -> 200 + Customer
  @Test
  void given_created_customer_when_get_customer_by_location_header_then_return_customer() {
    final var location = given()
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
      .header(HttpHeaders.LOCATION, is(instanceOf(String.class)))
      .extract().header(HttpHeaders.LOCATION);

    given()
      .accept(ContentType.JSON)
      .baseUri(location)
      .when()
      .get()
      .then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("name", is(equalTo("Tom Mayer")))
      .body("birthdate", is(equalTo("2006-06-23")))
      .body("state", is(equalTo("active")));

  }

  // Setup: POST /customers mit Customer als JSON -> 201 + UUID
  // Setup: DELETE /customers/{uuid} -> 204
  // Test: GET /customers/{uuid} -> 404
  @Test
  void when_get_customer_by_uuid_not_existing_then_return_not_found() {
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

    given()
      .pathParam("uuid", newCustomerUuid)
      .when()
      .delete("/customers/{uuid}")
      .then()
      .statusCode(204);

    // Test
    given()
      .accept(ContentType.JSON)
      .pathParam("uuid", newCustomerUuid)
      .when()
      .get("/customers/{uuid}")
      .then()
      .statusCode(404);
  }

  // Setup: POST /customers mit Customer als JSON -> 201 + UUID
  // Test: DELETE /customers/{uuid} -> 204 (=Setup für weitere Tests)
  // Test: DELETE /customers/{uuid} -> 404
  @Test
  void given_created_customer_when_delete_customer_then_return_no_content() {
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

    given()
      .pathParam("uuid", newCustomerUuid)
      .when()
      .delete("/customers/{uuid}")
      .then()
      .statusCode(204);

    // try to delete again
    given()
      .accept(ContentType.JSON)
      .pathParam("uuid", newCustomerUuid)
      .when()
      .delete("/customers/{uuid}")
      .then()
      .statusCode(404);
  }

  // Setup: POST /customers mit Customer als JSON -> 201 + UUID
  // Setup: DELETE /customers/{uuid} -> 204
  // Test: GET /customers -> 200 mit Array ohne diesen Kunden
  @Test
  void given_deleted_customer_when_get_customers_then_return_array_without_customer() {
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

    given()
      .pathParam("uuid", newCustomerUuid)
      .when()
      .delete("/customers/{uuid}")
      .then()
      .statusCode(204);

    // Test
    given()
      .accept(ContentType.JSON)
      .when()
      .get("/customers")
      .then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      // see https://github.com/rest-assured/rest-assured/wiki/usage#json-example
      .body(
        "find { it.uuid == '%s' }".formatted(newCustomerUuid),
        is(nullValue())
      );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // invalid birthdate format
    """
      {
        "name": "Tom Mayer",
        "birthdate": "gelbekatze",
        "state": "active"
      }
      """,
    // missing name
    """
      {
        "birthdate": "2001-04-23",
        "state": "active"
      }
      """,
    // missing birthdate
    """
      {
        "name": "Tom Mayer",
        "state": "active"
      }
      """,
    // name with less than 3 characters
    """
      {
        "name": "T",
        "birthdate": "2001-04-23",
        "state": "active"
      }
      """,
    // name with more than 100 characters
    """
      {
        "name": "T0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789",
        "birthdate": "2001-04-23",
        "state": "active"
      }
      """,
    // invalid state
    """
      {
        "name": "Tom Mayer",
        "birthdate": "2001-04-23",
        "state": "gelbekatze"
      }
      """,
    // UUID included
    """
      {
        "uuid": "3f8a1513-3061-4bf7-bb48-a2979a529ff5",
        "name": "Tom Mayer",
        "birthdate": "2001-04-23",
        "state": "active"
      }
      """,
    // unknown property
    """
      {
        "name": "Tom Mayer",
        "birthdate": "2001-04-23",
        "state": "active",
        "gelbekatze": "gruenerfuchs"
      }
      """,

  })
  void given_invalid_customer_when_post_customers_then_bad_request(String body) {
    given()
      .contentType(ContentType.JSON)
      .body(body)
      .accept(ContentType.JSON)
      .when()
      .post("/customers")
      .then()
      .statusCode(400);
  }

  @Test
  void given_invalid_state_parameter_when_get_customers_then_status_bad_request() {
    given()
      .accept(ContentType.JSON)
      .queryParam("state", "gelbekatze")
      .when()
      .get("/customers")
      .then()
      .statusCode(400);
  }

  @Test
  void given_created_customer_with_active_state_when_get_customers_with_locked_state_then_customer_is_not_in_array() {
    // setup
    var newCustomerUuid = given()
      .contentType(ContentType.JSON)
      .body("""
            {
              "name": "Tom Mayer",
              "birthdate": "2000-05-19",
              "state": "active"
            }
        """)
      .accept(ContentType.JSON)
      .when()
      .post("/customers")
      .then()
      .statusCode(201)
      .contentType(ContentType.JSON)
      .body("uuid", is(instanceOf(String.class)))
      .extract().path("uuid");

    // test
    given()
      .accept(ContentType.JSON)
      .queryParam("state", "locked")
      .when()
      .get("/customers")
      .then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      // see https://github.com/rest-assured/rest-assured/wiki/usage#json-example
      .body("find { it.uuid == '%s' }".formatted(newCustomerUuid), is(nullValue()));

  }

}
