package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.citizen.repository.RequestRepository;
import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.common.exception.UnauthorizedAccessException;
import com.floodrescue.backend.rescue.dto.AssignedMissionResponse;
import com.floodrescue.backend.rescue.dto.AssignMissionRequest;
import com.floodrescue.backend.rescue.dto.MissionAssignmentResponseRequest;
import com.floodrescue.backend.rescue.dto.MissionDetailResponse;
import com.floodrescue.backend.rescue.dto.MissionStatusUpdateRequest;
import com.floodrescue.backend.rescue.model.Mission;
import com.floodrescue.backend.rescue.model.MissionAssignment;
import com.floodrescue.backend.rescue.model.RescueTeam;
import com.floodrescue.backend.rescue.model.TeamMember;
import com.floodrescue.backend.rescue.repository.MissionAssignmentRepository;
import com.floodrescue.backend.rescue.repository.MissionRepository;
import com.floodrescue.backend.rescue.repository.RescueTeamRepository;
import com.floodrescue.backend.rescue.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;
    private final RequestRepository requestRepository;
    private final RescueTeamRepository rescueTeamRepository;
    private final MissionAssignmentRepository missionAssignmentRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public MissionDetailResponse createMission(Integer requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        Mission mission = new Mission();
        mission.setRequest(request);
        mission.setMissionType(mapRequestTypeToMissionType(request.getRequestType()));
        mission.setStatus(Mission.MissionStatus.PENDING);
        mission.setCreatedAt(LocalDateTime.now());

        Mission saved = missionRepository.save(mission);
        return mapToResponse(saved);
    }

    @Override
    public MissionDetailResponse getMissionById(Integer id) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found with id: " + id));
        return mapToResponse(mission);
    }

    @Override
    public List<MissionDetailResponse> getAllMissions() {
        return missionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MissionDetailResponse assignMission(Integer missionId, AssignMissionRequest request) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found with id: " + missionId));

        RescueTeam rescueTeam = rescueTeamRepository.findById(request.getRescueTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Rescue team not found with id: " + request.getRescueTeamId()));

        MissionAssignment assignment = new MissionAssignment();
        assignment.setMission(mission);
        assignment.setRescueTeam(rescueTeam);
        assignment.setAssignedTime(LocalDateTime.now());
        assignment.setMissionRole(request.getMissionRole());
        assignment.setStatus(MissionAssignment.AssignmentStatus.PENDING);
        missionAssignmentRepository.save(assignment);

        mission.setStatus(Mission.MissionStatus.ASSIGNED);
        Mission saved = missionRepository.save(mission);
        return mapToResponse(saved);
    }

    @Override
    public MissionDetailResponse updateMissionStatus(Integer id, MissionStatusUpdateRequest request) {
        if (request == null || request.getStatus() == null) {
            throw new BadRequestException("Status is required");
        }

        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found with id: " + id));

        Mission.MissionStatus newStatus;
        try {
            newStatus = Mission.MissionStatus.valueOf(request.getStatus().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid mission status: " + request.getStatus());
        }

        mission.setStatus(newStatus);
        if (newStatus == Mission.MissionStatus.IN_PROGRESS && mission.getStartTime() == null) {
            mission.setStartTime(LocalDateTime.now());
        }
        if ((newStatus == Mission.MissionStatus.COMPLETED || newStatus == Mission.MissionStatus.CANCELLED)
                && mission.getEndTime() == null) {
            mission.setEndTime(LocalDateTime.now());
        }

        Mission saved = missionRepository.save(mission);
        return mapToResponse(saved);
    }

    @Override
    public List<AssignedMissionResponse> getMissionsAssignedToCurrentRescuer() {
        User currentUser = getCurrentUser();
        TeamMember teamMember = teamMemberRepository.findFirstByUser_Id(currentUser.getId())
                .orElseThrow(() -> new UnauthorizedAccessException("User is not part of any rescue team"));

        List<MissionAssignment> assignments = missionAssignmentRepository
                .findByRescueTeam_IdAndStatus(teamMember.getRescueTeam().getId(), MissionAssignment.AssignmentStatus.PENDING);

        return assignments.stream()
                .map(this::mapToAssignedMissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MissionDetailResponse respondToMissionAssignment(Integer assignmentId, MissionAssignmentResponseRequest request) {
        if (request == null || request.getDecision() == null) {
            throw new BadRequestException("Decision is required");
        }

        User currentUser = getCurrentUser();
        TeamMember teamMember = teamMemberRepository.findFirstByUser_Id(currentUser.getId())
                .orElseThrow(() -> new UnauthorizedAccessException("User is not part of any rescue team"));

        MissionAssignment assignment = missionAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission assignment not found with id: " + assignmentId));

        if (!assignment.getRescueTeam().getId().equals(teamMember.getRescueTeam().getId())) {
            throw new UnauthorizedAccessException("Mission is not assigned to your team");
        }

        if (assignment.getStatus() != MissionAssignment.AssignmentStatus.PENDING) {
            throw new BadRequestException("Assignment already responded");
        }

        String decision = request.getDecision().toUpperCase(Locale.ROOT);
        switch (decision) {
            case "ACCEPT":
                assignment.setStatus(MissionAssignment.AssignmentStatus.ACCEPTED);
                assignment.setDeclineReason(null);
                break;
            case "DECLINE":
                if (request.getReason() == null || request.getReason().isBlank()) {
                    throw new BadRequestException("Decline reason is required");
                }
                assignment.setStatus(MissionAssignment.AssignmentStatus.DECLINED);
                assignment.setDeclineReason(request.getReason());
                break;
            default:
                throw new BadRequestException("Invalid decision. Allowed values: ACCEPT or DECLINE");
        }

        missionAssignmentRepository.save(assignment);

        Mission mission = assignment.getMission();
        if (assignment.getStatus() == MissionAssignment.AssignmentStatus.ACCEPTED) {
            mission.setStatus(Mission.MissionStatus.IN_PROGRESS);
            if (mission.getStartTime() == null) {
                mission.setStartTime(LocalDateTime.now());
            }
            missionRepository.save(mission);
        }

        return mapToResponse(mission);
    }

    private MissionDetailResponse mapToResponse(Mission mission) {
        MissionDetailResponse response = new MissionDetailResponse();
        response.setId(mission.getId());
        response.setRequestId(mission.getRequest() != null ? mission.getRequest().getId() : null);
        response.setMissionType(mission.getMissionType());
        response.setStatus(mission.getStatus());
        response.setStartTime(mission.getStartTime());
        response.setEndTime(mission.getEndTime());
        response.setCreatedAt(mission.getCreatedAt());
        return response;
    }

    private AssignedMissionResponse mapToAssignedMissionResponse(MissionAssignment assignment) {
        Mission mission = assignment.getMission();
        Request request = mission.getRequest();

        AssignedMissionResponse response = new AssignedMissionResponse();
        response.setAssignmentId(assignment.getId());
        response.setStatus(assignment.getStatus());
        response.setMission(mapToResponse(mission));

        if (request != null) {
            AssignedMissionResponse.RequestInfo requestInfo = new AssignedMissionResponse.RequestInfo();
            requestInfo.setId(request.getId());
            requestInfo.setLatitude(request.getLatitude());
            requestInfo.setLongitude(request.getLongitude());
            requestInfo.setPriority(request.getPriority());
            response.setRequest(requestInfo);
        }

        return response;
    }

    private Mission.MissionType mapRequestTypeToMissionType(Request.RequestType requestType) {
        if (requestType == null) {
            return Mission.MissionType.RESCUE;
        }
        switch (requestType) {
            case RESCUE:
                return Mission.MissionType.RESCUE;
            case RELIEF:
                return Mission.MissionType.RELIEF;
            default:
                return Mission.MissionType.RESCUE;
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("User not authenticated");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}

