# CDI Events vs. Interceptors

Beide Technologien gehören zu CDI (Contexts and Dependency Injection) und dienen
dazu, **fachlichen Code von Querschnittsbelangen zu entkoppeln**. Sie verfolgen
aber unterschiedliche Ziele und sind nicht beliebig austauschbar.

- **CDI Events** realisieren eine **Benachrichtigung** ("etwas ist passiert"):
  Ein Producer feuert ein Event, mehrere Observer reagieren darauf – ohne dass
  der Producer die Empfänger kennt (lose Kopplung, Publish/Subscribe).
- **Interceptors** realisieren ein **Umklammern eines Methodenaufrufs**
  (Cross-Cutting Concern): Code wird *vor*, *nach* oder *um* einen Aufruf herum
  ausgeführt (z.B. Logging, Transaktionen, Security, Caching).

## Vergleichstabelle

| Kriterium | CDI Events | Interceptors |
|---|---|---|
| **Zweck / Idee** | Benachrichtigung über ein Ereignis (Publish/Subscribe) | Querschnittsbelang um einen Methodenaufruf herum (Cross-Cutting Concern) |
| **Typische Anwendungsfälle** | Domain-Events, Auditing, Entkopplung von Modulen, "etwas reagieren lassen" | Logging, Transaktionen, Security-Checks, Caching, Metriken, Retry |
| **Auslösung** | Aktiv im Code: `event.fire(...)` bzw. `fireAsync(...)` | Implizit beim Aufruf einer annotierten/gebundenen Methode |
| **Kopplungsrichtung** | Producer kennt Observer **nicht** | Methode kennt Interceptor **nicht** (über Binding-Annotation verbunden) |
| **Multiplizität** | 1 Event → **0..n** Observer | 1 Methodenaufruf → **0..n** Interceptoren (als Kette) |
| **Reihenfolge** | Über `@Priority` steuerbar, sonst unbestimmt | Über `@Priority` / Reihenfolge der Bindings als Kette definiert |
| **Synchron / Asynchron** | **Beides**: `fire()` synchron, `fireAsync()` asynchron | **Synchron** (im Aufruf-Stack); Async nur indirekt (z.B. eigener Thread) |
| **Rückgabewert / Beeinflussung des Aufrufs** | Nein – Observer können den Producer nicht beeinflussen (außer über Exception bei `fire()`) | **Ja** – Interceptor kann Parameter/Ergebnis ändern, Aufruf unterdrücken, Exceptions abfangen |
| **Zugriff auf den Aufruf** | Nur auf das Event-Objekt (Payload) | Auf `InvocationContext`: Zielmethode, Parameter, `proceed()` |
| **Datenfluss** | Event-Payload (eigenes Objekt, z.B. `CustomerCreatedEvent`) | Der originale Methodenaufruf inkl. Argumenten und Rückgabewert |
| **Transaktionsverhalten** | Über `@Observes(during = ...)` an Transaktionsphasen koppelbar (z.B. `AFTER_SUCCESS`) | Kann selbst Transaktionen aufspannen (z.B. `@Transactional`) |
| **Fehlerbehandlung** | Bei `fire()` propagiert eine Observer-Exception zum Producer; bei `fireAsync()` gesammelt in `CompletionStage` | Exception aus `proceed()` kann abgefangen/umgewandelt werden |
| **Aktivierung** | `@Observes` an Methodenparameter | Interceptor-Klasse `@Interceptor` + Binding-Annotation an Bean/Methode (oder `@Interceptors(...)`) |
| **Konfigurierbar / abschaltbar** | z.B. über `@IfBuildProperty` am Observer (siehe `CustomerEventsLogger`) | über `beans.xml`-Priorität oder bedingte Aktivierung der Bean |
| **Granularität** | Fachliches Ereignis (Domain-Ebene) | Technischer Methodenaufruf (Infrastruktur-Ebene) |

## Code-Beispiele aus diesem Projekt

### CDI Events

Der Producer feuert ein Event, ohne die Empfänger zu kennen
(`domain/CustomersService.java`):

```java
private final Event<Object> customerEventPublisher;

public CustomersService(CustomersSink sink, Event<Object> customerEventPublisher) {
  this.sink = sink;
  this.customerEventPublisher = customerEventPublisher;
}

public Customer create(Customer customer) {
  // ...
  customerEventPublisher.fire(new CustomerCreatedEvent(customer));
  return customer;
}
```

Der Observer reagiert auf das Event (`infrastructure/CustomerEventsLogger.java`):

```java
@ApplicationScoped
@IfBuildProperty(
  name = "application.customer-events.logger.enabled",
  stringValue = "true"
)
public class CustomerEventsLogger {

  @LoggerName("customer-events")
  Logger log;

  public void logCustomerCreatedEvent(@Observes CustomerCreatedEvent event) {
    log.infof("Customer created: %s", event.customer().getUuid());
  }
}
```

> **Merke:** Mehrere Observer können dasselbe Event empfangen. Der Producer hängt
> von keinem konkreten Observer ab – Observer lassen sich (wie hier über
> `@IfBuildProperty`) ergänzen oder abschalten, ohne den Producer zu ändern.

### Interceptor (schematisch)

Ein Interceptor umklammert den Methodenaufruf und kann ihn beeinflussen:

```java
// 1. Binding-Annotation
@InterceptorBinding
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface Logged {
}

// 2. Interceptor-Implementierung
@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LoggingInterceptor {

  @AroundInvoke
  Object logInvocation(InvocationContext ctx) throws Exception {
    long start = System.nanoTime();
    try {
      return ctx.proceed(); // ruft die eigentliche Methode auf
    } finally {
      // vorher/nachher: Querschnittslogik
    }
  }
}

// 3. Anwendung auf eine Bean/Methode
@Logged
public Customer create(Customer customer) { /* ... */ }
```

> **Merke:** Der Interceptor sieht den gesamten Aufruf (`InvocationContext`),
> kann Argumente und Rückgabewert verändern, `proceed()` weglassen oder
> Exceptions behandeln – das kann ein Event-Observer nicht.

## Wann was verwenden?

- **CDI Events**, wenn …
  - ein **fachliches Ereignis** veröffentlicht werden soll,
  - **mehrere (oder keine) Empfänger** lose gekoppelt reagieren sollen,
  - die Reaktion den Auslöser **nicht beeinflussen** muss,
  - eine **asynchrone** Verarbeitung gewünscht ist (`fireAsync`).

- **Interceptors**, wenn …
  - ein **technischer Querschnittsbelang** viele Methoden betrifft (Logging,
    Transaktionen, Security, Caching),
  - der **Aufruf selbst** beeinflusst werden soll (Parameter, Ergebnis,
    Exceptions, Abbruch),
  - die Logik **transparent** und ohne Änderung des Fachcodes wirken soll.

> Häufig ergänzen sich beide: Ein **Interceptor** kann z.B. eine Transaktion
> aufspannen, und am Ende der Transaktion wird ein **CDI Event** gefeuert, auf
> das andere Module reagieren.
