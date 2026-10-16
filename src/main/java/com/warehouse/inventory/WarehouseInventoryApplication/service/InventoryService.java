package com.warehouse.inventory.WarehouseInventoryApplication.service;

import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.DispatchRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.ReceiptRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.ReconciliationRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.request.TransferRequestDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.InventoryResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.ReconciliationResponseDTO;
import com.warehouse.inventory.WarehouseInventoryApplication.dto.response.StockMovementResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InventoryService {
    List<InventoryResponseDTO> getInventory();

    List<InventoryResponseDTO> getLowStock();

    ResponseEntity<StockMovementResponseDTO> receive(ReceiptRequestDTO dto);

    ResponseEntity<List<StockMovementResponseDTO>> transfer(TransferRequestDTO dto);

    ResponseEntity<StockMovementResponseDTO> dispatch(DispatchRequestDTO dto);

    List<StockMovementResponseDTO> getMovements();

    ResponseEntity<ReconciliationResponseDTO> reconcile(ReconciliationRequestDTO dto);

    List<ReconciliationResponseDTO> getDiscrepancies();
}
