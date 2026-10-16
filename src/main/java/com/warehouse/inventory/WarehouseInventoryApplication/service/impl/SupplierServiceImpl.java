package com.warehouse.inventory.WarehouseInventoryApplication.service.impl;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.SupplierRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.SupplierResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.mapper.SupplierMapper;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Supplier;
import com.warehouse.inventory.WarehouseInventoryApplication.repository.SupplierRepository;
import com.warehouse.inventory.WarehouseInventoryApplication.service.SupplierService;
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
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository repository;

    @Autowired
    private SupplierMapper mapper;

    public SupplierResponseDTO create(SupplierRequestDTO dto) {
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SupplierResponseDTO getById(Long id) {
        return mapper.toResponse(getEntity(id));
    }

    public SupplierResponseDTO update(Long id, SupplierRequestDTO dto) {
        Supplier s = getEntity(id);
        mapper.updateEntity(dto, s);
        return mapper.toResponse(repository.save(s));
    }

    public void delete(Long id) {
        repository.delete(getEntity(id));
    }

    private Supplier getEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found: " + id));
    }
}
