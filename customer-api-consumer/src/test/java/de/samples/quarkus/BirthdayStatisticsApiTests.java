package de.samples.quarkus;

import de.samples.quarkus.client.CustomerClientDto;
import de.samples.quarkus.client.CustomersRestClient;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.when;

@QuarkusTest
class BirthdayStatisticsApiTests {

  @InjectMock
  @RestClient
  CustomersRestClient customersRestClient;

  private CustomerClientDto createClientDto(String name, LocalDate birthdate) {
    var dto = new CustomerClientDto();
    dto.setUuid(UUID.randomUUID());
    dto.setName(name);
    dto.setBirthdate(birthdate);
    dto.setState("active");
    return dto;
  }

  @Test
  void whenNoCustomers_thenAllMonthsHaveCountZero() {
    when(customersRestClient.getAll()).thenReturn(List.of());

    given()
      .accept(ContentType.JSON)
      .when()
      .get("/birthdays/statistics")
      .then()
      .statusCode(200)
      .body("", instanceOf(List.class))
      .body("", hasSize(12))
      .body("[0].month", equalTo("Januar"))
      .body("[0].count", equalTo(0))
      .body("[11].month", equalTo("Dezember"))
      .body("[11].count", equalTo(0));
  }

  @Test
  void whenCustomersInJanuary_thenJanuaryHasCorrectCount() {
    when(customersRestClient.getAll()).thenReturn(List.of(
      createClientDto("Alice", LocalDate.of(1990, 1, 15)),
      createClientDto("Bob", LocalDate.of(1985, 1, 20))
    ));

    given()
      .accept(ContentType.JSON)
      .when()
      .get("/birthdays/statistics")
      .then()
      .statusCode(200)
      .body("", hasSize(12))
      .body("[0].month", equalTo("Januar"))
      .body("[0].count", equalTo(2))
      .body("[1].count", equalTo(0));
  }

  @Test
  void whenCustomersInDifferentMonths_thenEachMonthHasCorrectCount() {
    when(customersRestClient.getAll()).thenReturn(List.of(
      createClientDto("Alice", LocalDate.of(1990, 3, 10)),
      createClientDto("Bob", LocalDate.of(1985, 3, 20)),
      createClientDto("Charlie", LocalDate.of(1992, 7, 5)),
      createClientDto("Diana", LocalDate.of(1988, 12, 25))
    ));

    given()
      .accept(ContentType.JSON)
      .when()
      .get("/birthdays/statistics")
      .then()
      .statusCode(200)
      .body("", hasSize(12))
      .body("[2].month", equalTo("März"))
      .body("[2].count", equalTo(2))
      .body("[6].month", equalTo("Juli"))
      .body("[6].count", equalTo(1))
      .body("[11].month", equalTo("Dezember"))
      .body("[11].count", equalTo(1))
      .body("[0].count", equalTo(0));
  }

}
