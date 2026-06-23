package de.schulung.customers;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static de.schulung.customers.testing.CustomersApiHelper.ResponseAssertions.toBeJsonArray;
import static de.schulung.customers.testing.CustomersApiHelper.ResponseAssertions.toContainCustomerInArray;
import static de.schulung.customers.testing.CustomersApiHelper.ResponseAssertions.toHaveCustomerInBody;
import static de.schulung.customers.testing.CustomersApiHelper.ResponseAssertions.toHaveSentCustomerInBody;
import static de.schulung.customers.testing.CustomersApiHelper.ResponseAssertions.toHaveStatusCode;
import static de.schulung.customers.testing.CustomersApiHelper.ResponseAssertions.toNotContainCustomerInArray;
import static de.schulung.customers.testing.CustomersApiHelper.ResponseAssertions.withAnyUuid;
import static de.schulung.customers.testing.CustomersApiHelper.aCustomer;
import static de.schulung.customers.testing.CustomersApiHelper.customers;
import static de.schulung.customers.testing.CustomersApiHelper.withAccept;
import static de.schulung.customers.testing.CustomersApiHelper.withBody;
import static de.schulung.customers.testing.CustomersApiHelper.withContentType;

@QuarkusTest
@TestTransaction
public class CustomersApiTests {

  @Test
  void when_get_customers_then_return_json_array() {
    customers()
      .fetchAll()
      .assertResponse(toHaveStatusCode(200))
      .assertResponse(toBeJsonArray());
  }

  @Test
  void when_get_customers_as_xml_then_deny() {
    customers()
      .fetchAll()
      .withAccept(ContentType.XML)
      .assertResponse(toHaveStatusCode(406));
  }

  @Test
  void when_post_customers_then_return_created_and_customer_with_uuid() {
    aCustomer()
      .create()
      .assertResponse(toHaveStatusCode(201))
      .assertResponse(toHaveSentCustomerInBody(withAnyUuid()));
  }

  @Test
  void when_post_customers_with_xml_then_return_unsupported_mediatype() {
    aCustomer()
      .create(withContentType(ContentType.XML).withBody("<customer />"))
      .assertResponse(toHaveStatusCode(415));
  }

  @Test
  void when_post_customers_with_invalid_accept_then_return_not_acceptable() {
    aCustomer()
      .create(withAccept(ContentType.XML))
      .assertResponse(toHaveStatusCode(406));
  }

  @Test
  void given_created_customer_when_get_customers_then_customer_is_in_array() {
    final var customer = aCustomer()
      .create()
      .andReturn();

    customers()
      .fetchAll()
      .assertResponse(toHaveStatusCode(200))
      .assertResponse(toContainCustomerInArray(customer.asCustomer()));
  }

  @Test
  void given_created_customer_when_get_customer_by_uuid_then_customer_is_returned() {
    final var customer = aCustomer()
      .create()
      .andReturn();

    customers()
      .fetchByUuid(customer.id())
      .assertResponse(toHaveStatusCode(200))
      .assertResponse(toHaveCustomerInBody(customer.asCustomer()));
  }

  @Test
  void given_created_customer_when_get_customer_by_uuid_as_xml_then_not_acceptable() {
    final var customer = aCustomer()
      .create()
      .andReturn();

    customers()
      .fetchByUuid(customer.id())
      .withAccept(ContentType.XML)
      .assertResponse(toHaveStatusCode(406));
  }

  @Test
  void given_created_customer_when_get_customer_by_location_header_then_return_customer() {
    final var customer = aCustomer()
      .create()
      .andReturn();

    customers()
      .fetchByLocation(customer.location())
      .assertResponse(toHaveStatusCode(200))
      .assertResponse(toHaveCustomerInBody(customer.asCustomer()));
  }

  @Test
  void when_get_customer_by_uuid_not_existing_then_return_not_found() {
    final var notExistingCustomerUuid = aCustomer()
      .ensureNotExisting()
      .id();

    customers()
      .fetchByUuid(notExistingCustomerUuid)
      .assertResponse(toHaveStatusCode(404));
  }

