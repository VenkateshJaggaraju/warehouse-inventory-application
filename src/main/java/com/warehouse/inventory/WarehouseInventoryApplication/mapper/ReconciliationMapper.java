package com.warehouse.inventory.WarehouseInventoryApplication.mapper;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.ReconciliationResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.model.StockReconciliation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReconciliationMapper {
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "sku", source = "product.sku")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "warehouseId", source = "warehouse.id")
    @Mapping(target = "warehouseName", source = "warehouse.name")
    ReconciliationResponseDTO toResponse(StockReconciliation stockReconciliation);
}
