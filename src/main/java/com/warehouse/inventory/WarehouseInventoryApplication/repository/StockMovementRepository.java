package com.warehouse.inventory.WarehouseInventoryApplication.repository;


import com.warehouse.inventory.WarehouseInventoryApplication.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository
        extends JpaRepository<StockMovement, Long> {
}

