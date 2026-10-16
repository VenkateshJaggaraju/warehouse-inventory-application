package com.warehouse.inventory.WarehouseInventoryApplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseRequestDTO {
    @NotBlank
    @Size(max = 120)
    private String name;
    @NotBlank
    @Size(max = 250)
    private String location;
}
