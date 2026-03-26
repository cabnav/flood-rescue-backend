package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.rescue.dto.CreateTeamRequest;
import com.floodrescue.backend.rescue.dto.RescueTeamResponse;

import java.util.List;

public interface RescueTeamService {
    List<RescueTeamResponse> getAllRescueTeams();

    List<RescueTeamResponse> getAvailableRescueTeams();

    List<RescueTeamResponse> getNearestRescueTeams(Integer requestId);

    RescueTeamResponse createTeam(CreateTeamRequest request);
}
