package de.schulung.customers.persistence;

import de.schulung.customers.domain.CustomersSink;
import de.schulung.customers.persistence.panache.CustomersSinkPanacheImpl;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestProfile(PanachePersistenceActivationProfile.class)
public class PanachePersistenceActivationTests {

  @Inject
  CustomersSink customersSink;

  @Test
  void should_inject_panache_sink() {
    assertThat(customersSink)
      .isInstanceOf(CustomersSinkPanacheImpl.class);
  }

}
