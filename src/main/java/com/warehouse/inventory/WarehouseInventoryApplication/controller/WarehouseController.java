package com.warehouse.inventory.WarehouseInventoryApplication.controller;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.WarehouseRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.WarehouseResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<WarehouseResponseDTO> create(@Valid @RequestBody WarehouseRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.create(dto));
    }

    @GetMapping
    public List<WarehouseResponseDTO> getAll() {
        return warehouseService.getAll();
    }

    @GetMapping("/{id}")
    public WarehouseResponseDTO getById(@PathVariable Long id) {
        return warehouseService.getById(id);
    }

    @PutMapping("/{id}")
    public WarehouseResponseDTO update(@PathVariable Long id, @Valid @RequestBody WarehouseRequestDTO dto) {
        return warehouseService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
