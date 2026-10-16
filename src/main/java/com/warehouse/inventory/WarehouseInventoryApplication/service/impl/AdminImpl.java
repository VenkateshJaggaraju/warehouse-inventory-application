package com.warehouse.inventory.WarehouseInventoryApplication.service.impl;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.AdminRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.AdminResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.enums.Role;
import com.warehouse.inventory.WarehouseInventoryApplication.mapper.AdminMapper;
import com.warehouse.inventory.WarehouseInventoryApplication.service.AdminService;
import com.warehouse.inventory.WarehouseInventoryApplication.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminImpl implements AdminService {

    private final UserService userService;
    private final AdminMapper adminMapper;

    @Override
    public AdminResponseDTO register(AdminRequestDTO dto) {
        return adminMapper.toResponseDTO(userService.register(adminMapper.toEntity(dto)));
    }

    @Override
    public String login(AdminRequestDTO dto) {
        return userService.verify(dto.getUsername(), dto.getPassword());
    }

    @Override
    public String logout(HttpServletRequest request) {
        return userService.logout(request);
    }

    @Override
    public AdminResponseDTO changeRole(String username, Role role) {
        return adminMapper.toResponseDTO(userService.changeRole(username, role));
    }
}
