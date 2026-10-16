package com.warehouse.inventory.WarehouseInventoryApplication.service.impl;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.SupplierRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.SupplierResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.mapper.SupplierMapper;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Supplier;
import com.warehouse.inventory.WarehouseInventoryApplication.repository.SupplierRepository;
import com.warehouse.inventory.WarehouseInventoryApplication.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierMapper supplierMapper;

    public ResponseEntity<SupplierResponseDTO> create(SupplierRequestDTO dto) {
        SupplierResponseDTO response = supplierMapper
                .toResponse(supplierRepository.
                        save(supplierMapper.toEntity(dto))
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getAll() {
        return supplierRepository
                .findAll()
                .stream()
                .map(supplierMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SupplierResponseDTO getById(Long id) {
        return supplierMapper.toResponse(getEntity(id));
    }

    public SupplierResponseDTO update(Long id, SupplierRequestDTO dto) {
        Supplier supplier = getEntity(id);
        supplierMapper.updateEntity(dto, supplier);
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        supplierRepository.delete(getEntity(id));
        return ResponseEntity.noContent().build();
    }


    private Supplier getEntity(Long id) {
        return supplierRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Supplier not found: " + id)
                );
    }
}
