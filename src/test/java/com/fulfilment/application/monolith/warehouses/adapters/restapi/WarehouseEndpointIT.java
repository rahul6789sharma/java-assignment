package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

/**
 * HTTP endpoint test for the warehouse API. Uses @QuarkusTest (not QuarkusIntegrationTest) so it
 * runs in-process with other tests and does not require a packaged artifact. Assertions are
 * resilient to shared DB state when tests run together.
 */
@QuarkusTest
public class WarehouseEndpointIT {

  @Test
  public void testSimpleListWarehouses() {
    // GET /warehouse returns 200 and at least one seed warehouse (import.sql has MWH.001, MWH.012,
    // MWH.023, MWH.034).
    // When run with other tests, the DB may have been modified, so we only require one seed code to
    // be present.
    given()
        .when()
        .get("warehouse")
        .then()
        .statusCode(200)
        .body(
            anyOf(
                containsString("MWH.001"),
                containsString("MWH.012"),
                containsString("MWH.023"),
                containsString("MWH.034")));
  }

  @Test
  public void testSimpleCheckingArchivingWarehouses() {

    // Uncomment the following lines to test the WarehouseResourceImpl implementation

    // final String path = "warehouse";

    // List all, should have all 3 products the database has initially:
    // given()
    //     .when()
    //     .get(path)
    //     .then()
    //     .statusCode(200)
    //     .body(
    //         containsString("MWH.001"),
    //         containsString("MWH.012"),
    //         containsString("MWH.023"),
    //         containsString("ZWOLLE-001"),
    //         containsString("AMSTERDAM-001"),
    //         containsString("TILBURG-001"));

    // // Archive the ZWOLLE-001:
    // given().when().delete(path + "/1").then().statusCode(204);

    // // List all, ZWOLLE-001 should be missing now:
    // given()
    //     .when()
    //     .get(path)
    //     .then()
    //     .statusCode(200)
    //     .body(
    //         not(containsString("ZWOLLE-001")),
    //         containsString("AMSTERDAM-001"),
    //         containsString("TILBURG-001"));
  }
}
