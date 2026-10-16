package com.warehouse.inventory.WarehouseInventoryApplication.service;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.SupplierRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.SupplierResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SupplierService {
    ResponseEntity<SupplierResponseDTO> create(SupplierRequestDTO dto);

    List<SupplierResponseDTO> getAll();

    SupplierResponseDTO getById(Long id);

    SupplierResponseDTO update(Long id, SupplierRequestDTO dto);

    ResponseEntity<Void> delete(Long id);
}
