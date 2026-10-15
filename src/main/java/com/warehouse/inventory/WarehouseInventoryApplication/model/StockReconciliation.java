package com.warehouse.inventory.WarehouseInventoryApplication.model;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_reconciliations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReconciliation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Product product;

    @ManyToOne(optional = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private int expectedQuantity;

    @Column(nullable = false)
    private int physicalQuantity;

    @Column(nullable = false)
    private int discrepancy;

    @Column(nullable = false, length = 100)
    private String reference;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}

