package com.warehouse.inventory.WarehouseInventoryApplication.controller;


import com.warehouse.inventory.WarehouseInventoryApplication.dto.AdminRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.AdminResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin")
//@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    public AdminResponseDTO register(@RequestBody AdminRequestDTO dto) {
        return adminService.register(dto);
    }

    @PostMapping("/login")
    public String login(@RequestBody AdminRequestDTO dto) {
        return adminService.login(dto);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        return adminService.logout(request);
    }

}
