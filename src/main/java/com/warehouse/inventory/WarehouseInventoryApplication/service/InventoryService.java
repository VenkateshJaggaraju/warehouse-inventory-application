package com.warehouse.inventory.WarehouseInventoryApplication.service;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.*;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.*;

import java.util.List;

public interface InventoryService {
    List<InventoryResponseDTO> getInventory();

    List<InventoryResponseDTO> getLowStock();

    StockMovementResponseDTO receive(ReceiptRequestDTO dto);

    List<StockMovementResponseDTO> transfer(TransferRequestDTO dto);

    StockMovementResponseDTO dispatch(DispatchRequestDTO dto);

    List<StockMovementResponseDTO> getMovements();

    ReconciliationResponseDTO reconcile(ReconciliationRequestDTO dto);

    List<ReconciliationResponseDTO> getDiscrepancies();
}
