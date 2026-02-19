package com.fulfilment.application.monolith.fulfilment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class FulfilmentExceptionMapper implements ExceptionMapper<FulfilmentConstraintException> {

  private static final Logger LOGGER = Logger.getLogger(FulfilmentExceptionMapper.class.getName());

  @Inject ObjectMapper objectMapper;

  @Override
  public Response toResponse(FulfilmentConstraintException exception) {
    LOGGER.warnv("Fulfilment constraint error: {0}", exception.getMessage());
    ObjectNode errorJson = objectMapper.createObjectNode();
    errorJson.put("exceptionType", exception.getClass().getSimpleName());
    errorJson.put("code", 400);
    errorJson.put("error", exception.getMessage());
    return Response.status(400).entity(errorJson).build();
  }
}
