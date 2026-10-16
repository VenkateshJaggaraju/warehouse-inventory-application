package com.warehouse.inventory.WarehouseInventoryApplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequestDTO {
    @NotNull
    @Positive
    private Long productId;
    @NotNull
    @Positive
    private Long sourceWarehouseId;
    @NotNull
    @Positive
    private Long destinationWarehouseId;
    @NotNull
    @Positive
    private Integer quantity;
    @NotBlank
    @Size(max = 100)
    private String transactionReference;
}
