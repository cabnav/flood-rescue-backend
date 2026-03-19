package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.rescue.dto.TeamPositionResponse;
import com.floodrescue.backend.rescue.model.RescueTeam;
import com.floodrescue.backend.rescue.model.TeamPosition;
import com.floodrescue.backend.rescue.repository.RescueTeamRepository;
import com.floodrescue.backend.rescue.repository.TeamPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamPositionServiceImpl implements TeamPositionService {

    private final TeamPositionRepository teamPositionRepository;
    private final RescueTeamRepository rescueTeamRepository;

    @Override
    public TeamPositionResponse getTeamPositionByRescueTeamId(Integer rescueTeamId) {
        if (rescueTeamId == null) {
            throw new BadRequestException("Thiếu rescueTeamId");
        }

        TeamPosition teamPosition = teamPositionRepository
                .findTopByTeam_IdOrderByRecordedAtDescPositionIdDesc(rescueTeamId)
                .orElse(null);

        if (teamPosition != null) {
            return mapToResponse(teamPosition);
        }

        // Fallback: lấy tọa độ từ warehouse của team
        RescueTeam team = rescueTeamRepository.findById(rescueTeamId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy đội cứu hộ ID: " + rescueTeamId));

        if (team.getWarehouse() != null && team.getWarehouse().getLatitude() != null) {
            TeamPositionResponse response = new TeamPositionResponse();
            response.setLatitude(team.getWarehouse().getLatitude());
            response.setLongitude(team.getWarehouse().getLongitude());
            return response;
        }

        throw new ResourceNotFoundException(
                "Chưa có vị trí được ghi nhận cho đội cứu hộ ID: " + rescueTeamId);
    }

    private TeamPositionResponse mapToResponse(TeamPosition teamPosition) {
        TeamPositionResponse response = new TeamPositionResponse();
        response.setLatitude(teamPosition.getLatitude());
        response.setLongitude(teamPosition.getLongitude());
        return response;
    }
}
