package com.fulfilment.application.monolith.warehouses.domain.exceptions;

public class DuplicateBusinessUnitCodeException extends RuntimeException {

  public DuplicateBusinessUnitCodeException(String businessUnitCode) {
    super("Business unit code already exists: " + businessUnitCode);
  }
}
