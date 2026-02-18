package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.jboss.logging.Logger;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  private static final Logger LOGGER = Logger.getLogger(WarehouseResourceImpl.class.getName());

  @Inject private WarehouseRepository warehouseRepository;

  @Inject private CreateWarehouseOperation createWarehouseOperation;

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @Override
  @Transactional
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    LOGGER.infov("Creating warehouse unit: buCode={0}, location={1}", data.getBusinessUnitCode(), data.getLocation());

    var warehouse = toDomainModel(data);
    createWarehouseOperation.create(warehouse);

    return toWarehouseResponse(warehouse);
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAWarehouseUnitByID'");
  }

  @Override
  public void archiveAWarehouseUnitByID(String id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'archiveAWarehouseUnitByID'");
  }

  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode, @NotNull Warehouse data) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
        "Unimplemented method 'replaceTheCurrentActiveWarehouse'");
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toDomainModel(
      Warehouse data) {
    var warehouse = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    warehouse.businessUnitCode = data.getBusinessUnitCode();
    warehouse.location = data.getLocation();
    warehouse.capacity = data.getCapacity();
    warehouse.stock = data.getStock();
    return warehouse;
  }

  private Warehouse toWarehouseResponse(
      com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);

    return response;
  }
}
