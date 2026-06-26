package de.schulung.customers.domain.events;

import de.schulung.customers.domain.Customer;

public record CustomerReplacedEvent(
  Customer customer
) {
}
