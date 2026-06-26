package de.samples.quarkus.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/customers")
@RegisterRestClient(configKey = "customers-api")
@Produces(MediaType.APPLICATION_JSON)
public interface CustomersRestClient {

  @GET
  List<CustomerClientDto> getAll();

}
