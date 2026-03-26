package com.floodrescue.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

    // --- Requests ---
    private long totalRequests;
    private long requestsCreated;
    private long requestsInProgress;
    private long requestsCompleted;
    private long requestsCancelled;

    // --- Missions ---
    private long totalMissions;
    private long missionsPending;
    private long missionsAssigned;
    private long missionsInProgress;
    private long missionsCompleted;
    private long missionsCancelled;

    // --- Rescue Impact ---
    private long totalPeopleRescued;

    // --- Vehicles ---
    private long totalVehicles;
    private long vehiclesAvailable;
    private long vehiclesInUse;
    private long vehiclesMaintenance;
}
