package com.fulfilment.application.monolith.warehouses.domain.exceptions;

import com.fulfilment.application.monolith.exception.BusinessException;

public class InvalidWarehouseException extends BusinessException {

  public static final String ERROR_CODE = "INVALID_WAREHOUSE";

  public InvalidWarehouseException(String message) {
    super(message, ERROR_CODE, 400);
  }
}
