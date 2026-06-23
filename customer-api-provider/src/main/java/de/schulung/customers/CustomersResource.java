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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Path("/customers")
public class CustomersResource {

  private final Map<UUID, Customer> customers = new ConcurrentHashMap<>();

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<Customer> getCustomers() {
    return customers
      .values();
  }

  @GET
  @Path("/{uuid}")
  @Produces(MediaType.APPLICATION_JSON)
  public Customer getCustomerByUuid(@PathParam("uuid") UUID uuid) {
    final var result = customers.get(uuid);
    if (result == null) {
      throw new NotFoundException();
    }
    return result;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createCustomer(Customer customer, UriInfo uriInfo) {
    customer.setUuid(UUID.randomUUID());
    customers.put(customer.getUuid(), customer);
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
    if (!customers.containsKey(uuid)) {
      throw new NotFoundException();
    }
    customers.remove(uuid);
  }

}
