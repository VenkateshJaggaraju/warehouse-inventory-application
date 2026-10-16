package com.warehouse.inventory.WarehouseInventoryApplication.repository;

import com.warehouse.inventory.WarehouseInventoryApplication.model.StockMovement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    @EntityGraph(attributePaths = {"product", "warehouse"})
    List<StockMovement> findAllByOrderByCreatedAtDesc();
}
