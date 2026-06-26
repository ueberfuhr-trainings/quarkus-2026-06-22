package de.samples.quarkus.domain;

import java.util.stream.Stream;

public interface CustomersClient {

  Stream<Customer> findAll();

}
