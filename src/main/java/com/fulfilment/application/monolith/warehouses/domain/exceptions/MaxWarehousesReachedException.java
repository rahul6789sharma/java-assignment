package com.fulfilment.application.monolith.warehouses.domain.exceptions;

import com.fulfilment.application.monolith.exception.BusinessException;

public class MaxWarehousesReachedException extends BusinessException {

  public static final String ERROR_CODE = "MAX_WAREHOUSES_REACHED";

  public MaxWarehousesReachedException(String location) {
    super("Maximum number of warehouses reached for location: " + location, ERROR_CODE, 400);
  }
}
