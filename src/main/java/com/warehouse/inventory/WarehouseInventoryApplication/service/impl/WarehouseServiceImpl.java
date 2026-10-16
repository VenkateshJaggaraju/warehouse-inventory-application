package com.warehouse.inventory.WarehouseInventoryApplication.service.impl;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.WarehouseRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.WarehouseResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.mapper.WarehouseMapper;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Warehouse;
import com.warehouse.inventory.WarehouseInventoryApplication.repository.WarehouseRepository;
import com.warehouse.inventory.WarehouseInventoryApplication.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseMapper warehouseMapper;

    public ResponseEntity<WarehouseResponseDTO> create(WarehouseRequestDTO dto) {
        WarehouseResponseDTO response = warehouseMapper.toResponse(
                                                warehouseRepository.save(warehouseMapper.toEntity(dto))
                                        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponseDTO> getAll() {
        return warehouseRepository
                .findAll()
                .stream()
                .map(warehouseMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WarehouseResponseDTO getById(Long id) {
        return warehouseMapper.toResponse(getEntity(id));
    }

    public WarehouseResponseDTO update(Long id, WarehouseRequestDTO dto) {
        Warehouse warehouse = getEntity(id);
        warehouseMapper.updateEntity(dto, warehouse);
        return warehouseMapper.toResponse(warehouseRepository.save(warehouse));
    }

    public ResponseEntity<Void> delete(Long id) {
        warehouseRepository.delete(getEntity(id));
        return ResponseEntity.noContent().build();
    }

    private Warehouse getEntity(Long id) {
        return warehouseRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Warehouse not found: " + id)
                );
    }
}
