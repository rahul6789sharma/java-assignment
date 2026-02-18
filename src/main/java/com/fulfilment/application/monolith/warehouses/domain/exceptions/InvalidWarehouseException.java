package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class InvalidWarehouseException extends RuntimeException {

  public InvalidWarehouseException(String message) {
    super(message);
  }
}
