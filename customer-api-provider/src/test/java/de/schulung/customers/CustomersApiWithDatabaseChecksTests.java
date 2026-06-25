package de.schulung.customers;

import de.schulung.customers.testing.TableChanges;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.assertj.db.type.Changes;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestTransaction
class CustomersApiWithDatabaseChecksTests {

  @Inject
  @TableChanges("customers")
  Changes changes;

  // POST /customers -> 201 + neuer Datensatz
  @Test
  void when_post_customers_then_status_created_and_inserted_into_database() {
    changes.setStartPointNow();

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
      .extract().path("uuid");

    changes.setEndPointNow();

    assertThat(changes)
      .hasNumberOfChanges(1)
      .change().isCreation()
      .rowAtEndPoint()
      .value("uuid").isEqualTo(UUID.fromString(newCustomerUuid.toString()))
      .value("name").isEqualTo("Tom Mayer")
      .value("day_of_birth").isEqualTo(LocalDate.of(2000, Month.MAY, 19))
      .value("state").isEqualTo("active");


  }

  // POST /customers mit invalidem Accept-Header -> 400 + keine Datenbankänderung
  @Test
  void when_post_customers_and_invalid_accept_then_status_not_acceptable_and_database_unmodified() {
    changes.setStartPointNow();

    given()
      .contentType(ContentType.JSON)
      .body("""
            {
              "name": "Tom Mayer",
              "birthdate": "2020-05-19",
              "state": "active"
            }
        """)
      .accept(ContentType.XML)
      .when()
      .post("/customers")
      .then()
      .statusCode(406);

    changes.setEndPointNow();

    assertThat(changes)
      .hasNumberOfChanges(0);
  }
}
