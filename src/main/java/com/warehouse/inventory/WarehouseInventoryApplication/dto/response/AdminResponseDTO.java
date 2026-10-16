package com.warehouse.inventory.WarehouseInventoryApplication.dto.response;

import com.warehouse.inventory.WarehouseInventoryApplication.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDTO {

    private Long id;
    private String username;
    private Role role;
}
