package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class MaxWarehousesReachedException extends RuntimeException {

  public MaxWarehousesReachedException(String location) {
    super("Maximum number of warehouses reached for location: " + location);
  }
}
