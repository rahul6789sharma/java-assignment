package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class CapacityExceededException extends RuntimeException {

  public CapacityExceededException(int capacity, int maxCapacity) {
    super(
        "Warehouse capacity "
            + capacity
            + " exceeds maximum allowed capacity "
            + maxCapacity
            + " for this location");
  }
}