  @Test
  void given_created_customer_when_delete_customer_then_return_no_content() {
    final var notExistingCustomerUuid = aCustomer()
      .ensureNotExisting()
      .id();

    customers()
      .delete(notExistingCustomerUuid)
      .assertResponse(toHaveStatusCode(404));
  }

  @Test
  void given_deleted_customer_when_get_customers_then_return_array_without_customer() {
    final var notExistingCustomerUuid = aCustomer()
      .ensureNotExisting()
      .id();

    customers()
      .fetchAll()
      .assertResponse(toHaveStatusCode(200))
      .assertResponse(toNotContainCustomerInArray(notExistingCustomerUuid));
  }

  static Stream<String> invalidCustomerJsons() {
    return Stream.of(
      // customer is too young
      """
        {
          "name": "Tom Mayer",
          "birthdate": "%s",
          "state": "active"
        }
        """
        .formatted(
          LocalDate
            .now()
            .minusYears(10)
            .format(DateTimeFormatter.ISO_DATE)
        ),
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
        """
    );
  }

  @ParameterizedTest
  @MethodSource("invalidCustomerJsons")
  void given_invalid_customer_when_post_customers_then_bad_request(String body) {
    aCustomer()
      .create(withBody(body))
      .assertResponse(toHaveStatusCode(400));
  }

  // Setup: POST /customers mit Customer als JSON -> 201 + UUID
  // Test: PUT /customers/{uuid} mit geaendertem Customer als JSON -> 204
  // Test: GET /customers/{uuid} -> 200 mit ersetztem Customer
  @Test
  void given_created_customer_when_put_customer_then_no_content_and_customer_is_replaced() {
    final var customer = aCustomer()
      .create()
      .andReturn();

    // Test: replace
    final var replacedCustomer = aCustomer()
      .named("Lisa Schmidt")
      .bornOn("1990-01-15")
      .inState("locked")
      .replace(customer.id())
      .assertResponse(toHaveStatusCode(204))
      .andReturn();

    // Test: customer was replaced
    customers()
      .fetchByUuid(customer.id())
      .assertResponse(toHaveStatusCode(200))
      .assertResponse(toHaveCustomerInBody(replacedCustomer));
  }

  // Setup: POST /customers mit Customer als JSON -> 201 + UUID
  // Setup: DELETE /customers/{uuid} -> 204
  // Test: PUT /customers/{uuid} -> 404
  @Test
  void given_deleted_customer_when_put_customer_then_not_found() {
    final var notExistingCustomerUuid = aCustomer()
      .ensureNotExisting()
      .id();

    // Test
    aCustomer()
      .named("Lisa Schmidt")
      .bornOn("1990-01-15")
      .inState("locked")
      .replace(notExistingCustomerUuid)
      .assertResponse(toHaveStatusCode(404));
  }

  // Setup: POST /customers mit Customer als JSON -> 201 + UUID
  // Test: PUT /customers/{uuid} mit ungueltigem Customer als JSON -> 400
  @ParameterizedTest
  @MethodSource("invalidCustomerJsons")
  void given_invalid_customer_when_put_customer_then_bad_request(String body) {
    final var customer = aCustomer()
      .create()
      .andReturn();

    // Test
    aCustomer()
      .replace(customer.id(), body)
      .assertResponse(toHaveStatusCode(400));
  }

  @Test
  void given_invalid_state_parameter_when_get_customers_then_status_bad_request() {
    customers()
      .fetchAll()
      .withState("gelbekatze")
      .assertResponse(toHaveStatusCode(400));
  }

  @Test
  void given_created_customer_with_active_state_when_get_customers_with_locked_state_then_customer_is_not_in_array() {
    final var customer = aCustomer()
      .inState("active")
      .create()
      .andReturn();

    customers()
      .fetchAll()
      .withState("locked")
      .assertResponse(toHaveStatusCode(200))
      .assertResponse(toNotContainCustomerInArray(customer.id()));
  }

}
