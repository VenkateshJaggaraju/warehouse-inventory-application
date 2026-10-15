package com.warehouse.inventory.WarehouseInventoryApplication.mapper;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.AdminRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.AdminResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    // Entity <- DTO
    @Mapping(target = "id", ignore = true)
    Admin toEntity(AdminRequestDTO dto);

    // DTO <- Entity
    AdminResponseDTO toResponseDTO(Admin admin);
}
