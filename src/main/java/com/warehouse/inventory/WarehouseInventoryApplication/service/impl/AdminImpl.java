package com.warehouse.inventory.WarehouseInventoryApplication.service.impl;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.AdminRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.AdminResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.mapper.AdminMapper;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Admin;
import com.warehouse.inventory.WarehouseInventoryApplication.repository.UserRepository;
import com.warehouse.inventory.WarehouseInventoryApplication.service.AdminService;
import com.warehouse.inventory.WarehouseInventoryApplication.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminImpl implements AdminService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository adminRepository;

    @Autowired
    private AdminMapper adminMapper;

    public AdminResponseDTO register(AdminRequestDTO dto) {

        Admin admin = adminMapper.toEntity(dto);
        Admin savedAdmin = userService.register(admin);
        return adminMapper.toResponseDTO(savedAdmin);
    }

    public String login(AdminRequestDTO dto) {

        Admin admin = adminMapper.toEntity(dto);
        return userService.verify(admin);
    }

    public String logout(HttpServletRequest request) {
        return userService.logout(request);
    }
}
