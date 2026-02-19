# Testing

## Overview

The project uses **JUnit 5** and **Quarkus Test** for unit and integration tests. **JaCoCo** is used for code coverage (target **80%** line coverage). Tests cover positive flows, negative/validation cases, and error conditions.

## Running tests

```bash
./mvnw test          # Unit tests only
./mvnw verify        # Tests + JaCoCo report and coverage check (80% minimum)
```

Tests run against an embedded or Testcontainers PostgreSQL when needed (Quarkus Dev Services).

## Code coverage (JaCoCo)

- **Report:** Generated at `target/jacoco-report/index.html` after `mvn verify`.
- **Minimum:** 80% line coverage (enforced in `verify` phase).
- **Exclusions:** Generated OpenAPI code (`com.warehouse.api`), DTOs/beans with no logic.
- **CI:** Coverage is collected and the JaCoCo report is uploaded as an artifact in GitHub Actions.

## Test structure

| Layer / Area | Test class | Focus |
|--------------|------------|--------|
| **Location** | `LocationGatewayTest` | `resolveByIdentifier` – valid id, null/blank, unknown (positive + error). |
| **Store** | `StoreResourceTest` | REST: list, get, create (201/422/409), update, patch, delete (positive + validation). |
| **Store** | `LegacyStoreManagerGatewayTest` | Legacy sync behaviour. |
| **Product** | `ProductEndpointTest` | REST: CRUD, create valid (201), validations (422). |
| **Warehouse – API** | `WarehouseResourceImplTest` | List, create, get, archive, replace; mapping and use-case delegation (incl. error: duplicate BU, not found). |
| **Warehouse – Use cases** | `CreateWarehouseUseCaseTest` | Create: valid, duplicate BU, invalid location, max warehouses, capacity/stock (positive + constraints). |
| | `ReplaceWarehouseUseCaseTest` | Replace: valid, not found, capacity/stock validations. |
| | `ArchiveWarehouseUseCaseTest` | Archive: success, not found. |
| **Warehouse – DB** | `WarehouseRepositoryTest` | Repository behaviour with DB. |
| **Fulfilment** | `FulfilmentServiceTest` | Assign/unassign, list; store/product/warehouse not found; max 2 wh per product per store, max 3 wh per store, max 5 product types per warehouse (positive + constraints). |
| **Fulfilment** | `FulfilmentResourceTest` | REST: assign (204), unassign, list; Content-Type for assign. |

## Types of tests

- **Positive:** Valid inputs and expected success (200/201/204, correct body or no content).
- **Negative / validation:** Invalid or out-of-constraint inputs (400, 404, 409, 422) and correct error messages or status.
- **Error:** Not found, invalid id, missing required fields, duplicate keys, constraint violations.

## API test script (manual / in-depth)

For full API coverage against a running app (e.g. on port 8080):

```bash
./scripts/api-test.sh http://localhost:8080
```

Covers Store, Product, Warehouse, and Fulfilment endpoints including all documented constraints. See `scripts/README.md`.
