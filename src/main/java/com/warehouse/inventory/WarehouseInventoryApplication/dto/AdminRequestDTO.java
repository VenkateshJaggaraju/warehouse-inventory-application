package com.warehouse.inventory.WarehouseInventoryApplication.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequestDTO {
    String username;
    String password;
}
