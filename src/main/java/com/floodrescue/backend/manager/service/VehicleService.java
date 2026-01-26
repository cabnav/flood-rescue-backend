package com.floodrescue.backend.manager.service;

import com.floodrescue.backend.manager.dto.VehicleDetailResponse;
import com.floodrescue.backend.manager.dto.VehicleStatusUpdateRequest;

import java.util.List;

public interface VehicleService {
    VehicleDetailResponse createVehicle(com.floodrescue.backend.manager.dto.CreateVehicleRequest request);
    VehicleDetailResponse getVehicleById(Integer id);
    List<VehicleDetailResponse> getAllVehicles();
    VehicleDetailResponse updateVehicleStatus(Integer id, VehicleStatusUpdateRequest request);
}
