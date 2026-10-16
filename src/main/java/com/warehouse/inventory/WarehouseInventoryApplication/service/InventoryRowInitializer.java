package com.warehouse.inventory.WarehouseInventoryApplication.service;

import com.warehouse.inventory.WarehouseInventoryApplication.model.Inventory;
import com.warehouse.inventory.WarehouseInventoryApplication.repository.InventoryRepository;
import com.warehouse.inventory.WarehouseInventoryApplication.repository.ProductRepository;
import com.warehouse.inventory.WarehouseInventoryApplication.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates the (product, warehouse) inventory row in its OWN transaction so that a
 * unique-constraint race between two requests cannot poison the caller's transaction.
 * The caller catches DataIntegrityViolationException (= someone else created it first).
 * Database-agnostic replacement for a native "INSERT ... ON CONFLICT DO NOTHING".
 */
@Component
@RequiredArgsConstructor
public class InventoryRowInitializer {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createIfMissing(Long productId, Long warehouseId) {
        if (inventoryRepository.findByProduct_IdAndWarehouse_Id(productId, warehouseId).isPresent()) {
            return;
        }
        inventoryRepository.saveAndFlush(Inventory.builder()
                .product(productRepository.getReferenceById(productId))
                .warehouse(warehouseRepository.getReferenceById(warehouseId))
                .quantity(0)
                .build());
    }
}
