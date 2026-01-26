package com.floodrescue.backend.manager.service;

import com.floodrescue.backend.manager.dto.VehicleDetailResponse;
import com.floodrescue.backend.manager.dto.VehicleStatusUpdateRequest;
import com.floodrescue.backend.manager.model.Vehicle;
import com.floodrescue.backend.manager.repository.VehicleRepository;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    public VehicleDetailResponse createVehicle(com.floodrescue.backend.manager.dto.CreateVehicleRequest request) {
        // TODO: Implement create vehicle logic
        return null;
    }

    @Override
    public VehicleDetailResponse getVehicleById(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        // TODO: Map to response DTO
        return null;
    }

    @Override
    public List<VehicleDetailResponse> getAllVehicles() {
        // TODO: Implement get all vehicles logic
        return null;
    }

    @Override
    public VehicleDetailResponse updateVehicleStatus(Integer id, VehicleStatusUpdateRequest request) {
        // TODO: Implement update status logic
        return null;
    }
}
