package com.warehouse.inventory.WarehouseInventoryApplication.service;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.WarehouseRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.WarehouseResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface WarehouseService {
    ResponseEntity<WarehouseResponseDTO> create(WarehouseRequestDTO dto);

    List<WarehouseResponseDTO> getAll();

    WarehouseResponseDTO getById(Long id);

    WarehouseResponseDTO update(Long id, WarehouseRequestDTO dto);

    ResponseEntity<Void> delete(Long id);
}
