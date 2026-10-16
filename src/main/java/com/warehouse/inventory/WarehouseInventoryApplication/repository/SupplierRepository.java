package com.warehouse.inventory.WarehouseInventoryApplication.repository;

import com.warehouse.inventory.WarehouseInventoryApplication.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
