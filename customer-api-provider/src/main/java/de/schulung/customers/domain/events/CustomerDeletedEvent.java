package de.schulung.customers.domain.events;

import java.util.UUID;

public record CustomerDeletedEvent(
  UUID uuid
) {
}
