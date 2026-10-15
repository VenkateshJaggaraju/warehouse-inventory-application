package com.warehouse.inventory.WarehouseInventoryApplication.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
}
