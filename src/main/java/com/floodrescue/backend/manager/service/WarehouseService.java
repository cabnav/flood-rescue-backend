package com.floodrescue.backend.manager.service;

import com.floodrescue.backend.manager.dto.WarehouseDetailResponse;
import com.floodrescue.backend.manager.dto.WarehouseInventoryResponse;
import com.floodrescue.backend.manager.dto.InventoryMovementRequest;

import java.util.List;

public interface WarehouseService {
    WarehouseDetailResponse createWarehouse(com.floodrescue.backend.manager.dto.CreateWarehouseRequest request);
    WarehouseDetailResponse getWarehouseById(Integer id);
    List<WarehouseDetailResponse> getAllWarehouses();
    WarehouseInventoryResponse getWarehouseInventory(Integer warehouseId);
    WarehouseInventoryResponse importInventory(Integer warehouseId, InventoryMovementRequest request);
    WarehouseInventoryResponse exportInventory(Integer warehouseId, InventoryMovementRequest request);
}
