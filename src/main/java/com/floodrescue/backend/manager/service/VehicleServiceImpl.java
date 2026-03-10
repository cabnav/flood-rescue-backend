package com.floodrescue.backend.manager.service;

import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.manager.dto.VehicleRequest;
import com.floodrescue.backend.manager.dto.VehicleResponse;
import com.floodrescue.backend.manager.model.Vehicle;
import com.floodrescue.backend.manager.repository.MissionVehicleRepository;
import com.floodrescue.backend.manager.repository.VehicleRepository;
import com.floodrescue.backend.rescue.model.Mission;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.rescue.repository.MissionAssignmentRepository;
import com.floodrescue.backend.rescue.model.MissionAssignment;
import com.floodrescue.backend.rescue.model.RescueTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final MissionVehicleRepository missionVehicleRepository;
    private final MissionAssignmentRepository missionAssignmentRepository;

    @Override
    public VehicleResponse createVehicle(VehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        // Depot handling can be added later when depot management is implemented
        vehicle.setDepot(null);
        vehicle.setType(request.getType());
        vehicle.setModel(request.getModel());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setCapacityPerson(request.getCapacityPerson());
        vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);

        Vehicle saved = vehicleRepository.save(vehicle);
        return mapToResponse(saved);
    }

    @Override
    public VehicleResponse getVehicleById(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        return mapToResponse(vehicle);
    }

    @Override
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public VehicleResponse updateVehicle(Integer id, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        // Business rule: if vehicle is IN_USE, core properties cannot be modified
        if (vehicle.getStatus() == Vehicle.VehicleStatus.IN_USE) {
            throw new BadRequestException("Vehicle is currently IN_USE and cannot be modified");
        }

        vehicle.setType(request.getType());
        vehicle.setModel(request.getModel());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setCapacityPerson(request.getCapacityPerson());
        // Do not update status from request

        Vehicle updated = vehicleRepository.save(vehicle);
        return mapToResponse(updated);
    }

    @Override
    public void deleteVehicle(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        // Business rule: do not allow deleting vehicles that are IN_USE
        if (vehicle.getStatus() == Vehicle.VehicleStatus.IN_USE) {
            throw new BadRequestException("Vehicle is currently IN_USE and cannot be deleted");
        }

        vehicleRepository.delete(vehicle);
    }

    @Override
    public List<VehicleResponse> getVehiclesByStatus(Vehicle.VehicleStatus status) {
        return vehicleRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public boolean isAnyVehicleAvailable() {
        return vehicleRepository.countByStatus(Vehicle.VehicleStatus.AVAILABLE) > 0;
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        Integer depotId = vehicle.getDepot() != null ? vehicle.getDepot().getDepotId() : null;
        Integer currentMissionId = null;
        Integer currentRequestId = null;
        Integer currentTeamId = null;
        String currentTeamName = null;

        if (vehicle.getStatus() == Vehicle.VehicleStatus.IN_USE) {
            List<com.floodrescue.backend.manager.model.MissionVehicle> missionVehicles = missionVehicleRepository
                    .findByVehicleVehicleId(vehicle.getVehicleId());
            if (missionVehicles != null && !missionVehicles.isEmpty()) {
                // Get the most recent mission assignment
                com.floodrescue.backend.manager.model.MissionVehicle activeMvAssignment = missionVehicles.stream()
                        .max(Comparator.comparing(mv -> mv.getMission().getId()))
                        .orElse(null);

                if (activeMvAssignment != null && activeMvAssignment.getMission() != null) {
                    Mission mission = activeMvAssignment.getMission();

                    // Verify if the mission is actually active (not completed/cancelled)
                    if (mission.getStatus() != Mission.MissionStatus.COMPLETED &&
                            mission.getStatus() != Mission.MissionStatus.CANCELLED) {
                        currentMissionId = mission.getId();
                        if (mission.getRequest() != null) {
                            currentRequestId = mission.getRequest().getId();
                        }

                        // Get the Rescue Team assigned to this active mission
                        List<MissionAssignment> assignments = missionAssignmentRepository
                                .findByMission_Id(mission.getId());
                        if (assignments != null && !assignments.isEmpty()) {
                            // Find an active assignment
                            MissionAssignment activeTeamAssignment = assignments.stream()
                                    .filter(a -> a.getStatus() == MissionAssignment.AssignmentStatus.ACCEPTED)
                                    .findFirst()
                                    .orElse(null);

                            if (activeTeamAssignment != null && activeTeamAssignment.getRescueTeam() != null) {
                                currentTeamId = activeTeamAssignment.getRescueTeam().getId();
                                currentTeamName = activeTeamAssignment.getRescueTeam().getName();
                            }
                        }
                    }
                }
            }
        }

        return new VehicleResponse(
                vehicle.getVehicleId(),
                depotId,
                vehicle.getType(),
                vehicle.getModel(),
                vehicle.getLicensePlate(),
                vehicle.getCapacityPerson(),
                vehicle.getStatus(),
                currentMissionId,
                currentRequestId,
                currentTeamId,
                currentTeamName);
    }
}
