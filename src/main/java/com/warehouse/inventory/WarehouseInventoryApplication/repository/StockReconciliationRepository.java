package com.warehouse.inventory.WarehouseInventoryApplication.repository;


import com.warehouse.inventory.WarehouseInventoryApplication.model.StockReconciliation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockReconciliationRepository
        extends JpaRepository<StockReconciliation, Long> {

    List<StockReconciliation> findByDiscrepancyNot(int discrepancy);
}

