package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
public class StoreResourceTest {

  @InjectMock LegacyStoreManagerGateway legacyStoreManagerGateway;

  @BeforeEach
  public void setup() {
    Mockito.reset(legacyStoreManagerGateway);
  }

  @Test
  public void testLegacyGatewayCalledOnSuccessfulCreate() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"NEW_UNIQUE_STORE\", \"quantityProductsInStock\": 5}")
        .when()
        .post("store")
        .then()
        .statusCode(201)
        .body(containsString("NEW_UNIQUE_STORE"));

    Mockito.verify(legacyStoreManagerGateway).createStoreOnLegacySystem(Mockito.any());
  }

  @Test
  public void testLegacyGatewayNotCalledOnRollback() {
    // "TONSTAD" already exists in import.sql → duplicate name → 409 (or 500 if constraint fires). No commit → legacy not called.
    given()
        .contentType("application/json")
        .body("{\"name\": \"TONSTAD\", \"quantityProductsInStock\": 99}")
        .when()
        .post("store");

    // AFTER_SUCCESS observer runs only after commit; duplicate must not trigger legacy sync
    Mockito.verify(legacyStoreManagerGateway, Mockito.never())
        .createStoreOnLegacySystem(Mockito.any());
  }

  @Test
  public void testLegacyGatewayCalledOnSuccessfulUpdate() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"TONSTAD_UPDATED\", \"quantityProductsInStock\": 20}")
        .when()
        .put("store/1")
        .then()
        .statusCode(200)
        .body(containsString("TONSTAD_UPDATED"));

    Mockito.verify(legacyStoreManagerGateway).updateStoreOnLegacySystem(Mockito.any());
  }
}
