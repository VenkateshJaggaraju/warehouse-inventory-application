package com.warehouse.inventory.WarehouseInventoryApplication.service;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.DispatchRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.ReceiptRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.ReconciliationRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.TransferRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.InventoryResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.ReconciliationResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.StockMovementResponseDTO;

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
