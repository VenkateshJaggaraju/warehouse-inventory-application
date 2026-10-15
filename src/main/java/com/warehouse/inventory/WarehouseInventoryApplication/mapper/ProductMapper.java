package com.warehouse.inventory.WarehouseInventoryApplication.mapper;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.ProductRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.ProductResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductRequestDTO dto);

    ProductResponseDTO toResponse(Product entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ProductRequestDTO dto, @MappingTarget Product entity);
}
