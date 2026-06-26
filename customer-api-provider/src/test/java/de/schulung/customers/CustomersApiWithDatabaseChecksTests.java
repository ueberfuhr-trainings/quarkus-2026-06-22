package de.schulung.customers;

import de.schulung.customers.testing.TableChanges;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.assertj.db.type.Changes;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static de.schulung.customers.testing.CustomersApiHelper.ResponseAssertions.toHaveStatusCode;
import static de.schulung.customers.testing.CustomersApiHelper.aCustomer;
import static de.schulung.customers.testing.CustomersApiHelper.withAccept;
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

    final var customer = aCustomer()
      .create()
      .andReturn();

    changes.setEndPointNow();

    assertThat(changes)
      .hasNumberOfChanges(1)
      .change().isCreation()
      .rowAtEndPoint()
      .value("uuid").isEqualTo(UUID.fromString(customer.id()))
      .value("name").isEqualTo(customer.name())
      .value("day_of_birth").isEqualTo(LocalDate.parse(customer.birthdate()))
      .value("state").isEqualTo(customer.state());
  }

  // POST /customers mit invalidem Accept-Header -> 406 + keine Datenbankänderung
  @Test
  void when_post_customers_and_invalid_accept_then_status_not_acceptable_and_database_unmodified() {
    changes.setStartPointNow();

    aCustomer()
      .create(withAccept(ContentType.XML))
      .assertResponse(toHaveStatusCode(406));

    changes.setEndPointNow();

    assertThat(changes)
      .hasNumberOfChanges(0);
  }
}
