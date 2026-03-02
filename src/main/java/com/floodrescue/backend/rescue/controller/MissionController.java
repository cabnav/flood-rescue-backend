package com.floodrescue.backend.rescue.controller;

import com.floodrescue.backend.common.dto.ApiResponse;
import com.floodrescue.backend.rescue.dto.AssignedMissionResponse;
import com.floodrescue.backend.rescue.dto.AssignMissionRequest;
import com.floodrescue.backend.rescue.dto.MissionAssignmentResponseRequest;
import com.floodrescue.backend.rescue.dto.MissionDetailResponse;
import com.floodrescue.backend.rescue.dto.MissionReportRequest;
import com.floodrescue.backend.rescue.dto.MissionReportResponse;
import com.floodrescue.backend.rescue.dto.MissionStatusUpdateRequest;
import com.floodrescue.backend.rescue.service.MissionService;
import com.floodrescue.backend.rescue.service.MissionReportService;
import jakarta.validation.Valid;
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
    private final MissionReportService missionReportService;

    @PostMapping("/request/{requestId}")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR')")
    public ResponseEntity<ApiResponse<MissionDetailResponse>> createMission(@PathVariable Integer requestId) {
        MissionDetailResponse response = missionService.createMission(requestId);
        return ResponseEntity.ok(ApiResponse.success("Mission created successfully", response));
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
            @Valid @RequestBody AssignMissionRequest request) {
        MissionDetailResponse response = missionService.assignMission(id, request);
        return ResponseEntity.ok(ApiResponse.success("Team assigned successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<MissionDetailResponse>> updateMissionStatus(
            @PathVariable Integer id,
            @Valid @RequestBody MissionStatusUpdateRequest request) {
        MissionDetailResponse response = missionService.updateMissionStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", response));
    }

    @PatchMapping("/assignments/{assignmentId}/response")
    @PreAuthorize("hasRole('RESCUE_TEAM')")
    public ResponseEntity<ApiResponse<MissionDetailResponse>> respondToMissionAssignment(
            @PathVariable Integer assignmentId,
            @RequestBody MissionAssignmentResponseRequest request
    ) {
        MissionDetailResponse response = missionService.respondToMissionAssignment(assignmentId, request);
        return ResponseEntity.ok(ApiResponse.success("Assignment response submitted successfully", response));
    }

    @PostMapping("/{id}/report")
    @PreAuthorize("hasRole('RESCUE_TEAM')")
    public ResponseEntity<ApiResponse<MissionReportResponse>> createMissionReport(
            @PathVariable Integer id,
            @Valid @RequestBody MissionReportRequest request) {
        MissionReportResponse response = missionReportService.createReport(id, request);
        return ResponseEntity.ok(ApiResponse.success("Báo cáo nhiệm vụ đã được ghi nhận thành công", response));
    }
}
