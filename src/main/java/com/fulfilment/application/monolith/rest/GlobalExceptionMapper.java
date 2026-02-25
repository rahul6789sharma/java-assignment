package com.fulfilment.application.monolith.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Fallback exception mapper for unhandled exceptions. Handles {@link WebApplicationException} by
 * using its response status (and existing response if any); all other exceptions are mapped to 500
 * with a generic JSON body. Business, validation and security exceptions are handled by their
 * dedicated mappers.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

  private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

  @Inject ObjectMapper objectMapper;

  @Override
  public Response toResponse(Exception exception) {
    if (exception instanceof WebApplicationException wae) {
      Response r = wae.getResponse();
      if (r != null && r.getStatus() > 0) {
        LOGGER.warnv("Request failed: {0}", exception.getMessage());
        return r;
      }
    }

    LOGGER.error("Request failed", exception);
    ObjectNode body = objectMapper.createObjectNode();
    body.put("exceptionType", exception.getClass().getSimpleName());
    body.put("code", 500);
    String message = exception.getMessage();
    body.put("error", message != null ? message : exception.toString());

    return Response.status(500).entity(body).build();
  }
}
