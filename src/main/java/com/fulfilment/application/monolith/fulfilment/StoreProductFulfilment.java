package com.fulfilment.application.monolith.fulfilment;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Cacheable
@Table(
    name = "store_product_fulfilment",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = {"store_id", "product_id", "warehouse_id"})
    })
public class StoreProductFulfilment {

  @Id @GeneratedValue public Long id;

  @Column(name = "store_id", nullable = false)
  public Long storeId;

  @Column(name = "product_id", nullable = false)
  public Long productId;

  @Column(name = "warehouse_id", nullable = false)
  public Long warehouseId;

  public StoreProductFulfilment() {}

  public StoreProductFulfilment(Long storeId, Long productId, Long warehouseId) {
    this.storeId = storeId;
    this.productId = productId;
    this.warehouseId = warehouseId;
  }
}
