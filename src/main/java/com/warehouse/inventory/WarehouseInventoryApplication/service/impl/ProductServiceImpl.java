package com.warehouse.inventory.WarehouseInventoryApplication.service.impl;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.ProductRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.ProductResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.mapper.ProductMapper;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Product;
import com.warehouse.inventory.WarehouseInventoryApplication.repository.ProductRepository;
import com.warehouse.inventory.WarehouseInventoryApplication.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductMapper mapper;

    public ProductResponseDTO create(ProductRequestDTO dto) {
        if (repository.existsBySkuIgnoreCase(dto.getSku()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists");
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getById(Long id) {
        return mapper.toResponse(getEntity(id));
    }

    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        Product p = getEntity(id);
        if (!p.getSku().equalsIgnoreCase(dto.getSku()) && repository.existsBySkuIgnoreCase(dto.getSku()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists");
        mapper.updateEntity(dto, p);
        return mapper.toResponse(repository.save(p));
    }

    public void delete(Long id) {
        repository.delete(getEntity(id));
    }

    private Product getEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id));
    }
}
