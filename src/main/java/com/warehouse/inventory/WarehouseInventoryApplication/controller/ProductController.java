package com.warehouse.inventory.WarehouseInventoryApplication.controller;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.ProductRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.ProductResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO dto) {
//        Place the @Valid annotation right before the incoming payload argument:
        // If execution reaches here, the data has passed validation successfully
        return productService.create(dto);
    }

    @GetMapping
    public List<ProductResponseDTO> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PutMapping("/{id}")
    public ProductResponseDTO update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto) {
        return productService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
       return productService.delete(id);
    }
}
