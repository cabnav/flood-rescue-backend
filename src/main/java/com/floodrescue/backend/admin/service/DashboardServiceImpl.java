package com.floodrescue.backend.admin.service;

import com.floodrescue.backend.admin.dto.DashboardSummaryResponse;
import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.citizen.repository.RequestRepository;
import com.floodrescue.backend.manager.model.Vehicle;
import com.floodrescue.backend.manager.repository.VehicleRepository;
import com.floodrescue.backend.rescue.model.Mission;
import com.floodrescue.backend.rescue.repository.MissionRepository;
import com.floodrescue.backend.rescue.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final RequestRepository requestRepository;
    private final MissionRepository missionRepository;
    private final ReportRepository reportRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        DashboardSummaryResponse response = new DashboardSummaryResponse();

        // --- Requests ---
        response.setTotalRequests(requestRepository.count());
        response.setRequestsCreated(requestRepository.countByStatus(Request.RequestStatus.PENDING));
        response.setRequestsInProgress(requestRepository.countByStatus(Request.RequestStatus.IN_PROGRESS));
        response.setRequestsCompleted(requestRepository.countByStatus(Request.RequestStatus.COMPLETED));
        response.setRequestsCancelled(requestRepository.countByStatus(Request.RequestStatus.CANCELLED));

        // --- Missions ---
        response.setTotalMissions(missionRepository.count());
        response.setMissionsPending(missionRepository.countByStatus(Mission.MissionStatus.PENDING));
        response.setMissionsAssigned(missionRepository.countByStatus(Mission.MissionStatus.ASSIGNED));
        response.setMissionsInProgress(missionRepository.countByStatus(Mission.MissionStatus.IN_PROGRESS));
        response.setMissionsCompleted(missionRepository.countByStatus(Mission.MissionStatus.COMPLETED));
        response.setMissionsCancelled(missionRepository.countByStatus(Mission.MissionStatus.CANCELLED));

        // --- Rescue Impact ---
        Long totalRescued = reportRepository.sumPeopleRescued();
        response.setTotalPeopleRescued(totalRescued != null ? totalRescued : 0L);

        // --- Vehicles ---
        response.setTotalVehicles(vehicleRepository.count());
        response.setVehiclesAvailable(vehicleRepository.countByStatus(Vehicle.VehicleStatus.AVAILABLE));
        response.setVehiclesInUse(vehicleRepository.countByStatus(Vehicle.VehicleStatus.IN_USE));
        response.setVehiclesMaintenance(vehicleRepository.countByStatus(Vehicle.VehicleStatus.MAINTENANCE));

        return response;
    }
}
