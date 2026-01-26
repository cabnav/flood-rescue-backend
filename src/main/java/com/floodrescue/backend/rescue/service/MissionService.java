package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.rescue.dto.AssignMissionRequest;
import com.floodrescue.backend.rescue.dto.MissionDetailResponse;
import com.floodrescue.backend.rescue.dto.MissionStatusUpdateRequest;

import java.util.List;

public interface MissionService {
    MissionDetailResponse createMission(Integer requestId);
    MissionDetailResponse getMissionById(Integer id);
    List<MissionDetailResponse> getAllMissions();
    MissionDetailResponse assignMission(Integer missionId, AssignMissionRequest request);
    MissionDetailResponse updateMissionStatus(Integer id, MissionStatusUpdateRequest request);
}
