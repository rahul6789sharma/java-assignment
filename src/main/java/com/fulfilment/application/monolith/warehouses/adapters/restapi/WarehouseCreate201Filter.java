package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Ensures POST /warehouse returns 201 Created per OpenAPI spec. The generated WarehouseResource
 * interface returns an entity, so JAX-RS uses 200 by default; this filter corrects the status.
 */
@Provider
public class WarehouseCreate201Filter implements ContainerResponseFilter {

  @Override
  public void filter(
      ContainerRequestContext requestContext, ContainerResponseContext responseContext)
      throws IOException {
    if ("POST".equals(requestContext.getMethod())
        && "warehouse".equals(requestContext.getUriInfo().getPath())
        && responseContext.getStatus() == Response.Status.OK.getStatusCode()) {
      responseContext.setStatus(Response.Status.CREATED.getStatusCode());
    }
  }
}
