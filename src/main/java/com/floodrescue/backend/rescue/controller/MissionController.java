package com.floodrescue.backend.rescue.controller;

import com.floodrescue.backend.common.dto.ApiResponse;
import com.floodrescue.backend.rescue.dto.AssignMissionRequest;
import com.floodrescue.backend.rescue.dto.MissionDetailResponse;
import com.floodrescue.backend.rescue.dto.MissionStatusUpdateRequest;
import com.floodrescue.backend.rescue.service.MissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @PostMapping("/request/{requestId}")
    public ResponseEntity<ApiResponse<MissionDetailResponse>> createMission(@PathVariable Integer requestId) {
        MissionDetailResponse response = missionService.createMission(requestId);
        return ResponseEntity.ok(ApiResponse.success("Mission created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MissionDetailResponse>> getMissionById(@PathVariable Integer id) {
        MissionDetailResponse response = missionService.getMissionById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MissionDetailResponse>>> getAllMissions() {
        List<MissionDetailResponse> responses = missionService.getAllMissions();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{id}/assign-team")
    public ResponseEntity<ApiResponse<MissionDetailResponse>> assignMission(
            @PathVariable Integer id,
            @Valid @RequestBody AssignMissionRequest request) {
        MissionDetailResponse response = missionService.assignMission(id, request);
        return ResponseEntity.ok(ApiResponse.success("Team assigned successfully", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<MissionDetailResponse>> updateMissionStatus(
            @PathVariable Integer id,
            @Valid @RequestBody MissionStatusUpdateRequest request) {
        MissionDetailResponse response = missionService.updateMissionStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", response));
    }
}
