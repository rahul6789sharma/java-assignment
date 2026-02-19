package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.CapacityExceededException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.DuplicateBusinessUnitCodeException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.InsufficientCapacityException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.InvalidWarehouseException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.LocationIdentifierInvalidException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.LocationNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.MaxWarehousesReachedException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.StockMismatchException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class WarehouseExceptionMapper implements ExceptionMapper<RuntimeException> {

  private static final Logger LOGGER = Logger.getLogger(WarehouseExceptionMapper.class.getName());

  @Inject ObjectMapper objectMapper;

  @Override
  public Response toResponse(RuntimeException exception) {
    int code = mapToHttpStatus(exception);

    if (code == 0) {
      // Not a domain exception we handle — let default mappers deal with it
      return null;
    }

    LOGGER.warnv("Warehouse API error: {0}", exception.getMessage());

    ObjectNode errorJson = objectMapper.createObjectNode();
    errorJson.put("exceptionType", exception.getClass().getSimpleName());
    errorJson.put("code", code);
    errorJson.put("error", exception.getMessage());

    return Response.status(code).entity(errorJson).build();
  }

  private int mapToHttpStatus(RuntimeException exception) {
    if (exception instanceof WarehouseNotFoundException) {
      return 404;
    }
    if (exception instanceof InvalidWarehouseException
        || exception instanceof LocationNotFoundException
        || exception instanceof LocationIdentifierInvalidException
        || exception instanceof DuplicateBusinessUnitCodeException
        || exception instanceof MaxWarehousesReachedException
        || exception instanceof CapacityExceededException
        || exception instanceof InsufficientCapacityException
        || exception instanceof StockMismatchException) {
      return 400;
    }
    return 0;
  }
}
