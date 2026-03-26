package com.floodrescue.backend.rescue.controller;

import com.floodrescue.backend.common.dto.ApiResponse;
import com.floodrescue.backend.rescue.dto.CreateTeamRequest;
import com.floodrescue.backend.rescue.dto.RescueTeamResponse;
import com.floodrescue.backend.rescue.service.RescueTeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rescue-teams")
@RequiredArgsConstructor
public class RescueTeamController {

    private final RescueTeamService rescueTeamService;

    @GetMapping
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR','ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<List<RescueTeamResponse>>> getAllRescueTeams() {
        List<RescueTeamResponse> teams = rescueTeamService.getAllRescueTeams();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách đội cứu hộ thành công", teams));
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR','ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<List<RescueTeamResponse>>> getAvailableRescueTeams() {
        List<RescueTeamResponse> teams = rescueTeamService.getAvailableRescueTeams();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách đội cứu hộ sẵn sàng thành công", teams));
    }

    @GetMapping("/nearby/{requestId}")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR','ADMIN')")
    public ResponseEntity<ApiResponse<List<RescueTeamResponse>>> getNearestTeams(
            @PathVariable Integer requestId) {
        List<RescueTeamResponse> teams = rescueTeamService.getNearestRescueTeams(requestId);
        return ResponseEntity.ok(ApiResponse.success("Lấy đội cứu hộ gần nhất thành công", teams));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<RescueTeamResponse>> createRescueTeam(
            @Valid @RequestBody CreateTeamRequest request) {
        RescueTeamResponse response = rescueTeamService.createTeam(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo đội cứu hộ thành công", response));
    }
}
