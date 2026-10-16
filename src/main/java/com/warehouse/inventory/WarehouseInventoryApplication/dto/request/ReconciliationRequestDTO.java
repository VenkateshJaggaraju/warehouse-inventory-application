package com.warehouse.inventory.WarehouseInventoryApplication.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReconciliationRequestDTO {
    @NotNull
    @Positive
    private Long productId;
    @NotNull
    @Positive
    private Long warehouseId;
    @NotNull
    @Min(0)
    private Integer physicalQuantity;
    @NotBlank
    @Size(max = 100)
    private String reference;
}
