package com.warehouse.inventory.WarehouseInventoryApplication.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponseDTO {
    private Long id;
    private Long productId;
    private String sku;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private int quantity;
    private Integer reorderLevel;
}
