package com.warehouse.inventory.WarehouseInventoryApplication.dto.response;

import com.warehouse.inventory.WarehouseInventoryApplication.enums.MovementType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementResponseDTO {
    private Long id;
    private Long productId;
    private String sku;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private MovementType type;
    private int quantity;
    private int quantityBefore;
    private int quantityAfter;
    private String transactionReference;
    private Long sourceWarehouseId;
    private Long destinationWarehouseId;
    private Long supplierId;
    private LocalDateTime createdAt;
}
