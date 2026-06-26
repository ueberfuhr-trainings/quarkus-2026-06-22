package de.schulung.customers.boundary;

import de.schulung.customers.boundary.validation.ValidCustomerState;
import de.schulung.customers.domain.CustomersService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.Collection;
import java.util.UUID;

@Path("/customers")
public class CustomersResource {

  private final CustomersService customersService;
  private final CustomerDtoMapper mapper;

  public CustomersResource(CustomersService customersService, CustomerDtoMapper mapper) {
    this.customersService = customersService;
    this.mapper = mapper;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<CustomerDto> getCustomers(
    @QueryParam("state")
    @ValidCustomerState
    String state
  ) {
    return (null == state
      ? customersService.getCustomers()
      : customersService.getCustomersByState(mapper.mapState(state))
    )
      .map(mapper::map)
      .toList();
  }

  @GET
  @Path("/{uuid}")
  @Produces(MediaType.APPLICATION_JSON)
  public CustomerDto getCustomerByUuid(@PathParam("uuid") UUID uuid) {
    return customersService
      .getCustomerByUuid(uuid)
      .map(mapper::map)
      .orElseThrow(NotFoundException::new);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createCustomer(@Valid CustomerDto customerDto, UriInfo uriInfo) {
    final var customer = mapper.map(customerDto);
    customersService.createCustomer(customer);
    final var responseDto = mapper.map(customer);
    final var location = uriInfo
      .getAbsolutePathBuilder()
      .path(responseDto.getUuid().toString())
      .build();
    return Response
      .created(location)
      .entity(responseDto)
      .build();
  }

  @PUT
  @Path("/{uuid}")
  @Consumes(MediaType.APPLICATION_JSON)
  public void replaceCustomer(
    @PathParam("uuid") UUID uuid,
    @Valid CustomerDto customerDto
  ) {
    if (!customersService.customerExists(uuid)) {
      throw new NotFoundException();
    }
    final var customer = mapper.map(customerDto);
    customer.setUuid(uuid);
    customersService.updateCustomer(customer);
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
