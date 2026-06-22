package de.schulung.customers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/customers")
public class CustomersResource {

  @GET
  //@Produces(MediaType.APPLICATION_JSON)
  public String[] getCustomers() {
    return new String[0];
  }

}
