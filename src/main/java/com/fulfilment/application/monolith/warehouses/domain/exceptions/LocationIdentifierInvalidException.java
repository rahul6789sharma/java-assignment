package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class LocationIdentifierInvalidException extends RuntimeException {

  public LocationIdentifierInvalidException() {
    super("Location identifier must not be null or blank");
  }
}
