package de.samples.quarkus.client;

import de.samples.quarkus.domain.Customer;
import de.samples.quarkus.domain.CustomersClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.stream.Stream;

@ApplicationScoped
@Typed(CustomersClient.class)
@RequiredArgsConstructor
public class CustomersClientImpl implements CustomersClient {

  @RestClient
  CustomersRestClient restClient;

  private final CustomerClientDtoMapper mapper;

  @Override
  public Stream<Customer> findAll() {
    return restClient.getAll()
      .stream()
      .map(mapper::map);
  }

}
