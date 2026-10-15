package com.warehouse.inventory.WarehouseInventoryApplication.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {
    @NotBlank
    @Size(max = 60)
    private String sku;
    @NotBlank
    @Size(max = 150)
    private String name;
    @Size(max = 1000)
    private String description;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal unitPrice;
    @NotNull
    @Min(0)
    private Integer reorderLevel = 10;
}
