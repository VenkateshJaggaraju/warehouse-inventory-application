package com.warehouse.inventory.WarehouseInventoryApplication.controller;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.SupplierRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.SupplierResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping
    public ResponseEntity<SupplierResponseDTO> create(@Valid @RequestBody SupplierRequestDTO dto) {
        return supplierService.create(dto);
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
        return supplierService.delete(id);
    }
}
