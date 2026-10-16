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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
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
        Product p = product(dto.getProductId());
        Warehouse w = warehouse(dto.getWarehouseId());
        if (dto.getSupplierId() != null &&
                !supplierRepository.existsById(dto.getSupplierId())) {
            throw notFound("Supplier", dto.getSupplierId());
        }

        Inventory inv = lockedInventoryOrCreate(p, w);
        int before = inv.getQuantity();
        inv.setQuantity(Math.addExact(before, dto.getQuantity()));
        inventoryRepository.save(inv);

        StockMovement m = movement(p, w, MovementType.RECEIPT, dto.getQuantity(), before,
                inv.getQuantity(), dto.getTransactionReference());

        m.setSupplierId(dto.getSupplierId());
        m.setDestinationWarehouseId(w.getId());

        StockMovementResponseDTO result = movementMapper.toResponse(movementRepository.save(m));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @Override
    public ResponseEntity<List<StockMovementResponseDTO>> transfer(TransferRequestDTO dto) {
        if (dto.getSourceWarehouseId().equals(dto.getDestinationWarehouseId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Source and destination warehouses must differ"
            );
        }

        Product p = product(dto.getProductId());
        Warehouse source = warehouse(dto.getSourceWarehouseId());
        Warehouse destination = warehouse(dto.getDestinationWarehouseId());

        // Make sure the destination row exists, then lock both rows in a stable
        // warehouse-id order to avoid deadlocks between opposite transfers.
        ensureInventoryRow(p.getId(), destination.getId());
        Long first = Math.min(source.getId(), destination.getId());
        Long second = Math.max(source.getId(), destination.getId());
        Inventory firstInv = inventoryRepository.findLocked(p.getId(), first).orElse(null);
        Inventory secondInv = inventoryRepository.findLocked(p.getId(), second).orElse(null);
        Inventory sourceInv = source.getId().equals(first) ? firstInv : secondInv;
        Inventory destinationInv = destination.getId().equals(first) ? firstInv : secondInv;

        if (sourceInv == null || sourceInv.getQuantity() < dto.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock in source warehouse");
        }

        if (destinationInv == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Destination inventory row could not be initialized");
        }

        int sourceBefore = sourceInv.getQuantity();
        int destBefore = destinationInv.getQuantity();
        sourceInv.setQuantity(sourceBefore - dto.getQuantity());
        destinationInv.setQuantity(Math.addExact(destBefore, dto.getQuantity()));
        inventoryRepository.save(sourceInv);
        inventoryRepository.save(destinationInv);

        StockMovement out = movement(p, source, MovementType.TRANSFER_OUT, dto.getQuantity(),
                sourceBefore, sourceInv.getQuantity(), dto.getTransactionReference());

        out.setSourceWarehouseId(source.getId());
        out.setDestinationWarehouseId(destination.getId());

        StockMovement in = movement(p, destination, MovementType.TRANSFER_IN, dto.getQuantity(),
                destBefore, destinationInv.getQuantity(), dto.getTransactionReference());

        in.setSourceWarehouseId(source.getId());
        in.setDestinationWarehouseId(destination.getId());

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

        Product p = product(dto.getProductId());
        Warehouse w = warehouse(dto.getWarehouseId());

        Inventory inv = inventoryRepository.findLocked(p.getId(), w.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                        "No inventory exists for this product in the warehouse"));

        int before = inv.getQuantity();
        if (before < dto.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock");
        }

        inv.setQuantity(before - dto.getQuantity());
        inventoryRepository.save(inv);

        StockMovement m = movement(p, w, MovementType.DISPATCH, dto.getQuantity(), before,
                inv.getQuantity(), dto.getTransactionReference());

        m.setSourceWarehouseId(w.getId());

        StockMovementResponseDTO result = movementMapper.toResponse(movementRepository.save(m));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @Override
    public ReconciliationResponseDTO reconcile(ReconciliationRequestDTO dto) {

        Product p = product(dto.getProductId());
        Warehouse w = warehouse(dto.getWarehouseId());
        Inventory inv = lockedInventoryOrCreate(p, w);

        int expected = inv.getQuantity();
        int physical = dto.getPhysicalQuantity();
        int discrepancy = physical - expected;

        StockReconciliation rec = reconciliationRepository.save(StockReconciliation.builder()
                .product(p).warehouse(w)
                .expectedQuantity(expected).physicalQuantity(physical).discrepancy(discrepancy)
                .reference(dto.getReference())
                .build());

        if (discrepancy != 0) {
            inv.setQuantity(physical);
            inventoryRepository.save(inv);
            movementRepository.save(movement(p, w, MovementType.RECONCILIATION_ADJUSTMENT,
                    Math.abs(discrepancy), expected, physical, dto.getReference()));
        }

        return reconciliationMapper.toResponse(rec);
    }

    // ---------------------------------------------------------------- helpers

    private StockMovement movement(Product p, Warehouse w, MovementType type, int qty,
                                   int before, int after, String reference) {
        return StockMovement.builder()
                .product(p).warehouse(w).type(type)
                .quantity(qty).quantityBefore(before).quantityAfter(after)
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
        return productRepository.findById(id).orElseThrow(() -> notFound("Product", id));
    }

    private Warehouse warehouse(Long id) {
        return warehouseRepository.findById(id).orElseThrow(() -> notFound("Warehouse", id));
    }

    private ResponseStatusException notFound(String type, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, type + " not found: " + id);
    }
}
