package com.warehouse.inventory.WarehouseInventoryApplication.controller;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.ReconciliationRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.ReconciliationResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reconciliations")
@RequiredArgsConstructor
public class ReconciliationController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<ReconciliationResponseDTO> reconcile(@Valid @RequestBody ReconciliationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.reconcile(dto));
    }

    @GetMapping("/discrepancies")
    public List<ReconciliationResponseDTO> getDiscrepancies() {
        return inventoryService.getDiscrepancies();
    }
}
