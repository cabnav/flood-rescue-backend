package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.citizen.repository.RequestRepository;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.common.util.DistanceCalculator;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.rescue.dto.CreateTeamRequest;
import com.floodrescue.backend.rescue.dto.RescueTeamResponse;
import com.floodrescue.backend.rescue.dto.TeamResponse;
import com.floodrescue.backend.rescue.model.RescueTeam;
import com.floodrescue.backend.rescue.model.TeamMember;
import com.floodrescue.backend.rescue.model.TeamPosition;
import com.floodrescue.backend.manager.repository.WarehouseRepository;
import com.floodrescue.backend.rescue.repository.RescueTeamRepository;
import com.floodrescue.backend.rescue.repository.TeamMemberRepository;
import com.floodrescue.backend.rescue.repository.TeamPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        private final WarehouseRepository warehouseRepository;
        private final TeamMemberRepository teamMemberRepository;
        private final UserRepository userRepository;

        @Override
        @Transactional(readOnly = true)
        public List<RescueTeamResponse> getAllRescueTeams() {
                return rescueTeamRepository.findAll().stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public List<RescueTeamResponse> getAvailableRescueTeams() {
                return rescueTeamRepository.findByStatus(RescueTeam.TeamStatus.ACTIVE).stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public List<RescueTeamResponse> getNearestRescueTeams(Integer requestId) {
                if (requestId == null) {
                        throw new com.floodrescue.backend.common.exception.BadRequestException(
                                        "Request ID must not be null");
                }
                Request request = requestRepository.findById(requestId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy yêu cầu với ID: " + requestId));
                double targetLat = request.getLatitude().doubleValue();
                double targetLng = request.getLongitude().doubleValue();

                return rescueTeamRepository.findByStatus(RescueTeam.TeamStatus.ACTIVE).stream()
                                .map(team -> mapToResponseWithDistance(team, targetLat, targetLng))
                                .filter(resp -> resp.getLatitude() != null && resp.getLongitude() != null)
                                .sorted(Comparator.comparing(RescueTeamResponse::getDistanceToTargetKm,
                                                Comparator.nullsLast(Double::compareTo)))
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        @SuppressWarnings("null")
        public TeamResponse createTeam(CreateTeamRequest request) {
                RescueTeam team = new RescueTeam();
                team.setName(request.getName());
                team.setQuantity(request.getQuantity());
                team.setStatus(RescueTeam.TeamStatus.ACTIVE);

                if (request.getWarehouseId() != null) {
                        team.setWarehouse(warehouseRepository.findById(request.getWarehouseId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Không tìm thấy kho với ID: " + request.getWarehouseId())));
                }

                RescueTeam saved = rescueTeamRepository.save(team);

                String leaderName = "Chưa có Leader";
                if (request.getLeaderId() != null) {
                        com.floodrescue.backend.auth.model.User leader = userRepository.findById(request.getLeaderId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Không tìm thấy Leader với ID: " + request.getLeaderId()));

                        TeamMember teamMember = new TeamMember();
                        teamMember.setRescueTeam(saved);
                        teamMember.setUser(leader);
                        teamMember.setRoleInTeam("LEADER");
                        teamMemberRepository.save(teamMember);

                        leaderName = leader.getFullName();
                }

                return TeamResponse.builder()
                                .id(saved.getId())
                                .name(saved.getName())
                                .status(saved.getStatus())
                                .quantity(saved.getQuantity())
                                .warehouseId(Optional.ofNullable(saved.getWarehouse()).map(w -> w.getId()).orElse(null))
                                .leaderName(leaderName)
                                .build();
        }

        private RescueTeamResponse mapToResponse(RescueTeam team) {
                if (team == null) {
                        return null;
                }
                String leaderName = teamMemberRepository.findByRescueTeam_IdAndRoleInTeam(team.getId(), "LEADER")
                                .map(tm -> tm.getUser().getFullName())
                                .orElse("Chưa có Leader");

                return RescueTeamResponse.builder()
                                .id(team.getId())
                                .name(team.getName())
                                .status(team.getStatus())
                                .quantity(team.getQuantity())
                                .warehouseId(team.getWarehouse() != null ? team.getWarehouse().getId() : null)
                                .leaderName(leaderName)
                                .build();
        }

        private RescueTeamResponse mapToResponseWithDistance(RescueTeam team, double targetLat, double targetLng) {
                Optional<double[]> coordinates = resolveTeamCoordinates(team);
                Double lat = coordinates.map(coords -> coords[0]).orElse(null);
                Double lng = coordinates.map(coords -> coords[1]).orElse(null);
                Double distanceToTarget = (lat != null && lng != null)
                                ? DistanceCalculator.haversineDistanceKm(lat, lng, targetLat, targetLng)
                                : null;

                String leaderName = teamMemberRepository.findByRescueTeam_IdAndRoleInTeam(team.getId(), "LEADER")
                                .map(tm -> tm.getUser().getFullName())
                                .orElse("Chưa có Leader");

                return RescueTeamResponse.builder()
                                .id(team.getId())
                                .name(team.getName())
                                .status(team.getStatus())
                                .quantity(team.getQuantity())
                                .warehouseId(team.getWarehouse() != null ? team.getWarehouse().getId() : null)
                                .leaderName(leaderName)
                                .latitude(lat)
                                .longitude(lng)
                                .distanceToTargetKm(distanceToTarget)
                                .build();
        }

        private Optional<double[]> resolveTeamCoordinates(RescueTeam team) {
                Optional<TeamPosition> latestPosition = teamPositionRepository
                                .findTopByTeam_IdOrderByRecordedAtDescPositionIdDesc(team.getId());
                if (latestPosition.isPresent()) {
                        return latestPosition.map(pos -> new double[] { pos.getLatitude().doubleValue(),
                                        pos.getLongitude().doubleValue() });
                }
                return Optional.ofNullable(team.getWarehouse())
                                .map(warehouse -> new double[] {
                                                warehouse.getLatitude().doubleValue(),
                                                warehouse.getLongitude().doubleValue()
                                });
        }
}
