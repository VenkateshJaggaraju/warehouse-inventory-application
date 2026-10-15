package com.warehouse.inventory.WarehouseInventoryApplication.controller;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.*;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.*;
import com.warehouse.inventory.WarehouseInventoryApplication.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService service;

    @GetMapping
    public List<InventoryResponseDTO> getInventory() {
        return service.getInventory();
    }

    @GetMapping("/low-stock")
    public List<InventoryResponseDTO> getLowStock() {
        return service.getLowStock();
    }

    @GetMapping("/movements")
    public List<StockMovementResponseDTO> getMovements() {
        return service.getMovements();
    }

    @PostMapping("/receipts")
    public ResponseEntity<StockMovementResponseDTO> receive(@Valid @RequestBody ReceiptRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.receive(dto));
    }

    @PostMapping("/transfers")
    public ResponseEntity<List<StockMovementResponseDTO>> transfer(@Valid @RequestBody TransferRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.transfer(dto));
    }

    @PostMapping("/dispatches")
    public ResponseEntity<StockMovementResponseDTO> dispatch(@Valid @RequestBody DispatchRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.dispatch(dto));
    }
}
l̥