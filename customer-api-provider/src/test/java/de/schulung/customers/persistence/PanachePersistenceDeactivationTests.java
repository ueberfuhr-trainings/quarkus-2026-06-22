package de.schulung.customers.persistence;

import de.schulung.customers.domain.CustomersSink;
import de.schulung.customers.persistence.panache.CustomersSinkPanacheImpl;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestProfile(PanachePersistenceDeactivationProfile.class)
public class PanachePersistenceDeactivationTests {

  @Inject
  CustomersSink customersSink;

  @Test
  void should_not_inject_panache_sink() {
    assertThat(customersSink)
      .isNotInstanceOf(CustomersSinkPanacheImpl.class);
  }

}
