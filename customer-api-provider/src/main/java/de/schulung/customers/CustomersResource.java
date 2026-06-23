package de.schulung.customers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/customers")
public class CustomersResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String[] getCustomers() {
    return new String[0];
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response createCustomer(Customer customer) {
    customer.setUuid(UUID.randomUUID());
    return Response
      .status(Response.Status.CREATED)
      .entity(customer)
      .build();
  }

}
