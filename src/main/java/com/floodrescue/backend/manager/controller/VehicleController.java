package com.floodrescue.backend.manager.controller;

import com.floodrescue.backend.common.dto.ApiResponse;
import com.floodrescue.backend.manager.dto.VehicleDetailResponse;
import com.floodrescue.backend.manager.dto.VehicleStatusUpdateRequest;
import com.floodrescue.backend.manager.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleDetailResponse>> createVehicle(
            @RequestBody com.floodrescue.backend.manager.dto.CreateVehicleRequest request) {
        VehicleDetailResponse response = vehicleService.createVehicle(request);
        return ResponseEntity.ok(ApiResponse.success("Vehicle created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleDetailResponse>> getVehicleById(@PathVariable Integer id) {
        VehicleDetailResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleDetailResponse>>> getAllVehicles() {
        List<VehicleDetailResponse> responses = vehicleService.getAllVehicles();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<VehicleDetailResponse>> updateVehicleStatus(
            @PathVariable Integer id,
            @RequestBody VehicleStatusUpdateRequest request) {
        VehicleDetailResponse response = vehicleService.updateVehicleStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", response));
    }
}
