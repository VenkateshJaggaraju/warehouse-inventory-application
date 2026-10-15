package com.warehouse.inventory.WarehouseInventoryApplication.repository;



import com.warehouse.inventory.WarehouseInventoryApplication.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);
}

