package com.floodrescue.backend.admin.controller;

import com.floodrescue.backend.admin.dto.*;
import com.floodrescue.backend.admin.service.CatalogService;
import com.floodrescue.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AD-02: Cấu hình danh mục - Item (loại nhu yếu phẩm), VehicleType (loại phương tiện).
 * Chỉ Admin được chỉnh sửa (POST/PUT/DELETE). Đọc danh mục cho tất cả role đã xác thực.
 * Áp dụng cho ca mới.
 */
@RestController
@RequestMapping("/api/v1/admin/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    // ========== Item (Loại nhu yếu phẩm) ==========

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<ItemCatalogResponse>>> getAllItems() {
        List<ItemCatalogResponse> items = catalogService.getAllItems();
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @GetMapping("/items/active")
    public ResponseEntity<ApiResponse<List<ItemCatalogResponse>>> getActiveItems() {
        List<ItemCatalogResponse> items = catalogService.getActiveItems();
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<ApiResponse<ItemCatalogResponse>> getItemById(@PathVariable Integer id) {
        ItemCatalogResponse item = catalogService.getItemById(id);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ItemCatalogResponse>> createItem(
            @Valid @RequestBody ItemCatalogRequest request) {
        ItemCatalogResponse created = catalogService.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item created successfully", created));
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ItemCatalogResponse>> updateItem(
            @PathVariable Integer id,
            @Valid @RequestBody ItemCatalogRequest request) {
        ItemCatalogResponse updated = catalogService.updateItem(id, request);
        return ResponseEntity.ok(ApiResponse.success("Item updated successfully", updated));
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Integer id) {
        catalogService.deleteItem(id);
        return ResponseEntity.ok(ApiResponse.success("Item deleted successfully", null));
    }

    // ========== VehicleType (Loại phương tiện) ==========

    @GetMapping("/vehicle-types")
    public ResponseEntity<ApiResponse<List<VehicleTypeCatalogResponse>>> getAllVehicleTypes() {
        List<VehicleTypeCatalogResponse> types = catalogService.getAllVehicleTypes();
        return ResponseEntity.ok(ApiResponse.success(types));
    }

    @GetMapping("/vehicle-types/active")
    public ResponseEntity<ApiResponse<List<VehicleTypeCatalogResponse>>> getActiveVehicleTypes() {
        List<VehicleTypeCatalogResponse> types = catalogService.getActiveVehicleTypes();
        return ResponseEntity.ok(ApiResponse.success(types));
    }

    @GetMapping("/vehicle-types/{id}")
    public ResponseEntity<ApiResponse<VehicleTypeCatalogResponse>> getVehicleTypeById(@PathVariable Integer id) {
        VehicleTypeCatalogResponse type = catalogService.getVehicleTypeById(id);
        return ResponseEntity.ok(ApiResponse.success(type));
    }

    @PostMapping("/vehicle-types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleTypeCatalogResponse>> createVehicleType(
            @Valid @RequestBody VehicleTypeCatalogRequest request) {
        VehicleTypeCatalogResponse created = catalogService.createVehicleType(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle type created successfully", created));
    }

    @PutMapping("/vehicle-types/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleTypeCatalogResponse>> updateVehicleType(
            @PathVariable Integer id,
            @Valid @RequestBody VehicleTypeCatalogRequest request) {
        VehicleTypeCatalogResponse updated = catalogService.updateVehicleType(id, request);
        return ResponseEntity.ok(ApiResponse.success("Vehicle type updated successfully", updated));
    }

    @DeleteMapping("/vehicle-types/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteVehicleType(@PathVariable Integer id) {
        catalogService.deleteVehicleType(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle type deleted successfully", null));
    }
}
