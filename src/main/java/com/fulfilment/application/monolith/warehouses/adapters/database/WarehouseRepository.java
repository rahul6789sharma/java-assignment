package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.jboss.logging.Logger;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  private static final Logger LOGGER = Logger.getLogger(WarehouseRepository.class.getName());

  @Override
  public List<Warehouse> getAll() {
    return this.listAll().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    LOGGER.infov("Creating warehouse with business unit code: {0}", warehouse.businessUnitCode);
    DbWarehouse entity = new DbWarehouse();
    entity.businessUnitCode = warehouse.businessUnitCode;
    entity.location = warehouse.location;
    entity.capacity = warehouse.capacity;
    entity.stock = warehouse.stock;
    entity.createdAt = warehouse.createdAt;
    entity.archivedAt = warehouse.archivedAt;
    persist(entity);
  }

  @Override
  public void update(Warehouse warehouse) {
    LOGGER.infov("Updating warehouse with business unit code: {0}", warehouse.businessUnitCode);
    DbWarehouse entity =
        find("businessUnitCode", warehouse.businessUnitCode).firstResult();
    if (entity == null) {
      LOGGER.warnv("Warehouse not found for update: {0}", warehouse.businessUnitCode);
      return;
    }
    entity.location = warehouse.location;
    entity.capacity = warehouse.capacity;
    entity.stock = warehouse.stock;
    entity.createdAt = warehouse.createdAt;
    entity.archivedAt = warehouse.archivedAt;
    persist(entity);
  }

  @Override
  public void remove(Warehouse warehouse) {
    LOGGER.infov("Removing warehouse with business unit code: {0}", warehouse.businessUnitCode);
    DbWarehouse entity =
        find("businessUnitCode", warehouse.businessUnitCode).firstResult();
    if (entity != null) {
      delete(entity);
    } else {
      LOGGER.warnv("Warehouse not found for removal: {0}", warehouse.businessUnitCode);
    }
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    LOGGER.infov("Finding warehouse by business unit code: {0}", buCode);
    DbWarehouse entity = find("businessUnitCode", buCode).firstResult();
    if (entity == null) {
      return null;
    }
    return entity.toWarehouse();
  }
}
