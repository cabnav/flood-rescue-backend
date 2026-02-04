package com.floodrescue.backend.manager.controller;

import com.floodrescue.backend.common.dto.ApiResponse;
import com.floodrescue.backend.manager.dto.VehicleRequest;
import com.floodrescue.backend.manager.dto.VehicleResponse;
import com.floodrescue.backend.manager.dto.VehicleStatusUpdateRequest;
import com.floodrescue.backend.manager.model.Vehicle.VehicleStatus;
import com.floodrescue.backend.manager.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponse>> createVehicle(
            @Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.createVehicle(request);
        return ResponseEntity.ok(ApiResponse.success("Vehicle created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicleById(@PathVariable Integer id) {
        VehicleResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getAllVehicles() {
        List<VehicleResponse> responses = vehicleService.getAllVehicles();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicle(
            @PathVariable Integer id,
            @Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.updateVehicle(id, request);
        return ResponseEntity.ok(ApiResponse.success("Vehicle updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable Integer id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle deleted successfully", null));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicleStatus(
            @PathVariable Integer id,
            @Valid @RequestBody VehicleStatusUpdateRequest request) {
        VehicleStatus newStatus;
        try {
            newStatus = VehicleStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException ex) {
            throw new com.floodrescue.backend.common.exception.BadRequestException("Invalid vehicle status: " + request.getStatus());
        }

        VehicleResponse response = vehicleService.updateStatus(id, newStatus);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", response));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getVehiclesByStatus(@PathVariable String status) {
        VehicleStatus vehicleStatus;
        try {
            vehicleStatus = VehicleStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            throw new com.floodrescue.backend.common.exception.BadRequestException("Invalid vehicle status: " + status);
        }
        List<VehicleResponse> responses = vehicleService.getVehiclesByStatus(vehicleStatus);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
