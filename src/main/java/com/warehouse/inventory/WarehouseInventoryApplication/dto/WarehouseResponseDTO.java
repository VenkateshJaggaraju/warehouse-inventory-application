package com.warehouse.inventory.WarehouseInventoryApplication.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseResponseDTO {
    private Long id;
    private String name;
    private String location;
}
