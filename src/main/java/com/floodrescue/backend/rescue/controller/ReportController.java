package com.floodrescue.backend.rescue.controller;

import com.floodrescue.backend.common.dto.ApiResponse;
import com.floodrescue.backend.rescue.dto.MissionReportDetailResponse;
import com.floodrescue.backend.rescue.service.MissionReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final MissionReportService missionReportService;

    @GetMapping
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR','MANAGER')")
    public ResponseEntity<ApiResponse<List<MissionReportDetailResponse>>> getAllReports() {
        List<MissionReportDetailResponse> responses = missionReportService.getAllReports();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('RESCUE_TEAM')")
    public ResponseEntity<ApiResponse<List<MissionReportDetailResponse>>> getMyTeamReports() {
        List<MissionReportDetailResponse> responses = missionReportService.getReportsForCurrentTeam();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}

