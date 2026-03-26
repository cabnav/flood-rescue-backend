package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.rescue.dto.TeamPositionResponse;

public interface TeamPositionService {
    TeamPositionResponse getTeamPositionByRescueTeamId(Integer rescueTeamId);
}
