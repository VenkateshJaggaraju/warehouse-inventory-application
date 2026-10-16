package com.warehouse.inventory.WarehouseInventoryApplication.controller;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.AdminRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.AdminResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.enums.Role;
import com.warehouse.inventory.WarehouseInventoryApplication.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    public AdminResponseDTO register(@Valid @RequestBody AdminRequestDTO dto) {
        return adminService.register(dto);
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody AdminRequestDTO dto) {
        return adminService.login(dto);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        return adminService.logout(request);
    }

    // ADMIN only (see SecurityConfig): promote/demote users
    @PutMapping("/users/{username}/role")
    public AdminResponseDTO changeRole(@PathVariable String username, @RequestParam Role role) {
        return adminService.changeRole(username, role);
    }
}
