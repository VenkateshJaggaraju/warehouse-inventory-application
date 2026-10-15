package com.warehouse.inventory.WarehouseInventoryApplication.mapper;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.SupplierRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.SupplierResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Supplier;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    Supplier toEntity(SupplierRequestDTO dto);

    SupplierResponseDTO toResponse(Supplier entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(SupplierRequestDTO dto, @MappingTarget Supplier entity);
}
