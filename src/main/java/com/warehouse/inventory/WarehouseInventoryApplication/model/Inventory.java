package com.warehouse.inventory.WarehouseInventoryApplication.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"product_id", "warehouse_id"})
    )
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private int quantity;

    @Version
    private Long version;
}
