package de.schulung.customers.boundary;

import de.schulung.customers.domain.CustomersService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static de.schulung.customers.testing.CustomersApiHelper.ResponseAssertions.toHaveStatusCode;
import static de.schulung.customers.testing.CustomersApiHelper.aCustomer;
import static de.schulung.customers.testing.CustomersApiHelper.customers;
import static de.schulung.customers.testing.CustomersApiHelper.withAccept;
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

    customers()
      .fetchByUuid(newCustomerUuid)
      .assertResponse(toHaveStatusCode(404));
  }

  // POST /customers mit Accept: application/xml -> 406
  @Test
  void when_post_customers_with_invalid_accept_then_return_not_acceptable() {
    aCustomer()
      .create(withAccept(ContentType.XML))
      .assertResponse(toHaveStatusCode(406));

    verifyNoInteractions(customersService);
  }

}
