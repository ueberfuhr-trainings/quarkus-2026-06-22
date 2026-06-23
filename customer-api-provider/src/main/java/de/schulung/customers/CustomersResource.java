package de.schulung.customers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.Collection;
import java.util.UUID;

@Path("/customers")
public class CustomersResource {

  private final CustomersService customersService = new CustomersService();

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<Customer> getCustomers() {
    return customersService
      .getCustomers()
      .toList();
  }

  @GET
  @Path("/{uuid}")
  @Produces(MediaType.APPLICATION_JSON)
  public Customer getCustomerByUuid(@PathParam("uuid") UUID uuid) {
    return customersService
      .getCustomerByUuid(uuid)
      .orElseThrow(NotFoundException::new);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createCustomer(Customer customer, UriInfo uriInfo) {
    customersService.createCustomer(customer);
    final var location = uriInfo
      .getAbsolutePathBuilder()
      .path(customer.getUuid().toString())
      .build();
    return Response
      .created(location)
      .entity(customer)
      .build();
  }

  @DELETE
  @Path("/{uuid}")
  public void deleteCustomer(@PathParam("uuid") UUID uuid) {
    if (!customersService.customerExists(uuid)) {
      throw new NotFoundException();
    }
    customersService.deleteCustomer(uuid);
  }

}
