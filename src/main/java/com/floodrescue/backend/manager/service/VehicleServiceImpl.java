package com.floodrescue.backend.manager.service;

import com.floodrescue.backend.manager.dto.VehicleDetailResponse;
import com.floodrescue.backend.manager.dto.VehicleStatusUpdateRequest;
import com.floodrescue.backend.manager.model.Vehicle;
import com.floodrescue.backend.manager.repository.VehicleRepository;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        return mapToDetailResponse(vehicle);
    }

    @Override
    public List<VehicleDetailResponse> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(this::mapToDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleDetailResponse updateVehicleStatus(Integer id, VehicleStatusUpdateRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        Vehicle.VehicleStatus newStatus;
        try {
            newStatus = Vehicle.VehicleStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new com.floodrescue.backend.common.exception.BadRequestException("Trạng thái xe không hợp lệ");
        }

        vehicle.setStatus(newStatus);
        Vehicle saved = vehicleRepository.save(vehicle);

        return mapToDetailResponse(saved);
    }

    private VehicleDetailResponse mapToDetailResponse(Vehicle vehicle) {
        Integer depotId = vehicle.getDepot() != null ? vehicle.getDepot().getDepotId() : null;
        return new VehicleDetailResponse(
                vehicle.getVehicleId(),
                depotId,
                vehicle.getType(),
                vehicle.getModel(),
                vehicle.getLicensePlate(),
                vehicle.getCapacityPerson(),
                vehicle.getStatus()
        );
    }
}
