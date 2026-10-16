package com.warehouse.inventory.WarehouseInventoryApplication.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierRequestDTO {
    @NotBlank
    @Size(max = 150)
    private String name;
    @Email
    @Size(max = 150)
    private String email;
    @Size(max = 25)
    private String phone;
}
