package com.fulfilment.application.monolith.rest;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps Bean Validation {@link ConstraintViolationException} to 400 with a consistent {@link
 * ErrorResponse}. Message aggregates the first constraint violation or a generic validation message.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

  private static final String ERROR_CODE = "VALIDATION_FAILED";

  @Override
  public Response toResponse(ConstraintViolationException ex) {
    String message =
        ex.getConstraintViolations().isEmpty()
            ? "Validation failed"
            : ex.getConstraintViolations().iterator().next().getMessage();
    ErrorResponse error = new ErrorResponse(ERROR_CODE, message);
    return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
  }
}
