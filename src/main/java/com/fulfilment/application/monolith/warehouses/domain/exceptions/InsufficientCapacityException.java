package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class InsufficientCapacityException extends RuntimeException {

  public InsufficientCapacityException(int newCapacity, int requiredStock) {
    super(
        "New warehouse capacity "
            + newCapacity
            + " cannot accommodate the required stock of "
            + requiredStock);
  }
}
