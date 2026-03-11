package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.citizen.repository.RequestRepository;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.common.util.DistanceCalculator;
import com.floodrescue.backend.rescue.dto.RescueTeamResponse;
import com.floodrescue.backend.rescue.model.RescueTeam;
import com.floodrescue.backend.rescue.model.TeamPosition;
import com.floodrescue.backend.rescue.repository.RescueTeamRepository;
import com.floodrescue.backend.rescue.repository.TeamPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RescueTeamServiceImpl implements RescueTeamService {

    private final RescueTeamRepository rescueTeamRepository;
    private final TeamPositionRepository teamPositionRepository;
    private final RequestRepository requestRepository;

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

    @Override
    public List<RescueTeamResponse> getNearestRescueTeams(Integer requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu với ID: " + requestId));
        double targetLat = request.getLatitude().doubleValue();
        double targetLng = request.getLongitude().doubleValue();

        return rescueTeamRepository.findByStatus(RescueTeam.TeamStatus.ACTIVE).stream()
                .map(team -> mapToResponseWithDistance(team, targetLat, targetLng))
                .filter(resp -> resp.getLatitude() != null && resp.getLongitude() != null)
                .sorted(Comparator.comparing(RescueTeamResponse::getDistanceToTargetKm, Comparator.nullsLast(Double::compareTo)))
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

    private RescueTeamResponse mapToResponseWithDistance(RescueTeam team, double targetLat, double targetLng) {
        Optional<double[]> coordinates = resolveTeamCoordinates(team);
        Double lat = coordinates.map(coords -> coords[0]).orElse(null);
        Double lng = coordinates.map(coords -> coords[1]).orElse(null);
        Double distanceToTarget = (lat != null && lng != null)
                ? DistanceCalculator.haversineDistanceKm(lat, lng, targetLat, targetLng)
                : null;

        return RescueTeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .status(team.getStatus())
                .quantity(team.getQuantity())
                .warehouseId(team.getWarehouse() != null ? team.getWarehouse().getId() : null)
                .latitude(lat)
                .longitude(lng)
                .distanceToTargetKm(distanceToTarget)
                .build();
    }

    private Optional<double[]> resolveTeamCoordinates(RescueTeam team) {
        Optional<TeamPosition> latestPosition = teamPositionRepository.findTopByTeam_IdOrderByRecordedAtDescPositionIdDesc(team.getId());
        if (latestPosition.isPresent()) {
            return latestPosition.map(pos -> new double[]{pos.getLatitude().doubleValue(), pos.getLongitude().doubleValue()});
        }
        return Optional.ofNullable(team.getWarehouse())
                .map(warehouse -> new double[]{
                        warehouse.getLatitude().doubleValue(),
                        warehouse.getLongitude().doubleValue()
                });
    }
}
