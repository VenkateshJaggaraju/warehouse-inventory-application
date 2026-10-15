package com.warehouse.inventory.WarehouseInventoryApplication.service.impl;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.*;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.*;
import com.warehouse.inventory.WarehouseInventoryApplication.enums.MovementType;
import com.warehouse.inventory.WarehouseInventoryApplication.mapper.InventoryMapper;
import com.warehouse.inventory.WarehouseInventoryApplication.mapper.ReconciliationMapper;
import com.warehouse.inventory.WarehouseInventoryApplication.mapper.StockMovementMapper;
import com.warehouse.inventory.WarehouseInventoryApplication.model.*;
import com.warehouse.inventory.WarehouseInventoryApplication.repository.*;
import com.warehouse.inventory.WarehouseInventoryApplication.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository movementRepository;
    private final StockReconciliationRepository reconciliationRepository;
    private final InventoryMapper inventoryMapper;
    private final StockMovementMapper movementMapper;
    private final ReconciliationMapper reconciliationMapper;

    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getInventory() {
        return inventoryRepository.findAllWithDetails().stream().map(inventoryMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getLowStock() {
        return inventoryRepository.findLowStock().stream().map(inventoryMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> getMovements() {
        return movementRepository.findAllByOrderByCreatedAtDesc().stream().map(movementMapper::toResponse).toList();
    }

    public StockMovementResponseDTO receive(ReceiptRequestDTO dto) {
        Product p = product(dto.getProductId());
        Warehouse w = warehouse(dto.getWarehouseId());
        if (dto.getSupplierId() != null)
            supplierRepository.findById(dto.getSupplierId()).orElseThrow(() -> notFound("Supplier", dto.getSupplierId()));
        Inventory inv = lockedInventoryOrCreate(p, w);
        int before = inv.getQuantity();
        inv.setQuantity(Math.addExact(before, dto.getQuantity()));
        inventoryRepository.save(inv);
        return movementMapper.toResponse(movementRepository.save(StockMovement.builder().product(p).warehouse(w).type(MovementType.RECEIPT).quantity(dto.getQuantity()).quantityBefore(before).quantityAfter(inv.getQuantity()).transactionReference(dto.getTransactionReference()).supplierId(dto.getSupplierId()).destinationWarehouseId(w.getId()).build()));
    }

    public List<StockMovementResponseDTO> transfer(TransferRequestDTO dto) {
        if (dto.getSourceWarehouseId().equals(dto.getDestinationWarehouseId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source and destination warehouses must differ");
        Product p = product(dto.getProductId());
        Warehouse source = warehouse(dto.getSourceWarehouseId());
        Warehouse destination = warehouse(dto.getDestinationWarehouseId());
        // Ensure destination row exists before locking rows in stable warehouse-ID order.
        inventoryRepository.createIfMissing(p.getId(), destination.getId());
        // Lock existing stock rows in a stable warehouse-ID order to reduce deadlock risk.
        Long first = Math.min(source.getId(), destination.getId()), second = Math.max(source.getId(), destination.getId());
        Inventory firstInv = inventoryRepository.findLocked(p.getId(), first).orElse(null);
        Inventory secondInv = inventoryRepository.findLocked(p.getId(), second).orElse(null);
        Inventory sourceInv = source.getId().equals(first) ? firstInv : secondInv;
        Inventory destinationInv = destination.getId().equals(first) ? firstInv : secondInv;
        if (sourceInv == null || sourceInv.getQuantity() < dto.getQuantity())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock in source warehouse");
        if (destinationInv == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Destination inventory row could not be initialized");
        int sourceBefore = sourceInv.getQuantity();
        int destBefore = destinationInv.getQuantity();
        sourceInv.setQuantity(sourceBefore - dto.getQuantity());
        destinationInv.setQuantity(Math.addExact(destBefore, dto.getQuantity()));
        inventoryRepository.save(sourceInv);
        inventoryRepository.save(destinationInv);
        StockMovement out = movementRepository.save(StockMovement.builder().product(p).warehouse(source).type(MovementType.TRANSFER_OUT).quantity(dto.getQuantity()).quantityBefore(sourceBefore).quantityAfter(sourceInv.getQuantity()).transactionReference(dto.getTransactionReference()).sourceWarehouseId(source.getId()).destinationWarehouseId(destination.getId()).build());
        StockMovement in = movementRepository.save(StockMovement.builder().product(p).warehouse(destination).type(MovementType.TRANSFER_IN).quantity(dto.getQuantity()).quantityBefore(destBefore).quantityAfter(destinationInv.getQuantity()).transactionReference(dto.getTransactionReference()).sourceWarehouseId(source.getId()).destinationWarehouseId(destination.getId()).build());
        return List.of(movementMapper.toResponse(out), movementMapper.toResponse(in));
    }

    public StockMovementResponseDTO dispatch(DispatchRequestDTO dto) {
        Product p = product(dto.getProductId());
        Warehouse w = warehouse(dto.getWarehouseId());
        Inventory inv = inventoryRepository.findLocked(p.getId(), w.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No inventory exists for this product in the warehouse"));
        int before = inv.getQuantity();
        if (before < dto.getQuantity()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock");
        inv.setQuantity(before - dto.getQuantity());
        inventoryRepository.save(inv);
        return movementMapper.toResponse(movementRepository.save(StockMovement.builder().product(p).warehouse(w).type(MovementType.DISPATCH).quantity(dto.getQuantity()).quantityBefore(before).quantityAfter(inv.getQuantity()).transactionReference(dto.getTransactionReference()).sourceWarehouseId(w.getId()).build()));
    }

    public ReconciliationResponseDTO reconcile(ReconciliationRequestDTO dto) {
        Product p = product(dto.getProductId());
        Warehouse w = warehouse(dto.getWarehouseId());
        Inventory inv = lockedInventoryOrCreate(p, w);
        int expected = inv.getQuantity(), physical = dto.getPhysicalQuantity(), discrepancy = physical - expected;
        StockReconciliation rec = reconciliationRepository.save(StockReconciliation.builder().product(p).warehouse(w).expectedQuantity(expected).physicalQuantity(physical).discrepancy(discrepancy).reference(dto.getReference()).build());
        if (discrepancy != 0) {
            inv.setQuantity(physical);
            inventoryRepository.save(inv);
            movementRepository.save(StockMovement.builder().product(p).warehouse(w).type(MovementType.RECONCILIATION_ADJUSTMENT).quantity(Math.abs(discrepancy)).quantityBefore(expected).quantityAfter(physical).transactionReference(dto.getReference()).build());
        }
        return reconciliationMapper.toResponse(rec);
    }

    @Transactional(readOnly = true)
    public List<ReconciliationResponseDTO> getDiscrepancies() {
        return reconciliationRepository.findByDiscrepancyNotOrderByCreatedAtDesc(0).stream().map(reconciliationMapper::toResponse).toList();
    }

    private Inventory lockedInventoryOrCreate(Product product, Warehouse warehouse) {
        inventoryRepository.createIfMissing(product.getId(), warehouse.getId());
        return inventoryRepository.findLocked(product.getId(), warehouse.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Inventory row could not be initialized"));
    }

    private Product product(Long id) {
        return productRepository.findById(id).orElseThrow(() -> notFound("Product", id));
    }

    private Warehouse warehouse(Long id) {
        return warehouseRepository.findById(id).orElseThrow(() -> notFound("Warehouse", id));
    }

    private ResponseStatusException notFound(String type, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, type + " not found: " + id);
    }
}
