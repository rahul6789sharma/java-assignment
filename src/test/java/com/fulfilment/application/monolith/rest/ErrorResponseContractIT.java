package com.fulfilment.application.monolith.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

/**
 * HTTP-level integration tests verifying that exception mappers return a consistent ErrorResponse
 * contract (errorCode + message) for business, validation, and security errors. Prevents
 * regressions in the API error format.
 */
@QuarkusTest
public class ErrorResponseContractIT {

  @Test
  public void businessException_returnsErrorResponseWithCodeAndMessage() {
    // Trigger a DuplicateBusinessUnitCodeException by creating a warehouse twice
    var warehouse =
        """
        {
          "businessUnitCode": "MWH.999",
          "location": "TEST-001",
          "capacity": 100,
          "stock": 0
        }
        """;

    // First creation should succeed
    given()
        .contentType(ContentType.JSON)
        .body(warehouse)
        .when()
        .post("/warehouse")
        .then()
        .statusCode(201);

    // Second creation should fail with business exception
    given()
        .contentType(ContentType.JSON)
        .body(warehouse)
        .when()
        .post("/warehouse")
        .then()
        .statusCode(400)
        .body("errorCode", equalTo("DUPLICATE_BUSINESS_UNIT_CODE"))
        .body("message", notNullValue())
        .body("message", containsString("MWH.999"));
  }

  @Test
  public void warehouseNotFound_returnsErrorResponseWithCodeAndMessage() {
    // Trigger a WarehouseNotFoundException by requesting non-existent warehouse
    given()
        .when()
        .get("/warehouse/999999")
        .then()
        .statusCode(404)
        .body("errorCode", equalTo("WAREHOUSE_NOT_FOUND"))
        .body("message", notNullValue())
        .body("message", containsString("999999"));
  }

  @Test
  public void validationException_returnsErrorResponseWithCodeAndMessage() {
    // Trigger Bean Validation ConstraintViolationException with invalid data
    var invalidWarehouse =
        """
        {
          "businessUnitCode": "",
          "location": "",
          "capacity": -1,
          "stock": -1
        }
        """;

    given()
        .contentType(ContentType.JSON)
        .body(invalidWarehouse)
        .when()
        .post("/warehouse")
        .then()
        .statusCode(400)
        .body("errorCode", equalTo("VALIDATION_FAILED"))
        .body("message", notNullValue());
  }

  @Test
  public void insufficientCapacity_returnsErrorResponseWithCodeAndMessage() {
    // Create a warehouse with capacity < stock requirement to trigger business validation
    var warehouseWithInsufficientCapacity =
        """
        {
          "businessUnitCode": "MWH.TEST.LOW",
          "location": "TEST-002",
          "capacity": 10,
          "stock": 20
        }
        """;

    given()
        .contentType(ContentType.JSON)
        .body(warehouseWithInsufficientCapacity)
        .when()
        .post("/warehouse")
        .then()
        .statusCode(400)
        .body("errorCode", equalTo("INSUFFICIENT_CAPACITY"))
        .body("message", notNullValue())
        .body("message", containsString("10"))
        .body("message", containsString("20"));
  }

  @Test
  public void archiveNonExistentWarehouse_returnsErrorResponseWithCodeAndMessage() {
    // Trigger WarehouseNotFoundException via archive endpoint
    given()
        .when()
        .delete("/warehouse/888888")
        .then()
        .statusCode(404)
        .body("errorCode", equalTo("WAREHOUSE_NOT_FOUND"))
        .body("message", notNullValue())
        .body("message", containsString("888888"));
  }

  @Test
  public void replaceWithInsufficientCapacity_returnsErrorResponseWithCodeAndMessage() {
    // First, create a warehouse with stock
    var warehouse =
        """
        {
          "businessUnitCode": "MWH.REPLACE.TEST",
          "location": "TEST-003",
          "capacity": 100,
          "stock": 50
        }
        """;

    given()
        .contentType(ContentType.JSON)
        .body(warehouse)
        .when()
        .post("/warehouse")
        .then()
        .statusCode(201);

    // Try to replace with insufficient capacity (less than current stock)
    var replacementWithLowCapacity =
        """
        {
          "businessUnitCode": "MWH.REPLACE.TEST",
          "location": "TEST-003",
          "capacity": 30,
          "stock": 50
        }
        """;

    given()
        .contentType(ContentType.JSON)
        .body(replacementWithLowCapacity)
        .when()
        .put("/warehouse/MWH.REPLACE.TEST")
        .then()
        .statusCode(400)
        .body("errorCode", equalTo("INSUFFICIENT_CAPACITY"))
        .body("message", notNullValue())
        .body("message", containsString("30"))
        .body("message", containsString("50"));
  }
}
