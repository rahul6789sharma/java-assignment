package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class LocationNotFoundException extends RuntimeException {

  public LocationNotFoundException(String identifier) {
    super("Location not found: " + identifier);
  }
}
