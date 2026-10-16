package com.warehouse.inventory.WarehouseInventoryApplication.service.impl;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.WarehouseRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.WarehouseResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.mapper.WarehouseMapper;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Warehouse;
import com.warehouse.inventory.WarehouseInventoryApplication.repository.WarehouseRepository;
import com.warehouse.inventory.WarehouseInventoryApplication.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseRepository repository;

    @Autowired
    private WarehouseMapper mapper;

    public WarehouseResponseDTO create(WarehouseRequestDTO dto) {
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponseDTO> getAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public WarehouseResponseDTO getById(Long id) {
        return mapper.toResponse(getEntity(id));
    }

    public WarehouseResponseDTO update(Long id, WarehouseRequestDTO dto) {
        Warehouse w = getEntity(id);
        mapper.updateEntity(dto, w);
        return mapper.toResponse(repository.save(w));
    }

    public void delete(Long id) {
        repository.delete(getEntity(id));
    }

    private Warehouse getEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found: " + id));
    }
}
