package com.fulfilment.application.monolith.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * REST-level tests for exception mappers. Verifies that when the application throws business (and
 * other) exceptions, the HTTP response has the correct status and {@link ErrorResponse} body
 * (errorCode + message). These tests hit the real HTTP layer (RestAssured) so that
 * BusinessExceptionMapper, ValidationExceptionMapper, etc. are exercised.
 */
@QuarkusTest
class ExceptionHandlingResourceTest {

  @Nested
  @DisplayName("BusinessExceptionMapper – WarehouseNotFoundException (404)")
  class WarehouseNotFound {

    @Test
    void getWarehouseByNonExistentId_returns404_withErrorResponse() {
      given()
          .when()
          .get("warehouse/99999")
          .then()
          .statusCode(404)
          .contentType(MediaType.APPLICATION_JSON)
          .body("errorCode", equalTo("WAREHOUSE_NOT_FOUND"))
          .body("message", containsString("99999"));
    }

    @Test
    void archiveWarehouseByNonExistentId_returns404_withErrorResponse() {
      given()
          .when()
          .delete("warehouse/99999")
          .then()
          .statusCode(404)
          .contentType(MediaType.APPLICATION_JSON)
          .body("errorCode", equalTo("WAREHOUSE_NOT_FOUND"))
          .body("message", containsString("99999"));
    }

    @Test
    void getWarehouseByInvalidIdFormat_returns404_withErrorResponse() {
      given()
          .when()
          .get("warehouse/abc")
          .then()
          .statusCode(404)
          .contentType(MediaType.APPLICATION_JSON)
          .body("errorCode", equalTo("WAREHOUSE_NOT_FOUND"))
          .body("message", containsString("-1"));
    }
  }

  @Nested
  @DisplayName("BusinessExceptionMapper – FulfilmentConstraintException (400)")
  class FulfilmentConstraint {

    @Test
    void assignFulfilment_withNonExistentStore_returns400_withErrorResponse() {
      given()
          .contentType(MediaType.APPLICATION_JSON)
          .when()
          .post("fulfilment/store/999/product/1/warehouse/1")
          .then()
          .statusCode(400)
          .contentType(MediaType.APPLICATION_JSON)
          .body("errorCode", equalTo("FULFILMENT_CONSTRAINT"))
          .body("message", containsString("Store not found"));
    }
  }

  @Nested
  @DisplayName("BusinessExceptionMapper – DuplicateBusinessUnitCodeException (400)")
  class DuplicateBusinessUnitCode {

    @Test
    void createWarehouse_withExistingBusinessUnitCode_returns400_withErrorResponse() {
      // MWH.001 already exists in import.sql
      var body =
          "{\"businessUnitCode\":\"MWH.001\",\"location\":\"ZWOLLE-001\",\"capacity\":50,\"stock\":5}";
      given()
          .contentType(MediaType.APPLICATION_JSON)
          .body(body)
          .when()
          .post("warehouse")
          .then()
          .statusCode(400)
          .contentType(MediaType.APPLICATION_JSON)
          .body("errorCode", equalTo("DUPLICATE_BUSINESS_UNIT_CODE"))
          .body("message", containsString("MWH.001"));
    }
  }
}
