package com.warehouse.inventory.WarehouseInventoryApplication.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
