package de.schulung.customers;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

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

  // Test: GET /customers/{uuid} für nicht-existenten Kunden -> 404

}
