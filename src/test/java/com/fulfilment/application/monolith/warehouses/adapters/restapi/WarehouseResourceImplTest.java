package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.DuplicateBusinessUnitCodeException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.warehouse.api.WarehouseResource;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@QuarkusTest
public class WarehouseResourceImplTest {

  @InjectMock
  WarehouseRepository warehouseRepository;

  @InjectMock
  CreateWarehouseOperation createWarehouseOperation;

  @Inject
  WarehouseResource warehouseResource;

  private static com.warehouse.api.beans.Warehouse apiWarehouse(
      String buCode, String location, Integer capacity, Integer stock) {
    var w = new com.warehouse.api.beans.Warehouse();
    w.setBusinessUnitCode(buCode);
    w.setLocation(location);
    w.setCapacity(capacity);
    w.setStock(stock);
    return w;
  }

  private static Warehouse domainWarehouse(
      String buCode, String location, Integer capacity, Integer stock) {
    var w = new Warehouse();
    w.businessUnitCode = buCode;
    w.location = location;
    w.capacity = capacity;
    w.stock = stock;
    return w;
  }

  @Test
  public void listAllWarehousesUnits_returnsMappedListFromRepository() {
    var d1 = domainWarehouse("MWH.001", "ZWOLLE-001", 100, 10);
    var d2 = domainWarehouse("MWH.012", "AMSTERDAM-001", 50, 5);
    when(warehouseRepository.getAll()).thenReturn(List.of(d1, d2));

    List<com.warehouse.api.beans.Warehouse> result = warehouseResource.listAllWarehousesUnits();

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("MWH.001", result.get(0).getBusinessUnitCode());
    assertEquals("ZWOLLE-001", result.get(0).getLocation());
    assertEquals(100, result.get(0).getCapacity());
    assertEquals(10, result.get(0).getStock());
    assertEquals("MWH.012", result.get(1).getBusinessUnitCode());
    assertEquals("AMSTERDAM-001", result.get(1).getLocation());
    verify(warehouseRepository).getAll();
  }

  @Test
  public void listAllWarehousesUnits_returnsEmptyListWhenRepositoryReturnsEmpty() {
    when(warehouseRepository.getAll()).thenReturn(List.of());

    List<com.warehouse.api.beans.Warehouse> result = warehouseResource.listAllWarehousesUnits();

    assertNotNull(result);
    assertEquals(0, result.size());
    verify(warehouseRepository).getAll();
  }

  @Test
  public void createANewWarehouseUnit_callsUseCaseWithMappedDomainAndReturnsMappedResponse() {
    var request = apiWarehouse("MWH.NEW", "AMSTERDAM-001", 30, 5);

    var response = warehouseResource.createANewWarehouseUnit(request);

    ArgumentCaptor<Warehouse> captor = ArgumentCaptor.forClass(Warehouse.class);
    verify(createWarehouseOperation).create(captor.capture());
    var passed = captor.getValue();
    assertEquals("MWH.NEW", passed.businessUnitCode);
    assertEquals("AMSTERDAM-001", passed.location);
    assertEquals(30, passed.capacity);
    assertEquals(5, passed.stock);

    assertNotNull(response);
    assertEquals("MWH.NEW", response.getBusinessUnitCode());
    assertEquals("AMSTERDAM-001", response.getLocation());
    assertEquals(30, response.getCapacity());
    assertEquals(5, response.getStock());
  }

  @Test
  public void createANewWarehouseUnit_returnsResponseWithSameDataAsRequest() {
    var request = apiWarehouse("MWH.TEST", "TILBURG-001", 40, 20);

    var response = warehouseResource.createANewWarehouseUnit(request);

    assertNotNull(response);
    assertEquals("MWH.TEST", response.getBusinessUnitCode());
    assertEquals("TILBURG-001", response.getLocation());
    assertEquals(40, response.getCapacity());
    assertEquals(20, response.getStock());
    verify(createWarehouseOperation).create(any(Warehouse.class));
  }

  @Test
  public void createANewWarehouseUnit_whenUseCaseThrowsDuplicateBuCode_exceptionPropagates() {
    var request = apiWarehouse("MWH.001", "AMSTERDAM-001", 30, 5);
    doThrow(new DuplicateBusinessUnitCodeException("MWH.001"))
        .when(createWarehouseOperation).create(any(Warehouse.class));

    assertThrows(
        DuplicateBusinessUnitCodeException.class,
        () -> warehouseResource.createANewWarehouseUnit(request));

    verify(createWarehouseOperation).create(any(Warehouse.class));
  }

  @Test
  public void getAWarehouseUnitByID_throwsUnsupportedOperation() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> warehouseResource.getAWarehouseUnitByID("1"));
  }

  @Test
  public void archiveAWarehouseUnitByID_throwsUnsupportedOperation() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> warehouseResource.archiveAWarehouseUnitByID("1"));
  }

  @Test
  public void replaceTheCurrentActiveWarehouse_throwsUnsupportedOperation() {
    var data = apiWarehouse("MWH.001", "AMSTERDAM-001", 50, 10);
    assertThrows(
        UnsupportedOperationException.class,
        () -> warehouseResource.replaceTheCurrentActiveWarehouse("MWH.001", data));
  }
}
