package com.floodrescue.backend.manager.controller;

import com.floodrescue.backend.common.dto.ApiResponse;
import com.floodrescue.backend.manager.dto.CreateWarehouseRequest;
import com.floodrescue.backend.manager.dto.WarehouseDetailResponse;
import com.floodrescue.backend.manager.dto.WarehouseInventoryResponse;
import com.floodrescue.backend.manager.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseDetailResponse>> createWarehouse(
            @Valid @RequestBody CreateWarehouseRequest request) {
        WarehouseDetailResponse response = warehouseService.createWarehouse(request);
        return ResponseEntity.ok(ApiResponse.success("Warehouse created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseDetailResponse>> getWarehouseById(@PathVariable Integer id) {
        WarehouseDetailResponse response = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WarehouseDetailResponse>>> getAllWarehouses() {
        List<WarehouseDetailResponse> responses = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}/inventory")
    public ResponseEntity<ApiResponse<WarehouseInventoryResponse>> getWarehouseInventory(@PathVariable Integer id) {
        WarehouseInventoryResponse response = warehouseService.getWarehouseInventory(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
