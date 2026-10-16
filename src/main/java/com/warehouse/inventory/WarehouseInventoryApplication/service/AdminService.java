package com.warehouse.inventory.WarehouseInventoryApplication.service;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.AdminRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.AdminResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.enums.Role;
import jakarta.servlet.http.HttpServletRequest;

public interface AdminService {

    AdminResponseDTO register(AdminRequestDTO dto);

    String login(AdminRequestDTO dto);

    String logout(HttpServletRequest request);

    AdminResponseDTO changeRole(String username, Role role);
}
