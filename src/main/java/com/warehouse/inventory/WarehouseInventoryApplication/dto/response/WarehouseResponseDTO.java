package com.warehouse.inventory.WarehouseInventoryApplication.dto.response;

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
