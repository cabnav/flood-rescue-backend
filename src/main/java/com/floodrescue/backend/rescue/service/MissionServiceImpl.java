package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.rescue.dto.AssignMissionRequest;
import com.floodrescue.backend.rescue.dto.MissionDetailResponse;
import com.floodrescue.backend.rescue.dto.MissionStatusUpdateRequest;
import com.floodrescue.backend.rescue.model.Mission;
import com.floodrescue.backend.rescue.repository.MissionRepository;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;

    @Override
    public MissionDetailResponse createMission(Integer requestId) {
        // TODO: Implement create mission logic
        return null;
    }

    @Override
    public MissionDetailResponse getMissionById(Integer id) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found with id: " + id));
        // TODO: Map to response DTO
        return null;
    }

    @Override
    public List<MissionDetailResponse> getAllMissions() {
        // TODO: Implement get all missions logic
        return null;
    }

    @Override
    public MissionDetailResponse assignMission(Integer missionId, AssignMissionRequest request) {
        // TODO: Implement assign mission logic
        return null;
    }

    @Override
    public MissionDetailResponse updateMissionStatus(Integer id, MissionStatusUpdateRequest request) {
        // TODO: Implement update status logic
        return null;
    }
}
