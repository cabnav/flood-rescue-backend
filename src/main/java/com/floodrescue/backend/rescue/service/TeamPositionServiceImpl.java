package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.rescue.dto.TeamPositionResponse;
import com.floodrescue.backend.rescue.model.TeamPosition;
import com.floodrescue.backend.rescue.repository.TeamPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TeamPositionServiceImpl implements TeamPositionService {

    private final TeamPositionRepository teamPositionRepository;

    @Override
    public TeamPositionResponse getTeamPositionByRescueTeamId(Integer rescueTeamId) {
        if (rescueTeamId == null) {
            throw new BadRequestException("Thiếu rescueTeamId");
        }

        TeamPosition teamPosition = teamPositionRepository
                .findTopByTeam_IdOrderByRecordedAtDescPositionIdDesc(rescueTeamId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chưa có vị trí được ghi nhận cho đội cứu hộ ID: " + rescueTeamId));

        return mapToResponse(teamPosition);
    }

    private TeamPositionResponse mapToResponse(TeamPosition teamPosition) {
        TeamPositionResponse response = new TeamPositionResponse();
        response.setLatitude(teamPosition.getLatitude());
        response.setLongitude(teamPosition.getLongitude());
        return response;
    }
}
