package com.warehouse.inventory.WarehouseInventoryApplication.service.impl;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.ProductRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.ProductResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.mapper.ProductMapper;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Product;
import com.warehouse.inventory.WarehouseInventoryApplication.repository.ProductRepository;
import com.warehouse.inventory.WarehouseInventoryApplication.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    public ResponseEntity<ProductResponseDTO> create(ProductRequestDTO dto) {
        if (productRepository.existsBySkuIgnoreCase(dto.getSku()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists");

        ProductResponseDTO result = productMapper.toResponse(productRepository.save(productMapper.toEntity(dto)));

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAll() {
        return productRepository
                .findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getById(Long id) {
        return productMapper.toResponse(getEntity(id));
    }

    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        Product product = getEntity(id);
        if (!product.getSku().equalsIgnoreCase(dto.getSku()) && productRepository.existsBySkuIgnoreCase(dto.getSku()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists");
        productMapper.updateEntity(dto, product);

        return productMapper.toResponse(productRepository.save(product));
    }

    public ResponseEntity<Void> delete(Long id) {
        productRepository.delete(getEntity(id));
        return ResponseEntity.noContent().build();
    }

    private Product getEntity(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product not found: " + id)
                );
    }
}
