package de.schulung.customers.testing;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import jakarta.ws.rs.core.HttpHeaders;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Fluent test helper for the Customers REST API.
 * <p>
 * Every operation is performed through a small call object that exposes:
 * <ul>
 *   <li>{@link ApiCall#assertResponse(java.util.function.BiConsumer)} – runs
 *       assertions against the sent request and the response and returns the
 *       call itself, so it can be chained (also for error cases);</li>
 *   <li>{@code andReturn()} – expects a successful call, asserts it and returns
 *       the resulting domain object.</li>
 * </ul>
 * Reads and deletes go through the {@link #customers()} client, writes through
 * {@link #aCustomer()}:
 * <pre>{@code
 * // reads
 * customers().fetchAll().assertResponse((req, resp) -> resp.statusCode(200));
 * Customer c = customers().fetchByUuid(uuid).andReturn();
 *
 * customers().fetchAll()
 *   .withAccept(ContentType.XML)
 *   .assertResponse((req, resp) -> resp.statusCode(406));
 *
 * // writes – chain assertions, then obtain the handle
 * CreatedCustomer created = aCustomer().create()
 *   .assertResponse((req, resp) -> resp.statusCode(201))
 *   .assertResponse((req, resp) -> resp.body("name", is(equalTo(req.customer().name()))))
 *   .andReturn();
 *
 * aCustomer()
 *   .create(withContentType(ContentType.XML).withBody("<customer/>"))
 *   .assertResponse((req, resp) -> resp.statusCode(415));
 *
 * // delete (no body, hence no andReturn())
 * customers().delete(uuid).assertResponse((req, resp) -> resp.statusCode(404));
 * }</pre>
 */
public final class CustomersApiHelper {

  public static final String DEFAULT_NAME = "Tom Mayer";
  public static final String DEFAULT_BIRTHDATE = "2006-06-23";
  public static final String DEFAULT_STATE = "active";

  private CustomersApiHelper() {
  }

  // ---------------------------------------------------------------------------
  // Entry points
  // ---------------------------------------------------------------------------

  public static CustomersClient customers() {
    return new CustomersClient();
  }

  public static CustomerBuilder aCustomer() {
    return new CustomerBuilder();
  }

  // ---------------------------------------------------------------------------
  // POST customizations for CustomerBuilder#create(PostOptions)
  // ---------------------------------------------------------------------------

  public static PostOptions withContentType(ContentType contentType) {
    return new PostOptions().withContentType(contentType);
  }

  public static PostOptions withAccept(ContentType accept) {
    return new PostOptions().withAccept(accept);
  }

  public static PostOptions withBody(String body) {
    return new PostOptions().withBody(body);
  }

  // ---------------------------------------------------------------------------
  // Call infrastructure
  // ---------------------------------------------------------------------------

  /**
   * A performed (or lazily performed) call against the Customers API. The HTTP
   * request runs once on the first terminal call and is cached, so chained
   * {@link #assertResponse(BiConsumer)} calls and a subclass {@code andReturn()}
   * share it.
   *
   * @param <SELF> the concrete call type, returned by
   *               {@link #assertResponse(BiConsumer)} for chaining.
   * @param <REQ>  the request descriptor handed to assertions so they can refer
   *               to what was sent instead of hard-coded constants.
   * @param <RESP> the typed response handed to assertions; different operations
   *               yield different bodies, so assertions can be restricted to the
   *               operations they make sense for.
   */
  public abstract static class ApiCall<
    SELF extends ApiCall<SELF, REQ, RESP>,
    REQ,
    RESP extends ApiResponse> {

    private ValidatableResponse validatable;

    /**
     * Performs the actual HTTP request.
     */
    protected abstract ValidatableResponse perform();

    /**
     * Describes what was sent, for use in assertions.
     */
    protected abstract REQ request();

    /**
     * Wraps the raw response in the operation's typed response.
     */
    protected abstract RESP wrap(ValidatableResponse validatable);

    /**
     * Lazily performs the call once and caches the response.
     */
    protected final ValidatableResponse validatable() {
      if (validatable == null) {
        validatable = perform();
      }
      return validatable;
    }

    /**
     * Runs the given assertion against the request descriptor and the typed
     * response and returns {@code this}, so calls can be chained and finished
     * with {@code andReturn()}, e.g.
     * {@code .assertResponse(toHaveStatusCode(201)).assertResponse(toHaveSentCustomerInBody(withAnyUuid()))}.
     */
    @SuppressWarnings("unchecked")
    public SELF assertResponse(BiConsumer<? super REQ, ? super RESP> assertion) {
      assertion.accept(request(), wrap(validatable()));
      return (SELF) this;
    }
  }

  // ---------------------------------------------------------------------------
  // Typed responses – one per operation, so assertions can be scoped
  // ---------------------------------------------------------------------------

  /**
   * Any response, exposing the underlying RestAssured response.
   */
  public interface ApiResponse {
    ValidatableResponse response();
  }

  /** Response of POST /customers. */
  public record PostCustomersResponse(ValidatableResponse response) implements ApiResponse {
  }

  /** Response of GET /customers. */
  public record GetCustomersResponse(ValidatableResponse response) implements ApiResponse {
  }

  /** Response of GET /customers/{uuid} or GET {location}. */
  public record GetCustomerResponse(ValidatableResponse response) implements ApiResponse {
  }

  /** Response of DELETE /customers/{uuid}. */
  public record DeleteCustomerResponse(ValidatableResponse response) implements ApiResponse {
  }

  /** Response of PUT /customers/{uuid}. */
  public record ReplaceCustomerResponse(ValidatableResponse response) implements ApiResponse {
  }

  /**
   * Named, reusable assertions on a response, for use with
   * {@link ApiCall#assertResponse(BiConsumer)}. Each is typed to the response
   * kind it applies to, so e.g. {@link #toBeJsonArray()} only compiles on a
   * fetch-all call and {@link #toHaveSentCustomerInBody(Consumer)} only on a
   * create call:
   * <pre>{@code
   * .assertResponse(toHaveStatusCode(201))
   * .assertResponse(toHaveSentCustomerInBody(withAnyUuid()))
   * }</pre>
   */
  public static final class ResponseAssertions {

    private ResponseAssertions() {
    }

    // -- applicable to every operation ----------------------------------------

    public static BiConsumer<Object, ApiResponse> toHaveStatusCode(int statusCode) {
      return (req, resp) -> resp.response().statusCode(statusCode);
    }

    public static BiConsumer<Object, ApiResponse> toHaveContentType(ContentType contentType) {
      return (req, resp) -> resp.response().contentType(contentType);
    }

    // -- array responses (fetch all) ------------------------------------------

    public static BiConsumer<Object, GetCustomersResponse> toBeJsonArray() {
      return (req, resp) -> resp.response()
        .contentType(ContentType.JSON)
        .body("", is(instanceOf(List.class)));
    }

    /**
     * Asserts the JSON array contains the given customer (matched by uuid).
     */
    public static BiConsumer<Object, GetCustomersResponse> toContainCustomerInArray(Customer expected) {
      return (req, resp) -> resp.response()
        .contentType(ContentType.JSON)
        // see https://github.com/rest-assured/rest-assured/wiki/usage#json-example
        .body(
          "find { it.uuid == '%s' }".formatted(expected.uuid()),
          allOf(
            hasEntry("name", expected.name()),
            hasEntry("birthdate", expected.birthdate()),
            hasEntry("state", expected.state())
          )
        );
    }

    /**
     * Asserts the JSON array does not contain a customer with the given uuid.
     */
    public static BiConsumer<Object, GetCustomersResponse> toNotContainCustomerInArray(Object uuid) {
      return (req, resp) -> resp.response()
        .contentType(ContentType.JSON)
        .body("find { it.uuid == '%s' }".formatted(uuid), is(nullValue()));
    }

    // -- single-customer responses (create, fetch by uuid/location) -----------

    /**
     * Asserts the JSON body represents the given customer including its uuid,
     * e.g. {@code toHaveCustomerInBody(customer.asCustomer())}.
     */
    public static BiConsumer<Object, GetCustomerResponse> toHaveCustomerInBody(Customer expected) {
      return toHaveCustomerInBody(expected, withUuid(expected.uuid()));
    }

    /**
     * Asserts the JSON body represents the given customer (name, birthdate,
     * state) and that its {@code uuid} matches the given expectation.
     */
    public static BiConsumer<Object, GetCustomerResponse> toHaveCustomerInBody(
      Customer expected,
      Consumer<ValidatableResponse> uuidExpectation
    ) {
      return (req, resp) -> assertCustomerBody(resp.response(), expected, uuidExpectation);
    }

    // -- create only: asserts the body equals the sent customer ---------------

    /**
     * Asserts the create response body represents the customer that was sent in
     * the request, with its {@code uuid} matching the given expectation, e.g.
     * {@code toHaveSentCustomerInBody(withAnyUuid())}.
     */
    public static BiConsumer<CreateCall.Request, PostCustomersResponse> toHaveSentCustomerInBody(
      Consumer<ValidatableResponse> uuidExpectation
    ) {
      return (req, resp) -> assertCustomerBody(resp.response(), req.customer(), uuidExpectation);
    }

    private static void assertCustomerBody(
      ValidatableResponse resp,
      Customer expected,
      Consumer<ValidatableResponse> uuidExpectation
    ) {
      uuidExpectation.accept(resp);
      resp
        .contentType(ContentType.JSON)
        .body("name", is(equalTo(expected.name())))
        .body("birthdate", is(equalTo(expected.birthdate())))
        .body("state", is(equalTo(expected.state())));
    }

    /**
     * Expects the body's {@code uuid} to be any (present) string.
     */
    public static Consumer<ValidatableResponse> withAnyUuid() {
      return resp -> resp.body("uuid", is(instanceOf(String.class)));
    }

    /**
     * Expects the body's {@code uuid} to equal the given value.
     */
    public static Consumer<ValidatableResponse> withUuid(Object expected) {
      return resp -> resp.body("uuid", is(equalTo(expected)));
    }
  }

  // ---------------------------------------------------------------------------
  // Read / delete client
  // ---------------------------------------------------------------------------

  public static final class CustomersClient {

    private CustomersClient() {
    }

    public FetchAllCall fetchAll() {
      return new FetchAllCall();
    }

    public FetchByUuidCall fetchByUuid(Object uuid) {
      return new FetchByUuidCall(uuid);
    }

    public FetchByLocationCall fetchByLocation(String location) {
      return new FetchByLocationCall(location);
    }

    public DeleteCall delete(Object uuid) {
      return new DeleteCall(uuid);
    }
  }

  /**
   * GET /customers
   */
  public static final class FetchAllCall extends ApiCall<FetchAllCall, FetchAllCall.Request, GetCustomersResponse> {

    private ContentType accept = ContentType.JSON;
    private String state;

    private FetchAllCall() {
    }

    public FetchAllCall withAccept(ContentType accept) {
      this.accept = accept;
      return this;
    }

    public FetchAllCall withState(String state) {
      this.state = state;
      return this;
    }

    @Override
    protected ValidatableResponse perform() {
      final var request = given().accept(accept);
      if (state != null) {
        request.queryParam("state", state);
      }
      return request
        .when()
        .get("/customers")
        .then();
    }

    @Override
    protected Request request() {
      return new Request(state);
    }

    @Override
    protected GetCustomersResponse wrap(ValidatableResponse validatable) {
      return new GetCustomersResponse(validatable);
    }

    /**
     * What was sent with a GET /customers.
     */
    public record Request(String state) {
    }

    /**
     * Asserts 200 + JSON and returns the customers.
     */
    public List<Customer> andReturn() {
      final List<Map<String, String>> raw = validatable()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .extract()
        .jsonPath()
        .getList(".");
      return raw.stream()
        .map(CustomersApiHelper::toCustomer)
        .toList();
    }
  }

  /**
   * GET /customers/{uuid}
   */
  public static final class FetchByUuidCall extends ApiCall<FetchByUuidCall, FetchByUuidCall.Request, GetCustomerResponse> {

    private final Object uuid;
    private ContentType accept = ContentType.JSON;

    private FetchByUuidCall(Object uuid) {
      this.uuid = uuid;
    }

    public FetchByUuidCall withAccept(ContentType accept) {
      this.accept = accept;
      return this;
    }

    @Override
    protected ValidatableResponse perform() {
      return given()
        .accept(accept)
        .pathParam("uuid", uuid)
        .when()
        .get("/customers/{uuid}")
        .then();
    }

    @Override
    protected Request request() {
      return new Request(uuid);
    }

    @Override
    protected GetCustomerResponse wrap(ValidatableResponse validatable) {
      return new GetCustomerResponse(validatable);
    }

    /**
     * What was sent with a GET /customers/{uuid}.
     */
    public record Request(Object uuid) {
    }

    /**
     * Asserts 200 + JSON and returns the customer.
     */
    public Customer andReturn() {
      return toCustomer(validatable());
    }
  }

  /**
   * GET {location}
   */
  public static final class FetchByLocationCall extends ApiCall<FetchByLocationCall, FetchByLocationCall.Request, GetCustomerResponse> {

    private final String location;
    private ContentType accept = ContentType.JSON;

    private FetchByLocationCall(String location) {
      this.location = location;
    }

    public FetchByLocationCall withAccept(ContentType accept) {
      this.accept = accept;
      return this;
    }

    @Override
    protected ValidatableResponse perform() {
      return given()
        .accept(accept)
        .baseUri(location)
        .when()
        .get()
        .then();
    }

    @Override
    protected Request request() {
      return new Request(location);
    }

    @Override
    protected GetCustomerResponse wrap(ValidatableResponse validatable) {
      return new GetCustomerResponse(validatable);
    }

    /**
     * What was sent with a GET {location}.
     */
    public record Request(String location) {
    }

    /**
     * Asserts 200 + JSON and returns the customer.
     */
    public Customer andReturn() {
      return toCustomer(validatable());
    }
  }

  /**
   * DELETE /customers/{uuid}
   */
  public static final class DeleteCall extends ApiCall<DeleteCall, DeleteCall.Request, DeleteCustomerResponse> {

    private final Object uuid;

    private DeleteCall(Object uuid) {
      this.uuid = uuid;
    }

    @Override
    protected ValidatableResponse perform() {
      return given()
        .pathParam("uuid", uuid)
        .when()
        .delete("/customers/{uuid}")
        .then();
    }

    @Override
    protected Request request() {
      return new Request(uuid);
    }

    @Override
    protected DeleteCustomerResponse wrap(ValidatableResponse validatable) {
      return new DeleteCustomerResponse(validatable);
    }

    /**
     * What was sent with a DELETE /customers/{uuid}.
     */
    public record Request(Object uuid) {
    }
  }

  private static Customer toCustomer(ValidatableResponse okResponse) {
    final var extracted = okResponse
      .statusCode(200)
      .contentType(ContentType.JSON)
      .extract();
    return new Customer(
      extracted.path("uuid"),
      extracted.path("name"),
      extracted.path("birthdate"),
      extracted.path("state")
    );
  }

  private static Customer toCustomer(Map<String, String> json) {
    return new Customer(
      json.get("uuid"),
      json.get("name"),
      json.get("birthdate"),
      json.get("state")
    );
  }

  // ---------------------------------------------------------------------------
  // Write builder + call
  // ---------------------------------------------------------------------------

  public static final class CustomerBuilder {

    private String name = DEFAULT_NAME;
    private String birthdate = DEFAULT_BIRTHDATE;
    private String state = DEFAULT_STATE;

    private CustomerBuilder() {
    }

    public CustomerBuilder named(String name) {
      this.name = name;
      return this;
    }

    public CustomerBuilder bornOn(String birthdate) {
      this.birthdate = birthdate;
      return this;
    }

    public CustomerBuilder inState(String state) {
      this.state = state;
      return this;
    }

    public String asJson() {
      return """
        {
          "name": "%s",
          "birthdate": "%s",
          "state": "%s"
        }
        """.formatted(name, birthdate, state);
    }

    /**
     * The customer payload this builder represents (uuid not yet assigned).
     */
    public Customer customer() {
      return new Customer(null, name, birthdate, state);
    }

    private CreateCustomerRequest post() {
      return new CreateCustomerRequest(asJson());
    }

    /**
     * Performs POST /customers with the default (valid) request. See
     * {@link #create(PostOptions)}.
     */
    public CreateCall create() {
      return create(new PostOptions());
    }

    /**
     * Performs POST /customers with the given request customization. Assert the
     * response via {@link CreateCall#assertResponse(BiConsumer)} or obtain the created
     * customer (asserting 201) via {@link CreateCall#andReturn()}.
     */
    public CreateCall create(PostOptions options) {
      final var customer = new Customer(null, name, birthdate, state);
      return new CreateCall(customer, options.applyTo(post()));
    }

    /**
     * Creates this customer and immediately deletes it again, so its uuid is
     * guaranteed not to exist. Returns the deleted reference (uuid/location),
     * e.g. {@code var customer = aCustomer().ensureNotExisting();}.
     */
    public DeletedCustomer ensureNotExisting() {
      return create().andReturn().delete();
    }

    /**
     * Performs PUT /customers/{uuid} with this builder's (valid) payload. Assert
     * the response via {@link ReplaceCall#assertResponse(BiConsumer)} or obtain the
     * resulting customer (asserting 204) via {@link ReplaceCall#andReturn()}.
     */
    public ReplaceCall replace(Object uuid) {
      return replace(uuid, asJson());
    }

    /**
     * Performs PUT /customers/{uuid} with the given (typically invalid) raw body.
     */
    public ReplaceCall replace(Object uuid, String body) {
      final var customer = new Customer(String.valueOf(uuid), name, birthdate, state);
      return new ReplaceCall(uuid, customer, body);
    }
  }

  /**
   * POST /customers
   */
  public static final class CreateCall extends ApiCall<CreateCall, CreateCall.Request, PostCustomersResponse> {

    private final Customer customer;
    private final CreateCustomerRequest request;

    private CreateCall(Customer customer, CreateCustomerRequest request) {
      this.customer = customer;
      this.request = request;
    }

    @Override
    protected ValidatableResponse perform() {
      return request.response();
    }

    @Override
    protected Request request() {
      return new Request(customer);
    }

    @Override
    protected PostCustomersResponse wrap(ValidatableResponse validatable) {
      return new PostCustomersResponse(validatable);
    }

    /**
     * What was sent with a POST /customers. {@link #customer()} reflects the
     * {@link CustomerBuilder}'s payload (its {@code uuid} is {@code null});
     * for a {@code create(PostOptions)} that overrides the raw body it may
     * differ from what was actually transmitted.
     */
    public record Request(Customer customer) {
    }

    /**
     * Asserts a successful creation (201 + Location) and returns the handle.
     */
    public CreatedCustomer andReturn() {
      final var extracted = validatable()
        .statusCode(201)
        .header(HttpHeaders.LOCATION, is(instanceOf(String.class)))
        .extract();
      return new CreatedCustomer(
        extracted.path("uuid"),
        extracted.path("name"),
        extracted.path("birthdate"),
        extracted.path("state"),
        extracted.header(HttpHeaders.LOCATION)
      );
    }
  }

  /**
   * PUT /customers/{uuid}
   */
  public static final class ReplaceCall extends ApiCall<ReplaceCall, ReplaceCall.Request, ReplaceCustomerResponse> {

    private final Object uuid;
    private final Customer customer;
    private final String body;

    private ReplaceCall(Object uuid, Customer customer, String body) {
      this.uuid = uuid;
      this.customer = customer;
      this.body = body;
    }

    @Override
    protected ValidatableResponse perform() {
      return given()
        .contentType(ContentType.JSON)
        .body(body)
        .accept(ContentType.JSON)
        .pathParam("uuid", uuid)
        .when()
        .put("/customers/{uuid}")
        .then();
    }

    @Override
    protected Request request() {
      return new Request(uuid, customer);
    }

    @Override
    protected ReplaceCustomerResponse wrap(ValidatableResponse validatable) {
      return new ReplaceCustomerResponse(validatable);
    }

    /**
     * What was sent with a PUT /customers/{uuid}. {@link #customer()} carries the
     * target {@code uuid} and the builder's payload; for a {@code replace(uuid, body)}
     * that overrides the raw body it may differ from what was actually transmitted.
     */
    public record Request(Object uuid, Customer customer) {
    }

    /**
     * Asserts a successful replacement (204) and returns the resulting customer
     * (the target uuid combined with the sent payload).
     */
    public Customer andReturn() {
      validatable().statusCode(204);
      return customer;
    }
  }

  /**
   * Customizations for a POST /customers request, used with
   * {@link CustomerBuilder#create(PostOptions)} to build (typically invalid)
   * requests. A {@code null} value keeps the default of the underlying request.
   */
  public static final class PostOptions {

    private ContentType contentType;
    private ContentType accept;
    private String body;

    private PostOptions() {
    }

    public PostOptions withContentType(ContentType contentType) {
      this.contentType = contentType;
      return this;
    }

    public PostOptions withAccept(ContentType accept) {
      this.accept = accept;
      return this;
    }

    public PostOptions withBody(String body) {
      this.body = body;
      return this;
    }

    private CreateCustomerRequest applyTo(CreateCustomerRequest request) {
      if (contentType != null) {
        request.withContentType(contentType);
      }
      if (accept != null) {
        request.withAccept(accept);
      }
      if (body != null) {
        request.withBody(body);
      }
      return request;
    }
  }

  /**
   * Internal POST request builder.
   */
  private static final class CreateCustomerRequest {

    private ContentType contentType = ContentType.JSON;
    private ContentType accept = ContentType.JSON;
    private String body;

    private CreateCustomerRequest(String body) {
      this.body = body;
    }

    private void withContentType(ContentType contentType) {
      this.contentType = contentType;
    }

    private void withAccept(ContentType accept) {
      this.accept = accept;
    }

    private void withBody(String body) {
      this.body = body;
    }

    private ValidatableResponse response() {
      return given()
        .contentType(contentType)
        .body(body)
        .accept(accept)
        .when()
        .post("/customers")
        .then();
    }
  }

  // ---------------------------------------------------------------------------
  // Domain handles
  // ---------------------------------------------------------------------------

  /**
   * A customer representation as returned by the API.
   */
  public record Customer(String uuid, String name, String birthdate, String state) {
  }

  /**
   * A customer that has been created via the API. Exposes the created
   * properties and {@code Location} header for assertions and allows
   * deleting the customer again via {@link #delete()}.
   */
  public record CreatedCustomer(
    String id,
    String name,
    String birthdate,
    String state,
    String location
  ) {

    /**
     * This customer as a plain {@link Customer} (its {@code id} as uuid).
     */
    public Customer asCustomer() {
      return new Customer(id, name, birthdate, state);
    }

    /**
     * Performs DELETE /customers/{uuid}, asserts 204 and returns a
     * reference to the deleted customer.
     */
    public DeletedCustomer delete() {
      customers()
        .delete(id)
        .assertResponse(ResponseAssertions.toHaveStatusCode(204));
      return new DeletedCustomer(id, location);
    }
  }

  /**
   * A reference to a customer that has been deleted via the API.
   */
  public record DeletedCustomer(String id, String location) {
  }
}
