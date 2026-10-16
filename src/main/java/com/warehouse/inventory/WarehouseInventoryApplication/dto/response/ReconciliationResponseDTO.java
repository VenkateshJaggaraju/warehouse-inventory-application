package com.warehouse.inventory.WarehouseInventoryApplication.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReconciliationResponseDTO {
    private Long id;
    private Long productId;
    private String sku;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private int expectedQuantity;
    private int physicalQuantity;
    private int discrepancy;
    private String reference;
    private LocalDateTime createdAt;
}
