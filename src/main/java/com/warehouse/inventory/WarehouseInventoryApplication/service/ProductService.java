package com.warehouse.inventory.WarehouseInventoryApplication.service;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.ProductRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.ProductResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    ResponseEntity<ProductResponseDTO> create(ProductRequestDTO dto);

    List<ProductResponseDTO> getAll();

    ProductResponseDTO getById(Long id);

    ProductResponseDTO update(Long id, ProductRequestDTO dto);

    ResponseEntity<Void> delete(Long id);
}
