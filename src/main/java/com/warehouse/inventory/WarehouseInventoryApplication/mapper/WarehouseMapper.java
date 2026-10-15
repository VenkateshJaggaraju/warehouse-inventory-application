package com.warehouse.inventory.WarehouseInventoryApplication.mapper;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.WarehouseRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.WarehouseResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Warehouse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    Warehouse toEntity(WarehouseRequestDTO dto);

    WarehouseResponseDTO toResponse(Warehouse entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(WarehouseRequestDTO dto, @MappingTarget Warehouse entity);
}
