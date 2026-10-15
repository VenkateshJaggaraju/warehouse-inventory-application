package com.warehouse.inventory.WarehouseInventoryApplication.mapper;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.StockMovementResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.model.StockMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "sku", source = "product.sku")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "warehouseId", source = "warehouse.id")
    @Mapping(target = "warehouseName", source = "warehouse.name")
    StockMovementResponseDTO toResponse(StockMovement entity);
}
