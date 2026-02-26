package com.fulfilment.application.monolith.warehouses.domain.exceptions;

import com.fulfilment.application.monolith.exception.BusinessException;

public class LocationIdentifierInvalidException extends BusinessException {

  public static final String ERROR_CODE = "LOCATION_IDENTIFIER_INVALID";

  public LocationIdentifierInvalidException() {
    super("Location identifier must not be null or blank", ERROR_CODE, 400);
  }
}
