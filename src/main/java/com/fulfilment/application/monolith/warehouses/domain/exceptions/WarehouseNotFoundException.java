package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class WarehouseNotFoundException extends RuntimeException {

  public WarehouseNotFoundException(String businessUnitCode) {
    super("Warehouse not found with business unit code: " + businessUnitCode);
  }

  public WarehouseNotFoundException(Long id) {
    super("Warehouse not found with id: " + id);
  }
}
