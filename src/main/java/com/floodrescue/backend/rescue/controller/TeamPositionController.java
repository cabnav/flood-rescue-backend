package com.floodrescue.backend.rescue.controller;

import com.floodrescue.backend.common.dto.ApiResponse;
import com.floodrescue.backend.rescue.dto.TeamPositionResponse;
import com.floodrescue.backend.rescue.service.TeamPositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/team-positions")
@RequiredArgsConstructor
public class TeamPositionController {

    private final TeamPositionService teamPositionService;

    @GetMapping("/team/{rescueTeamId}")
    @PreAuthorize("hasAnyRole('RESCUE_COORDINATOR','RESCUE_TEAM','ADMIN')")
    public ResponseEntity<ApiResponse<TeamPositionResponse>> getTeamPositionByTeam(
            @PathVariable Integer rescueTeamId) {
        TeamPositionResponse response = teamPositionService.getTeamPositionByRescueTeamId(rescueTeamId);
        return ResponseEntity.ok(ApiResponse.success("Lấy vị trí đội thành công", response));
    }
}
