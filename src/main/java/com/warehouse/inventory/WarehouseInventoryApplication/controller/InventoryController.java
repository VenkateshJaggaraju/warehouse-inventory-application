package com.warehouse.inventory.WarehouseInventoryApplication.controller;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.DispatchRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.ReceiptRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.TransferRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.InventoryResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.StockMovementResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public List<InventoryResponseDTO> getInventory() {
        return inventoryService.getInventory();
    }

    @GetMapping("/low-stock")
    public List<InventoryResponseDTO> getLowStock() {
        return inventoryService.getLowStock();
    }

    @GetMapping("/movements")
    public List<StockMovementResponseDTO> getMovements() {
        return inventoryService.getMovements();
    }

    @PostMapping("/receipts")
    public ResponseEntity<StockMovementResponseDTO> receive(@Valid @RequestBody ReceiptRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.receive(dto));
    }

    @PostMapping("/transfers")
    public ResponseEntity<List<StockMovementResponseDTO>> transfer(@Valid @RequestBody TransferRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.transfer(dto));
    }

    @PostMapping("/dispatches")
    public ResponseEntity<StockMovementResponseDTO> dispatch(@Valid @RequestBody DispatchRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.dispatch(dto));
    }
}
