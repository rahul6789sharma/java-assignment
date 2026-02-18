package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class StockMismatchException extends RuntimeException {

  public StockMismatchException(int newStock, int expectedStock) {
    super(
        "New warehouse stock "
            + newStock
            + " does not match the expected stock of "
            + expectedStock);
  }
}
