package com.warehouse.inventory.WarehouseInventoryApplication.service.impl;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.DispatchRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.ReceiptRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.ReconciliationRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.TransferRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.InventoryResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.ReconciliationResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.StockMovementResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.enums.MovementType;
import com.warehouse.inventory.WarehouseInventoryApplication.mapper.InventoryMapper;
import com.warehouse.inventory.WarehouseInventoryApplication.mapper.ReconciliationMapper;
import com.warehouse.inventory.WarehouseInventoryApplication.mapper.StockMovementMapper;
import com.warehouse.inventory.WarehouseInventoryApplication.model.*;
import com.warehouse.inventory.WarehouseInventoryApplication.repository.*;
import com.warehouse.inventory.WarehouseInventoryApplication.service.InventoryRowInitializer;
import com.warehouse.inventory.WarehouseInventoryApplication.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private StockMovementRepository movementRepository;

    @Autowired
    private StockReconciliationRepository reconciliationRepository;

    @Autowired
    private InventoryRowInitializer rowInitializer;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private StockMovementMapper movementMapper;

    @Autowired
    private ReconciliationMapper reconciliationMapper;

    // ---------------------------------------------------------------- queries

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getInventory() {
        return inventoryRepository
                .findAllWithDetails()
                .stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getLowStock() {
        return inventoryRepository
                .findLowStock()
                .stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> getMovements() {
        return movementRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(movementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReconciliationResponseDTO> getDiscrepancies() {
        return reconciliationRepository
                .findByDiscrepancyNotOrderByCreatedAtDesc(0)
                .stream()
                .map(reconciliationMapper::toResponse)
                .toList();
    }

    // --------------------------------------------------------------- commands

    @Override
    public ResponseEntity<StockMovementResponseDTO> receive(ReceiptRequestDTO dto) {
        Product product = product(dto.getProductId());
        Warehouse warehouse = warehouse(dto.getWarehouseId());
        if (dto.getSupplierId() != null &&
                !supplierRepository.existsById(dto.getSupplierId())) {
            throw notFound("Supplier", dto.getSupplierId());
        }

        Inventory inventory = lockedInventoryOrCreate(product, warehouse);
        int before = inventory.getQuantity();
        inventory.setQuantity(Math.addExact(before, dto.getQuantity()));
        inventoryRepository.save(inventory);

        StockMovement stockMovement = movement(product, warehouse, MovementType.RECEIPT, dto.getQuantity(), before,
                inventory.getQuantity(), dto.getTransactionReference());

        stockMovement.setSupplierId(dto.getSupplierId());
        stockMovement.setDestinationWarehouseId(warehouse.getId());

        StockMovementResponseDTO response = movementMapper.toResponse(movementRepository.save(stockMovement));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Override
    public ResponseEntity<List<StockMovementResponseDTO>> transfer(TransferRequestDTO dto) {
        if (dto.getSourceWarehouseId().equals(dto.getDestinationWarehouseId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Source and destination warehouses must differ"
            );
        }

        Product product = product(dto.getProductId());
        Warehouse sourceWarehouse = warehouse(dto.getSourceWarehouseId());
        Warehouse destinationWarehouse = warehouse(dto.getDestinationWarehouseId());

        // Make sure the destination row exists, then lock both rows in a stable
        // warehouse-id order to avoid deadlocks between opposite transfers.
        ensureInventoryRow(product.getId(), destinationWarehouse.getId());
        Long first = Math.min(sourceWarehouse.getId(), destinationWarehouse.getId());
        Long second = Math.max(sourceWarehouse.getId(), destinationWarehouse.getId());
        Inventory firstInventory = inventoryRepository.findLocked(product.getId(), first).orElse(null);
        Inventory secondInventory = inventoryRepository.findLocked(product.getId(), second).orElse(null);
        Inventory sourceInventory = sourceWarehouse.getId().equals(first) ? firstInventory : secondInventory;
        Inventory destinationInventory = destinationWarehouse.getId().equals(first) ? firstInventory : secondInventory;

        if (sourceInventory == null || sourceInventory.getQuantity() < dto.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock in source warehouse");
        }

        if (destinationInventory == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Destination inventory row could not be initialized");
        }

        int sourceBefore = sourceInventory.getQuantity();
        int destBefore = destinationInventory.getQuantity();
        sourceInventory.setQuantity(sourceBefore - dto.getQuantity());
        destinationInventory.setQuantity(Math.addExact(destBefore, dto.getQuantity()));
        inventoryRepository.save(sourceInventory);
        inventoryRepository.save(destinationInventory);

        StockMovement out = movement(product, sourceWarehouse, MovementType.TRANSFER_OUT, dto.getQuantity(),
                sourceBefore, sourceInventory.getQuantity(), dto.getTransactionReference());

        out.setSourceWarehouseId(sourceWarehouse.getId());
        out.setDestinationWarehouseId(destinationWarehouse.getId());

        StockMovement in = movement(product, destinationWarehouse, MovementType.TRANSFER_IN, dto.getQuantity(),
                destBefore, destinationInventory.getQuantity(), dto.getTransactionReference());

        in.setSourceWarehouseId(sourceWarehouse.getId());
        in.setDestinationWarehouseId(destinationWarehouse.getId());

        List<StockMovementResponseDTO> result = List.of(
                movementMapper.toResponse(movementRepository.save(out)
                ),
                movementMapper.toResponse(movementRepository.save(in))
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @Override
    public ResponseEntity<StockMovementResponseDTO> dispatch(DispatchRequestDTO dto) {

        Product product = product(dto.getProductId());
        Warehouse warehouse = warehouse(dto.getWarehouseId());

        Inventory inventory = inventoryRepository.findLocked(product.getId(), warehouse.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                        "No inventory exists for this product in the warehouse"));

        int before = inventory.getQuantity();
        if (before < dto.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock");
        }

        inventory.setQuantity(before - dto.getQuantity());
        inventoryRepository.save(inventory);

        StockMovement stockMovement = movement(product, warehouse, MovementType.DISPATCH, dto.getQuantity(), before,
                inventory.getQuantity(), dto.getTransactionReference());

        stockMovement.setSourceWarehouseId(warehouse.getId());

        StockMovementResponseDTO result = movementMapper.toResponse(movementRepository.save(stockMovement));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @Override
    public ResponseEntity<ReconciliationResponseDTO> reconcile(ReconciliationRequestDTO dto) {

        Product product = product(dto.getProductId());
        Warehouse warehouse = warehouse(dto.getWarehouseId());
        Inventory inventory = lockedInventoryOrCreate(product, warehouse);

        int expected = inventory.getQuantity();
        int physical = dto.getPhysicalQuantity();
        int discrepancy = physical - expected;

        StockReconciliation stockReconciliation = reconciliationRepository.save(StockReconciliation.builder()
                .product(product).warehouse(warehouse)
                .expectedQuantity(expected).physicalQuantity(physical).discrepancy(discrepancy)
                .reference(dto.getReference())
                .build());

        if (discrepancy != 0) {
            inventory.setQuantity(physical);
            inventoryRepository.save(inventory);
            movementRepository.save(movement(product, warehouse, MovementType.RECONCILIATION_ADJUSTMENT,
                    Math.abs(discrepancy), expected, physical, dto.getReference()));
        }

        ReconciliationResponseDTO result = reconciliationMapper.toResponse(stockReconciliation);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // ---------------------------------------------------------------- helpers

    private StockMovement movement(Product product, Warehouse warehouse, MovementType movementType, int quantity,
                                   int before, int after, String reference) {
        return StockMovement.builder()
                .product(product).warehouse(warehouse).type(movementType)
                .quantity(quantity).quantityBefore(before).quantityAfter(after)
                .transactionReference(reference)
                .build();
    }

    private Inventory lockedInventoryOrCreate(Product product, Warehouse warehouse) {
        ensureInventoryRow(product.getId(), warehouse.getId());
        return inventoryRepository.findLocked(product.getId(), warehouse.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Inventory row could not be initialized"));
    }

    private void ensureInventoryRow(Long productId, Long warehouseId) {
        try {
            rowInitializer.createIfMissing(productId, warehouseId);
        } catch (DataIntegrityViolationException ignored) {
            // another request created the row first - that's fine, we lock it next
        }
    }

    private Product product(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> notFound("Product", id)
                );
    }

    private Warehouse warehouse(Long id) {
        return warehouseRepository
                .findById(id)
                .orElseThrow(() -> notFound("Warehouse", id)
                );
    }

    private ResponseStatusException notFound(String type, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, type + " not found: " + id);
    }
}
