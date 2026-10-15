package com.warehouse.inventory.WarehouseInventoryApplication.service;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.AdminRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.AdminResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface AdminService {

    AdminResponseDTO register(AdminRequestDTO dto);

    String login(AdminRequestDTO dto);

    String logout(HttpServletRequest request);
}
