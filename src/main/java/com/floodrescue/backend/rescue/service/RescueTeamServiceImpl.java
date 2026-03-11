package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.rescue.dto.RescueTeamResponse;
import com.floodrescue.backend.rescue.model.RescueTeam;
import com.floodrescue.backend.rescue.repository.RescueTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RescueTeamServiceImpl implements RescueTeamService {

    private final RescueTeamRepository rescueTeamRepository;

    @Override
    public List<RescueTeamResponse> getAllRescueTeams() {
        return rescueTeamRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RescueTeamResponse> getAvailableRescueTeams() {
        return rescueTeamRepository.findByStatus(RescueTeam.TeamStatus.ACTIVE).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private RescueTeamResponse mapToResponse(RescueTeam team) {
        if (team == null) {
            return null;
        }
        return RescueTeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .status(team.getStatus())
                .quantity(team.getQuantity())
                .warehouseId(team.getWarehouse() != null ? team.getWarehouse().getId() : null)
                .build();
    }
}

