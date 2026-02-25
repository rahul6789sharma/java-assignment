package com.fulfilment.application.monolith.rest;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps JAX-RS security-related exceptions (401 Unauthorized, 403 Forbidden) to responses with a
 * consistent {@link ErrorResponse} body. Other {@link WebApplicationException}s are passed through
 * via the existing response.
 */
@Provider
public class SecurityExceptionMapper implements ExceptionMapper<WebApplicationException> {

  private static final String CODE_UNAUTHORIZED = "UNAUTHORIZED";
  private static final String CODE_FORBIDDEN = "FORBIDDEN";

  @Override
  public Response toResponse(WebApplicationException ex) {
    if (ex instanceof NotAuthorizedException) {
      String message = ex.getMessage() != null ? ex.getMessage() : "Unauthorized";
      return Response.status(Response.Status.UNAUTHORIZED)
          .entity(new ErrorResponse(CODE_UNAUTHORIZED, message))
          .build();
    }
    if (ex instanceof ForbiddenException) {
      String message = ex.getMessage() != null ? ex.getMessage() : "Forbidden";
      return Response.status(Response.Status.FORBIDDEN)
          .entity(new ErrorResponse(CODE_FORBIDDEN, message))
          .build();
    }
    return ex.getResponse();
  }
}
