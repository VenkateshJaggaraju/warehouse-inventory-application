package com.warehouse.inventory.WarehouseInventoryApplication.controller;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.SupplierRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.SupplierResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping
    public ResponseEntity<SupplierResponseDTO> create(@Valid @RequestBody SupplierRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.create(dto));
    }

    @GetMapping
    public List<SupplierResponseDTO> getAll() {
        return supplierService.getAll();
    }

    @GetMapping("/{id}")
    public SupplierResponseDTO getById(@PathVariable Long id) {
        return supplierService.getById(id);
    }

    @PutMapping("/{id}")
    public SupplierResponseDTO update(@PathVariable Long id, @Valid @RequestBody SupplierRequestDTO dto) {
        return supplierService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
