package com.floodrescue.backend.manager.service;

import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.manager.dto.VehicleRequest;
import com.floodrescue.backend.manager.dto.VehicleResponse;
import com.floodrescue.backend.manager.model.Vehicle;
import com.floodrescue.backend.manager.model.VehicleType;
import com.floodrescue.backend.manager.repository.VehicleRepository;
import com.floodrescue.backend.manager.repository.VehicleTypeRepository;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;

    @Override
    @SuppressWarnings("null")
    public VehicleResponse createVehicle(VehicleRequest request) {
        if (request.getVehicleTypeId() == null) {
            throw new BadRequestException("Vehicle type ID must not be null");
        }
        VehicleType vehicleType = vehicleTypeRepository.findById(request.getVehicleTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle type not found with id: " + request.getVehicleTypeId()));

        Vehicle vehicle = new Vehicle();
        // Depot handling can be added later when depot management is implemented
        vehicle.setDepot(null);
        vehicle.setType(request.getType());
        vehicle.setVehicleType(vehicleType);
        vehicle.setModel(request.getModel());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setCapacityPerson(request.getCapacityPerson());
        vehicle.setStatus(request.getStatus());
        vehicle.setIsActive(true);

        Vehicle saved = vehicleRepository.save(vehicle);
        return mapToResponse(saved);
    }

    @Override
    @SuppressWarnings("null")
    public VehicleResponse getVehicleById(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        if (!Boolean.TRUE.equals(vehicle.getIsActive())) {
            throw new ResourceNotFoundException("Vehicle not found with id: " + id);
        }

        return mapToResponse(vehicle);
    }

    @Override
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(v -> Boolean.TRUE.equals(v.getIsActive()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("null")
    public VehicleResponse updateVehicle(Integer id, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        if (!Boolean.TRUE.equals(vehicle.getIsActive())) {
            throw new ResourceNotFoundException("Vehicle not found with id: " + id);
        }

        // Business rule: if vehicle is IN_USE, core properties cannot be modified
        if (vehicle.getStatus() == Vehicle.VehicleStatus.IN_USE) {
            throw new BadRequestException("Vehicle is currently IN_USE and cannot be modified");
        }

        VehicleType vehicleType = vehicleTypeRepository.findById(request.getVehicleTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle type not found with id: " + request.getVehicleTypeId()));

        vehicle.setType(request.getType());
        vehicle.setVehicleType(vehicleType);
        vehicle.setModel(request.getModel());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setCapacityPerson(request.getCapacityPerson());
        vehicle.setStatus(request.getStatus());

        Vehicle updated = vehicleRepository.save(vehicle);
        return mapToResponse(updated);
    }

    @Override
    @SuppressWarnings("null")
    public void deleteVehicle(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        // Business rule: do not allow deleting vehicles that are IN_USE
        if (vehicle.getStatus() == Vehicle.VehicleStatus.IN_USE) {
            throw new BadRequestException("Vehicle is currently IN_USE and cannot be deleted");
        }

        // Soft delete: set isActive to false instead of deleting from DB
        vehicle.setIsActive(false);
        vehicleRepository.save(vehicle);
    }

    @Override
    @SuppressWarnings("null")
    public VehicleResponse updateStatus(Integer id, Vehicle.VehicleStatus newStatus) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        if (!Boolean.TRUE.equals(vehicle.getIsActive())) {
            throw new ResourceNotFoundException("Vehicle not found with id: " + id);
        }

        vehicle.setStatus(newStatus);
        Vehicle updated = vehicleRepository.save(vehicle);
        return mapToResponse(updated);
    }

    @Override
    public List<VehicleResponse> getVehiclesByStatus(Vehicle.VehicleStatus status) {
        return vehicleRepository.findByStatus(status).stream()
                .filter(v -> Boolean.TRUE.equals(v.getIsActive()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAnyVehicleAvailable() {
        return vehicleRepository.countByStatus(Vehicle.VehicleStatus.AVAILABLE) > 0;
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        Integer depotId = vehicle.getDepot() != null ? vehicle.getDepot().getDepotId() : null;
        Integer vehicleTypeId = vehicle.getVehicleType() != null ? vehicle.getVehicleType().getId() : null;

        return new VehicleResponse(
                vehicle.getVehicleId(),
                depotId,
                vehicle.getType(),
                vehicleTypeId,
                vehicle.getModel(),
                vehicle.getLicensePlate(),
                vehicle.getCapacityPerson(),
                vehicle.getStatus());
    }
}
