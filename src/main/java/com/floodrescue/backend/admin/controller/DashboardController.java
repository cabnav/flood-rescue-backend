package com.floodrescue.backend.admin.controller;

import com.floodrescue.backend.admin.dto.DashboardSummaryResponse;
import com.floodrescue.backend.admin.service.DashboardService;
import com.floodrescue.backend.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {
        DashboardSummaryResponse response = dashboardService.getSummary();
        return ResponseEntity.ok(ApiResponse.success("Báo cáo tổng hợp", response));
    }
}
