package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.citizen.repository.RequestRepository;
import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.common.exception.UnauthorizedAccessException;
import com.floodrescue.backend.manager.model.Inventory;
import com.floodrescue.backend.manager.model.MissionSupply;
import com.floodrescue.backend.manager.model.MissionVehicle;
import com.floodrescue.backend.manager.model.Vehicle;
import com.floodrescue.backend.manager.repository.InventoryRepository;
import com.floodrescue.backend.manager.repository.MissionSupplyRepository;
import com.floodrescue.backend.manager.repository.MissionVehicleRepository;
import com.floodrescue.backend.manager.repository.VehicleRepository;
import com.floodrescue.backend.rescue.dto.AssignedMissionResponse;
import com.floodrescue.backend.rescue.dto.AssignMissionRequest;
import com.floodrescue.backend.rescue.dto.AssignSuppliesRequest;
import com.floodrescue.backend.rescue.dto.AssignVehicleRequest;
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
import com.floodrescue.backend.admin.service.NotificationService;
import com.floodrescue.backend.rescue.model.Report;
import com.floodrescue.backend.rescue.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final VehicleRepository vehicleRepository;
    private final MissionVehicleRepository missionVehicleRepository;
    private final InventoryRepository inventoryRepository;
    private final MissionSupplyRepository missionSupplyRepository;
    private final NotificationService notificationService;
    private final ReportRepository reportRepository;

    @Override
    public MissionDetailResponse createMission(Integer requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu với ID: " + requestId));

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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ với ID: " + id));
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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ với ID: " + missionId));

        RescueTeam rescueTeam = rescueTeamRepository.findById(request.getRescueTeamId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy đội cứu hộ với ID: " + request.getRescueTeamId()));

        RescueTeam.TeamStatus teamStatus = rescueTeam.getStatus();
        if (teamStatus == RescueTeam.TeamStatus.BUSY || teamStatus == RescueTeam.TeamStatus.INACTIVE) {
            throw new BadRequestException("Đội cứu hộ không sẵn sàng (BUSY/INACTIVE), không thể nhận nhiệm vụ mới");
        }

        MissionAssignment assignment = new MissionAssignment();
        assignment.setMission(mission);
        assignment.setRescueTeam(rescueTeam);
        assignment.setAssignedTime(LocalTime.now());
        assignment.setMissionRole(request.getMissionRole());
        assignment.setStatus(MissionAssignment.AssignmentStatus.ACCEPTED);
        missionAssignmentRepository.save(assignment);

        rescueTeam.setStatus(RescueTeam.TeamStatus.BUSY);
        rescueTeamRepository.save(rescueTeam);

        mission.setStatus(Mission.MissionStatus.ASSIGNED);
        mission.setStartTime(LocalDateTime.now());
        Mission saved = missionRepository.save(mission);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public MissionDetailResponse updateMissionStatus(Integer id, MissionStatusUpdateRequest request) {
        if (request == null || request.getStatus() == null) {
            throw new BadRequestException("Cần có thông tin xác thực.");
        }

        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ với ID: " + id));

        User actor = getCurrentUser();
        boolean actorIsRescueTeam = hasRole(actor, "RESCUE_TEAM");
        boolean actorIsCoordinator = hasRole(actor, "RESCUE_COORDINATOR");

        Mission.MissionStatus newStatus;
        try {
            newStatus = Mission.MissionStatus.valueOf(request.getStatus().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Trạng thái nhiệm vụ không hợp lệ: " + request.getStatus());
        }

        boolean completingNow = newStatus == Mission.MissionStatus.COMPLETED
                && mission.getStatus() != Mission.MissionStatus.COMPLETED;

        Request linkedRequest = requestRepository.findById(mission.getRequest().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Request not found with id: " + mission.getRequest().getId()));

        LocalDateTime now = LocalDateTime.now();
        mission.setStatus(newStatus);
        if (newStatus == Mission.MissionStatus.IN_PROGRESS && mission.getStartTime() == null) {
            mission.setStartTime(now);
            linkedRequest.setStatus(Request.RequestStatus.IN_PROGRESS);
        }
        if (newStatus == Mission.MissionStatus.ARRIVED) {
            linkedRequest.setStatus(Request.RequestStatus.ARRIVED);
        }
        if ((newStatus == Mission.MissionStatus.COMPLETED || newStatus == Mission.MissionStatus.CANCELLED)
                && mission.getEndTime() == null) {
            mission.setEndTime(now);
        }
        if (newStatus == Mission.MissionStatus.CANCELLED) {
            linkedRequest.setStatus(Request.RequestStatus.CANCELLED);
        }
        if (newStatus == Mission.MissionStatus.COMPLETED && actorIsCoordinator) {
            linkedRequest.setStatus(Request.RequestStatus.COMPLETED);
        }

        Mission saved = missionRepository.save(mission);

        if (newStatus == Mission.MissionStatus.COMPLETED || newStatus == Mission.MissionStatus.CANCELLED) {
            if (completingNow || newStatus == Mission.MissionStatus.CANCELLED) {
                if (newStatus == Mission.MissionStatus.COMPLETED) {
                    createCompletionReport(saved, actor, request);
                }
                releaseVehiclesForMission(saved);
            }
            if (newStatus == Mission.MissionStatus.COMPLETED) {
                if (actorIsCoordinator) {
                    notifyCitizenCompletion(linkedRequest, saved);
                } else if (actorIsRescueTeam) {
                    notifyCoordinatorsForCompletion(saved, linkedRequest);
                }
            }
        } else if (actorIsRescueTeam) {
            notifyCitizen(linkedRequest, newStatus);
        }

        return mapToResponse(saved);
    }

    @Override
    public List<AssignedMissionResponse> getMissionsAssignedToCurrentRescuer() {
        User currentUser = getCurrentUser();
        TeamMember teamMember = teamMemberRepository.findFirstByUser_Id(currentUser.getId())
                .orElseThrow(() -> new UnauthorizedAccessException("Người dùng không thuộc bất kỳ đội cứu hộ nào."));

        List<MissionAssignment> assignments = missionAssignmentRepository
                .findByRescueTeam_IdAndStatus(teamMember.getRescueTeam().getId(),
                        MissionAssignment.AssignmentStatus.ACCEPTED);

        return assignments.stream()
                .filter(a -> a.getMission() == null
                        || a.getMission().getStatus() != Mission.MissionStatus.COMPLETED)
                .map(this::mapToAssignedMissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MissionDetailResponse respondToMissionAssignment(Integer assignmentId,
            MissionAssignmentResponseRequest request) {
        if (request == null || request.getDecision() == null) {
            throw new BadRequestException("Cần phải đưa ra quyết định.");
        }

        User currentUser = getCurrentUser();
        TeamMember teamMember = teamMemberRepository.findFirstByUser_Id(currentUser.getId())
                .orElseThrow(() -> new UnauthorizedAccessException("Người dùng không thuộc bất kỳ đội cứu hộ nào."));

        MissionAssignment assignment = missionAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy nhiệm vụ được giao với ID: " + assignmentId));

        if (!assignment.getRescueTeam().getId().equals(teamMember.getRescueTeam().getId())) {
            throw new UnauthorizedAccessException("Nhiệm vụ này không được giao cho nhóm của bạn");
        }

        if (assignment.getStatus() != MissionAssignment.AssignmentStatus.PENDING) {
            throw new BadRequestException("\n" +
                    "Phân công này đã được phản hồi");
        }

        String decision = request.getDecision().toUpperCase(Locale.ROOT);
        switch (decision) {
            case "ACCEPTED":
                assignment.setStatus(MissionAssignment.AssignmentStatus.ACCEPTED);
                assignment.setDeclineReason(null);
                break;
            case "DECLINED":
                if (request.getReason() == null || request.getReason().isBlank()) {
                    throw new BadRequestException("Cần nêu lý do từ chối");
                }
                assignment.setStatus(MissionAssignment.AssignmentStatus.DECLINED);
                assignment.setDeclineReason(request.getReason());
                break;
            default:
                throw new BadRequestException(
                        "Quyết định không hợp lệ. Các giá trị được cho phép: ACCEPTED hoặc DECLINED");
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

    // =====================================================================
    // Feature 2: Vehicle Dispatch — Assign a vehicle to a mission
    // =====================================================================
    @Override
    @Transactional
    public MissionDetailResponse assignVehicleToMission(Integer missionId, AssignVehicleRequest request) {
        // 1. Fetch Mission
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ với id: " + missionId));

        // 2. Fetch Vehicle
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phương tiện với id: " + request.getVehicleId()));

        // 3. Check vehicle availability
        if (vehicle.getStatus() != Vehicle.VehicleStatus.AVAILABLE) {
            throw new BadRequestException("Phương tiện này đang bận hoặc bảo trì");
        }

        // 4. Update vehicle status to IN_USE
        vehicle.setStatus(Vehicle.VehicleStatus.IN_USE);
        vehicleRepository.save(vehicle);

        // 5. Create MissionVehicle junction record
        MissionVehicle missionVehicle = new MissionVehicle();
        missionVehicle.setMission(mission);
        missionVehicle.setVehicle(vehicle);
        missionVehicleRepository.save(missionVehicle);

        return mapToResponse(mission);
    }

    // =====================================================================
    // Feature 3: Inventory Deduction — Assign supplies to a mission
    // =====================================================================
    @Override
    @Transactional
    public MissionDetailResponse assignSuppliesToMission(Integer missionId, AssignSuppliesRequest request) {
        // 1. Fetch Mission
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ với id: " + missionId));

        // 2. Fetch Inventory
        Inventory inventory = inventoryRepository.findById(request.getInventoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tồn kho với id: " + request.getInventoryId()));

        // 2b. Guard: item must be ACTIVE
        if (inventory.getItem() != null
                && inventory.getItem().getStatus() == com.floodrescue.backend.manager.model.Item.ItemStatus.INACTIVE) {
            throw new BadRequestException("Vật phẩm này hiện không hoạt động, không thể gán vào nhiệm vụ");
        }

        // 3. Check sufficient stock
        if (request.getQuantity() > inventory.getQuantity()) {
            throw new BadRequestException("Không đủ tồn kho");
        }

        // 4. Deduct quantity from inventory
        inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
        inventory.setLastUpdate(LocalDateTime.now());
        inventoryRepository.save(inventory);

        // 5. Create MissionSupply junction record
        MissionSupply missionSupply = new MissionSupply();
        missionSupply.setMission(mission);
        missionSupply.setInventory(inventory);
        missionSupply.setQuantity(request.getQuantity());
        missionSupplyRepository.save(missionSupply);

        return mapToResponse(mission);
    }

    // =====================================================================
    // Private helper methods
    // =====================================================================

    private void releaseVehiclesForMission(Mission mission) {
        List<MissionVehicle> missionVehicles = missionVehicleRepository.findByMissionId(mission.getId());
        for (MissionVehicle mv : missionVehicles) {
            Vehicle vehicle = mv.getVehicle();
            if (vehicle != null && vehicle.getStatus() == Vehicle.VehicleStatus.IN_USE) {
                vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
                vehicleRepository.save(vehicle);
            }
        }
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
        if (assignment.getRescueTeam() != null) {
            response.setRescueTeamId(assignment.getRescueTeam().getId());
        }
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
            throw new BadRequestException("Người dùng chưa được xác thực");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    private boolean hasRole(User user, String roleName) {
        return user != null
                && user.getRole() != null
                && roleName.equalsIgnoreCase(user.getRole().getName());
    }

    private void notifyCitizen(Request request, Mission.MissionStatus status) {
        if (request == null || request.getUser() == null) {
            return;
        }
        String message = "Yêu cầu SOS #" + request.getId() + " đã được cập nhật trạng thái nhiệm vụ: " + status;
        sendNotification(request.getUser(), message);
    }

    private void notifyCoordinatorsForCompletion(Mission mission, Request request) {
        List<User> coordinators = userRepository.findByRole_NameIgnoreCaseAndIsActiveTrue("RESCUE_COORDINATOR");
        if (coordinators == null || coordinators.isEmpty()) {
            return;
        }
        String message = "Nhiệm vụ #" + mission.getId() + " cho yêu cầu SOS #" + request.getId()
                + " đã được đội cứu hộ báo hoàn thành. Vui lòng duyệt hoàn tất nhiệm vụ.";
        coordinators.forEach(coordinator -> sendNotification(coordinator, message));
    }

    private void notifyCitizenCompletion(Request request, Mission mission) {
        if (request == null || request.getUser() == null) {
            return;
        }
        String message = "Yêu cầu SOS #" + request.getId() + " đã được xác nhận hoàn tất (nhiệm vụ #"
                + mission.getId() + ").";
        sendNotification(request.getUser(), message);
    }

    private void sendNotification(User user, String message) {
        if (user == null || message == null || message.isBlank()) {
            return;
        }
        notificationService.create(user.getId(), message);
    }

    private void createCompletionReport(Mission mission, User reporter, MissionStatusUpdateRequest request) {
        if (request.getPeopleRescued() == null || request.getPeopleRescued() < 0) {
            throw new BadRequestException("Cần nhập số người được cứu (>=0) khi hoàn tất nhiệm vụ");
        }
        if (request.getSummary() == null || request.getSummary().isBlank()) {
            throw new BadRequestException("Cần nhập tóm tắt sự cố khi hoàn tất nhiệm vụ");
        }
        Report report = new Report();
        report.setMission(mission);
        report.setUser(reporter);
        report.setPeopleRescued(request.getPeopleRescued());
        report.setSummary(request.getSummary());
        report.setObstacles(request.getObstacles());
        report.setCreatedAt(LocalDateTime.now());
        reportRepository.save(report);
    }
}
