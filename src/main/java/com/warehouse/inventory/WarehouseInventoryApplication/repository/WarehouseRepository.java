package com.warehouse.inventory.WarehouseInventoryApplication.repository;

import com.warehouse.inventory.WarehouseInventoryApplication.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}
