package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.rescue.dto.AssignedMissionResponse;
import com.floodrescue.backend.rescue.dto.AssignMissionRequest;
import com.floodrescue.backend.rescue.dto.AssignSuppliesRequest;
import com.floodrescue.backend.rescue.dto.AssignVehicleRequest;
import com.floodrescue.backend.rescue.dto.MissionAssignmentResponseRequest;
import com.floodrescue.backend.rescue.dto.MissionDetailResponse;
import com.floodrescue.backend.rescue.dto.MissionStatusUpdateRequest;

import java.util.List;

public interface MissionService {
    MissionDetailResponse createMission(Integer requestId);

    MissionDetailResponse getMissionById(Integer id);

    List<MissionDetailResponse> getAllMissions();

    MissionDetailResponse assignMission(Integer missionId, AssignMissionRequest request);

    MissionDetailResponse updateMissionStatus(Integer id, MissionStatusUpdateRequest request);

    /**
     * RT-01: Get missions assigned to the current rescuer team member.
     */
    List<AssignedMissionResponse> getMissionsAssignedToCurrentRescuer();

    /**
     * RT-01: Rescuer responds (ACCEPT/DECLINE) to a mission assignment.
     */
    MissionDetailResponse respondToMissionAssignment(Integer assignmentId, MissionAssignmentResponseRequest request);

    /**
     * Assign a vehicle to a mission. Validates vehicle is AVAILABLE and sets status
     * to IN_USE.
     */
    MissionDetailResponse assignVehicleToMission(Integer missionId, AssignVehicleRequest request);

    /**
     * Assign supplies from inventory to a mission. Validates stock and deducts
     * quantity.
     */
    MissionDetailResponse assignSuppliesToMission(Integer missionId, AssignSuppliesRequest request);
}
