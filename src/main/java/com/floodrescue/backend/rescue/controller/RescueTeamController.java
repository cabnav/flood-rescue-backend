package com.floodrescue.backend.rescue.controller;

import com.floodrescue.backend.common.dto.ApiResponse;
import com.floodrescue.backend.rescue.dto.RescueTeamResponse;
import com.floodrescue.backend.rescue.service.RescueTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rescue-teams")
@RequiredArgsConstructor
public class RescueTeamController {

    private final RescueTeamService rescueTeamService;

    @GetMapping
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR','ADMIN')")
    public ResponseEntity<ApiResponse<List<RescueTeamResponse>>> getAllRescueTeams() {
        List<RescueTeamResponse> teams = rescueTeamService.getAllRescueTeams();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách đội cứu hộ thành công", teams));
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR','ADMIN')")
    public ResponseEntity<ApiResponse<List<RescueTeamResponse>>> getAvailableRescueTeams() {
        List<RescueTeamResponse> teams = rescueTeamService.getAvailableRescueTeams();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách đội cứu hộ sẵn sàng thành công", teams));
    }
}

