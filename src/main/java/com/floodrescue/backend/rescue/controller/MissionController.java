package com.floodrescue.backend.rescue.controller;

import com.floodrescue.backend.common.dto.ApiResponse;
import com.floodrescue.backend.rescue.dto.*;
import com.floodrescue.backend.rescue.service.MissionReportService;
import com.floodrescue.backend.rescue.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @PostMapping("/request/{requestId}")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR')")
    public ResponseEntity<ApiResponse<MissionDetailResponse>> createMission(@PathVariable Integer requestId) {
        MissionDetailResponse response = missionService.createMission(requestId);
        return ResponseEntity.ok(ApiResponse.success("Nhiệm vụ được hoàn thành thành công", response));
    }

    @GetMapping("/assigned-to-me")
    @PreAuthorize("hasRole('RESCUE_TEAM')")
    public ResponseEntity<ApiResponse<List<AssignedMissionResponse>>> getMissionsAssignedToCurrentRescuer() {
        List<AssignedMissionResponse> responses = missionService.getMissionsAssignedToCurrentRescuer();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MissionDetailResponse>> getMissionById(@PathVariable Integer id) {
        MissionDetailResponse response = missionService.getMissionById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR','ADMIN')")
    public ResponseEntity<ApiResponse<List<MissionDetailResponse>>> getAllMissions() {
        List<MissionDetailResponse> responses = missionService.getAllMissions();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{id}/assign-team")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR')")
    public ResponseEntity<ApiResponse<MissionDetailResponse>> assignMission(
            @PathVariable Integer id,
            @RequestBody AssignMissionRequest request) {
        MissionDetailResponse response = missionService.assignMission(id, request);
        return ResponseEntity.ok(ApiResponse.success("Nhóm được giao nhiệm vụ thành công", response));
    }
    /**
     * API kết hợp: Phân công nhiệm vụ + gán phương tiện + xuất vật tư
     * trong một transaction duy nhất.
     */
    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR', 'MANAGER')")
    public ResponseEntity<ApiResponse<MissionDetailResponse>> assignMissionWithResources(
            @PathVariable Integer id,
            @Valid @RequestBody AssignMissionWithResourcesRequest request) {
        MissionDetailResponse response = missionService.assignMissionWithResources(id, request);
        return ResponseEntity.ok(ApiResponse.success("Phân công nhiệm vụ và gán nguồn lực thành công", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR', 'ADMIN', 'RESCUE_TEAM')")
    public ResponseEntity<ApiResponse<MissionDetailResponse>> updateMissionStatus(
            @PathVariable Integer id,
            @RequestBody MissionStatusUpdateRequest request) {
        MissionDetailResponse response = missionService.updateMissionStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Trạng thái đã được cập nhật thành công", response));
    }

    @PatchMapping("/assignments/{assignmentId}/response")
    @PreAuthorize("hasRole('RESCUE_TEAM')")
    public ResponseEntity<ApiResponse<MissionDetailResponse>> respondToMissionAssignment(
            @PathVariable Integer assignmentId,
            @RequestBody MissionAssignmentResponseRequest request
    ) {
        MissionDetailResponse response = missionService.respondToMissionAssignment(assignmentId, request);
        return ResponseEntity.ok(ApiResponse.success("Phân công đã được phản hồi thành công", response));
    }
}
