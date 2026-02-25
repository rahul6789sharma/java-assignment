package com.fulfilment.application.monolith.rest;

import com.fulfilment.application.monolith.exception.BusinessException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Single mapper for all business exceptions. Returns a consistent {@link ErrorResponse} (errorCode +
 * message) with the HTTP status defined on the exception.
 */
@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

  @Override
  public Response toResponse(BusinessException ex) {
    ErrorResponse error = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
    return Response.status(ex.getStatus()).entity(error).build();
  }
}
