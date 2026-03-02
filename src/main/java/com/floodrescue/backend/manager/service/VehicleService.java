package com.floodrescue.backend.manager.service;

import com.floodrescue.backend.manager.dto.VehicleRequest;
import com.floodrescue.backend.manager.dto.VehicleResponse;
import com.floodrescue.backend.manager.model.Vehicle;

import java.util.List;

public interface VehicleService {
    VehicleResponse createVehicle(VehicleRequest request);
    VehicleResponse getVehicleById(Integer id);
    List<VehicleResponse> getAllVehicles();
    VehicleResponse updateVehicle(Integer id, VehicleRequest request);
    void deleteVehicle(Integer id);
    VehicleResponse updateStatus(Integer id, Vehicle.VehicleStatus newStatus);
    List<VehicleResponse> getVehiclesByStatus(Vehicle.VehicleStatus status);
}
