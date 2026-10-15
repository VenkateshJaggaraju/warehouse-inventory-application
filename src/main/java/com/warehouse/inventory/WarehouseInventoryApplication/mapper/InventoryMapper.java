package com.warehouse.inventory.WarehouseInventoryApplication.mapper;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.InventoryResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "sku", source = "product.sku")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "warehouseId", source = "warehouse.id")
    @Mapping(target = "warehouseName", source = "warehouse.name")
    @Mapping(target = "reorderLevel", source = "product.reorderLevel")
    InventoryResponseDTO toResponse(Inventory entity);
}
