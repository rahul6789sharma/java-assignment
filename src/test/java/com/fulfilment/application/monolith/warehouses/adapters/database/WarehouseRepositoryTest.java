package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WarehouseRepositoryTest {

  @Inject WarehouseRepository warehouseRepository;

  @Test
  public void testGetAllReturnsSeedData() {
    List<Warehouse> warehouses = warehouseRepository.getAll();

    // import.sql seeds at least 3 warehouses (other tests may add more)
    assertTrue(warehouses.size() >= 3);
  }

  @Test
  public void testFindByBusinessUnitCodeReturnsExisting() {
    // MWH.001 exists in import.sql
    Warehouse warehouse = warehouseRepository.findByBusinessUnitCode("MWH.001");

    assertNotNull(warehouse);
    assertEquals("MWH.001", warehouse.businessUnitCode);
    assertEquals("ZWOLLE-001", warehouse.location);
    assertEquals(100, warehouse.capacity);
    assertEquals(10, warehouse.stock);
  }

  @Test
  public void testFindByBusinessUnitCodeReturnsNullForNonExistent() {
    Warehouse warehouse = warehouseRepository.findByBusinessUnitCode("NON_EXISTENT");

    assertNull(warehouse);
  }

  @Test
  @Transactional
  public void testCreateAndFindWarehouse() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.TEST.001";
    warehouse.location = "AMSTERDAM-001";
    warehouse.capacity = 60;
    warehouse.stock = 10;
    warehouse.createdAt = LocalDateTime.now();
    warehouse.archivedAt = null;

    warehouseRepository.create(warehouse);

    Warehouse found = warehouseRepository.findByBusinessUnitCode("MWH.TEST.001");
    assertNotNull(found);
    assertEquals("MWH.TEST.001", found.businessUnitCode);
    assertEquals("AMSTERDAM-001", found.location);
    assertEquals(60, found.capacity);
    assertEquals(10, found.stock);
    assertNotNull(found.createdAt);
    assertNull(found.archivedAt);
  }

  @Test
  @Transactional
  public void testUpdateWarehouse() {
    // MWH.012 exists in import.sql with capacity=50, stock=5
    Warehouse warehouse = warehouseRepository.findByBusinessUnitCode("MWH.012");
    assertNotNull(warehouse);

    warehouse.capacity = 80;
    warehouse.stock = 15;
    warehouseRepository.update(warehouse);

    Warehouse updated = warehouseRepository.findByBusinessUnitCode("MWH.012");
    assertNotNull(updated);
    assertEquals(80, updated.capacity);
    assertEquals(15, updated.stock);
  }

  @Test
  @Transactional
  public void testRemoveWarehouse() {
    // Create one first, then remove it
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.TEST.REMOVE";
    warehouse.location = "EINDHOVEN-001";
    warehouse.capacity = 30;
    warehouse.stock = 5;
    warehouse.createdAt = LocalDateTime.now();
    warehouse.archivedAt = null;

    warehouseRepository.create(warehouse);

    Warehouse found = warehouseRepository.findByBusinessUnitCode("MWH.TEST.REMOVE");
    assertNotNull(found);

    warehouseRepository.remove(found);

    Warehouse removed = warehouseRepository.findByBusinessUnitCode("MWH.TEST.REMOVE");
    assertNull(removed);
  }
}
