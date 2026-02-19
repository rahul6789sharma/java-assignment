package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.InsufficientCapacityException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.StockMismatchException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ReplaceWarehouseUseCaseTest {

  @InjectMock
  WarehouseStore warehouseStore;

  @InjectMock
  LocationResolver locationResolver;

  @Inject
  ReplaceWarehouseUseCase useCase;

  private static Warehouse warehouse(String buCode, String location, int capacity, int stock) {
    var w = new Warehouse();
    w.businessUnitCode = buCode;
    w.location = location;
    w.capacity = capacity;
    w.stock = stock;
    return w;
  }

  @Test
  void replace_whenCurrentNotFound_throwsWarehouseNotFoundException() {
    Warehouse newWh = warehouse("MWH.001", "AMSTERDAM-001", 100, 50);
    when(warehouseStore.findActiveByBusinessUnitCode("MWH.001")).thenReturn(null);

    assertThrows(WarehouseNotFoundException.class, () -> useCase.replace(newWh));
    verify(warehouseStore, never()).update(any());
    verify(warehouseStore, never()).create(any());
  }

  @Test
  void replace_whenNewCapacityLessThanOldStock_throwsInsufficientCapacityException() {
    Warehouse current = warehouse("MWH.001", "ZWOLLE-001", 100, 80);
    Warehouse newWh = warehouse("MWH.001", "AMSTERDAM-001", 50, 80); // capacity 50 < stock 80
    when(warehouseStore.findActiveByBusinessUnitCode("MWH.001")).thenReturn(current);

    assertThrows(InsufficientCapacityException.class, () -> useCase.replace(newWh));
    verify(warehouseStore, never()).update(any());
    verify(warehouseStore, never()).create(any());
  }

  @Test
  void replace_whenNewStockDoesNotMatchOldStock_throwsStockMismatchException() {
    Warehouse current = warehouse("MWH.001", "ZWOLLE-001", 100, 50);
    Warehouse newWh = warehouse("MWH.001", "AMSTERDAM-001", 100, 30); // stock 30 != 50
    when(warehouseStore.findActiveByBusinessUnitCode("MWH.001")).thenReturn(current);
    when(locationResolver.resolveByIdentifier("AMSTERDAM-001"))
        .thenReturn(new Location("AMSTERDAM-001", 5, 100));
    when(warehouseStore.countActiveByLocation("AMSTERDAM-001")).thenReturn(0L);
    when(warehouseStore.totalCapacityByLocation("AMSTERDAM-001")).thenReturn(0);

    assertThrows(StockMismatchException.class, () -> useCase.replace(newWh));
    verify(warehouseStore, never()).update(any());
    verify(warehouseStore, never()).create(any());
  }

  @Test
  void replace_whenValid_archivesCurrentAndCreatesNew() {
    Warehouse current = warehouse("MWH.001", "ZWOLLE-001", 100, 50);
    Warehouse newWh = warehouse("MWH.001", "AMSTERDAM-001", 80, 50); // same stock, capacity >= stock
    when(warehouseStore.findActiveByBusinessUnitCode("MWH.001")).thenReturn(current);
    when(locationResolver.resolveByIdentifier("AMSTERDAM-001"))
        .thenReturn(new Location("AMSTERDAM-001", 5, 100));
    when(warehouseStore.countActiveByLocation("AMSTERDAM-001")).thenReturn(1L);
    when(warehouseStore.totalCapacityByLocation("AMSTERDAM-001")).thenReturn(20); // 20 + 80 <= 100

    useCase.replace(newWh);

    verify(warehouseStore).update(current);
    verify(warehouseStore).create(newWh);
    assert current.archivedAt != null;
    assert newWh.createdAt != null;
    assert newWh.archivedAt == null;
  }
}
