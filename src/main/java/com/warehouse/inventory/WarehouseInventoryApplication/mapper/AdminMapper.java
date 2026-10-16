package com.warehouse.inventory.WarehouseInventoryApplication.mapper;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.AdminRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.AdminResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true) // role is decided by the server, never by the client
    Admin toEntity(AdminRequestDTO dto);

    AdminResponseDTO toResponseDTO(Admin admin);
}
