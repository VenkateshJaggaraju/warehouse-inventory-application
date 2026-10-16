package com.warehouse.inventory.WarehouseInventoryApplication.mapper;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.SupplierRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.SupplierResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Supplier;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    Supplier toEntity(SupplierRequestDTO dto);

    SupplierResponseDTO toResponse(Supplier supplier);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(SupplierRequestDTO dto, @MappingTarget Supplier supplier);
}
