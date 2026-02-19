package com.fulfilment.application.monolith.stores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import org.jboss.logging.Logger;

@Path("store")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class StoreResource {

  @Inject Event<StoreEvent> storeEvent;

  private static final Logger LOGGER = Logger.getLogger(StoreResource.class.getName());

  @GET
  public List<Store> get() {
    return Store.listAll(Sort.by("name"));
  }

  @GET
  @Path("{id}")
  public Store getSingle(@PathParam("id") Long id) {
    Store entity = Store.findById(id);
    if (entity == null) {
      throw new WebApplicationException(
          Response.status(404).entity("Store with id of " + id + " does not exist.").build());
    }
    return entity;
  }

  @POST
  @Transactional
  public Response create(Store store) {
    if (store.id != null) {
      throw new WebApplicationException(
          Response.status(422).entity("Id was invalidly set on request.").build());
    }
    if (store.name == null || store.name.isBlank()) {
      throw new WebApplicationException(
          Response.status(422).entity("Store name is required.").build());
    }
    if (Store.find("name", store.name).firstResult() != null) {
      throw new WebApplicationException(
          Response.status(409)
              .entity("Store with name '" + store.name + "' already exists.")
              .build());
    }

    store.persist();

    storeEvent.fire(new StoreEvent(store, StoreEvent.ActionType.CREATED));

    return Response.ok(store).status(201).build();
  }

  @PUT
  @Path("{id}")
  @Transactional
  public Store update(@PathParam("id") Long id, Store updatedStore) {
    if (updatedStore.name == null) {
      throw new WebApplicationException(
          Response.status(422).entity("Store Name was not set on request.").build());
    }

    Store entity = Store.findById(id);

    if (entity == null) {
      throw new WebApplicationException(
          Response.status(404).entity("Store with id of " + id + " does not exist.").build());
    }

    entity.name = updatedStore.name;
    entity.quantityProductsInStock = updatedStore.quantityProductsInStock;

    storeEvent.fire(new StoreEvent(entity, StoreEvent.ActionType.UPDATED));

    return entity;
  }

  @PATCH
  @Path("{id}")
  @Transactional
  public Store patch(@PathParam("id") Long id, Store updatedStore) {
    if (updatedStore.name == null) {
      throw new WebApplicationException(
          Response.status(422).entity("Store Name was not set on request.").build());
    }

    Store entity = Store.findById(id);

    if (entity == null) {
      throw new WebApplicationException(
          Response.status(404).entity("Store with id of " + id + " does not exist.").build());
    }

    if (entity.name != null) {
      entity.name = updatedStore.name;
    }

    if (entity.quantityProductsInStock != 0) {
      entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
    }

    storeEvent.fire(new StoreEvent(entity, StoreEvent.ActionType.UPDATED));

    return entity;
  }

  @DELETE
  @Path("{id}")
  @Transactional
  public Response delete(@PathParam("id") Long id) {
    Store entity = Store.findById(id);
    if (entity == null) {
      throw new WebApplicationException(
          Response.status(404).entity("Store with id of " + id + " does not exist.").build());
    }
    entity.delete();
    return Response.status(204).build();
  }

  @Provider
  public static class ErrorMapper implements ExceptionMapper<Exception> {

    @Inject ObjectMapper objectMapper;

    @Override
    public Response toResponse(Exception exception) {
      LOGGER.error("Failed to handle request", exception);

      int code = 500;
      if (exception instanceof WebApplicationException) {
        code = ((WebApplicationException) exception).getResponse().getStatus();
      }

      ObjectNode exceptionJson = objectMapper.createObjectNode();
      exceptionJson.put("exceptionType", exception.getClass().getName());
      exceptionJson.put("code", code);

      if (exception.getMessage() != null) {
        exceptionJson.put("error", exception.getMessage());
      }

      return Response.status(code).entity(exceptionJson).build();
    }
  }
}
