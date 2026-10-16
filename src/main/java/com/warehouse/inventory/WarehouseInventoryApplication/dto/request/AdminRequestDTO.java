package com.warehouse.inventory.WarehouseInventoryApplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String username;
    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
}
