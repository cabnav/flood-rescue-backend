package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.rescue.dto.AssignedMissionResponse;
import com.floodrescue.backend.rescue.dto.AssignMissionRequest;
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
}
