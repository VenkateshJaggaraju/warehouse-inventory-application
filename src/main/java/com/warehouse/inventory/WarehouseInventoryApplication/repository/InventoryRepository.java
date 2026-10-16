package com.warehouse.inventory.WarehouseInventoryApplication.repository;

import com.warehouse.inventory.WarehouseInventoryApplication.model.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProduct_IdAndWarehouse_Id(Long productId, Long warehouseId);

    /** Row lock (SELECT ... FOR UPDATE). No join fetch here: it would lock the joined rows too. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT i FROM Inventory i
        WHERE i.product.id = :productId
          AND i.warehouse.id = :warehouseId
        """)
    Optional<Inventory> findLocked(@Param("productId") Long productId,
                                   @Param("warehouseId") Long warehouseId);

    /** One query instead of 1 + 2N (product + warehouse per row). */
    @Query("""
        SELECT i FROM Inventory i
        JOIN FETCH i.product
        JOIN FETCH i.warehouse
        """)
    List<Inventory> findAllWithDetails();

    @Query("""
        SELECT i FROM Inventory i
        JOIN FETCH i.product p
        JOIN FETCH i.warehouse
        WHERE i.quantity <= p.reorderLevel
        """)
    List<Inventory> findLowStock();

    List<Inventory> findByProduct_Id(Long productId);

    List<Inventory> findByWarehouse_Id(Long warehouseId);
}
