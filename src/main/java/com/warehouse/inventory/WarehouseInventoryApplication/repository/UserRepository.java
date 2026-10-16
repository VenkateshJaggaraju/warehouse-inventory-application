package com.warehouse.inventory.WarehouseInventoryApplication.repository;

import com.warehouse.inventory.WarehouseInventoryApplication.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Admin, Long> {

    Admin findByUsername(String username);
}
