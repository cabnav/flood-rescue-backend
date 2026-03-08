package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.citizen.repository.RequestRepository;
import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.common.exception.UnauthorizedAccessException;
import com.floodrescue.backend.manager.model.Inventory;
import com.floodrescue.backend.manager.model.InventoryTransaction;
import com.floodrescue.backend.manager.model.ReliefDistribution;
import com.floodrescue.backend.manager.model.MissionVehicle;
import com.floodrescue.backend.manager.model.Vehicle;
import com.floodrescue.backend.manager.repository.InventoryRepository;
import com.floodrescue.backend.manager.repository.InventoryTransactionRepository;
import com.floodrescue.backend.manager.repository.ReliefDistributionRepository;
import com.floodrescue.backend.manager.repository.MissionVehicleRepository;
import com.floodrescue.backend.manager.repository.VehicleRepository;
import com.floodrescue.backend.rescue.dto.*;
import com.floodrescue.backend.rescue.dto.MissionAssignmentResponseRequest;
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
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ReliefDistributionRepository reliefDistributionRepository;


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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đội cứu hộ với ID: " + request.getRescueTeamId()));

        MissionAssignment assignment = new MissionAssignment();
        assignment.setMission(mission);
        assignment.setRescueTeam(rescueTeam);
        assignment.setAssignedTime(
                LocalTime.now().withNano(0)
        );
        assignment.setMissionRole(request.getMissionRole());
        assignment.setStatus(MissionAssignment.AssignmentStatus.PENDING);
        missionAssignmentRepository.save(assignment);

        mission.setStatus(Mission.MissionStatus.ASSIGNED);
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

        Mission.MissionStatus oldStatus = mission.getStatus();
        Mission.MissionStatus newStatus;
        try {
            newStatus = Mission.MissionStatus.valueOf(request.getStatus().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Trạng thái nhiệm vụ không hợp lệ: " + request.getStatus());
        }

        mission.setStatus(newStatus);
        if (newStatus == Mission.MissionStatus.IN_PROGRESS && mission.getStartTime() == null) {
            mission.setStartTime(LocalDateTime.now());
        }
        if (isTerminalStatus(newStatus) && mission.getEndTime() == null) {
            mission.setEndTime(LocalDateTime.now());
        }

        Mission saved = missionRepository.save(mission);

        if (isTerminalStatus(newStatus)) {
            updateAssignmentsForMission(saved, newStatus);
            releaseVehiclesForMission(saved);
            if (shouldReturnSupplies(oldStatus, newStatus)) {
                returnSuppliesForMission(saved);
            }
        }

        return mapToResponse(saved);
    }

    private static boolean isTerminalStatus(Mission.MissionStatus status) {
        return status == Mission.MissionStatus.COMPLETED
                || status == Mission.MissionStatus.FAILED
                || status == Mission.MissionStatus.CANCELLED;
    }

    /**
     * Chỉ hoàn vật tư một lần: khi trạng thái mới là FAILED/CANCELLED
     * và trạng thái cũ chưa phải FAILED/CANCELLED.
     */
    private static boolean shouldReturnSupplies(Mission.MissionStatus oldStatus, Mission.MissionStatus newStatus) {
        if (newStatus != Mission.MissionStatus.FAILED && newStatus != Mission.MissionStatus.CANCELLED) {
            return false;
        }
        return oldStatus != Mission.MissionStatus.FAILED && oldStatus != Mission.MissionStatus.CANCELLED;
    }

    @Override
    public List<AssignedMissionResponse> getMissionsAssignedToCurrentRescuer() {
        User currentUser = getCurrentUser();
        TeamMember teamMember = teamMemberRepository.findFirstByUser_Id(currentUser.getId())
                .orElseThrow(() -> new UnauthorizedAccessException("Người dùng không thuộc bất kỳ đội cứu hộ nào."));

        List<MissionAssignment> assignments = missionAssignmentRepository
                .findByRescueTeam_IdAndStatus(teamMember.getRescueTeam().getId(),
                        MissionAssignment.AssignmentStatus.PENDING);

        return assignments.stream()
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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ được giao với ID: " + assignmentId));

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
                throw new BadRequestException("Quyết định không hợp lệ. Các giá trị được cho phép: ACCEPTED hoặc DECLINED");
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
        int beforeQuantity = inventory.getQuantity();
        int afterQuantity = beforeQuantity - request.getQuantity();
        inventory.setQuantity(afterQuantity);
        inventory.setLastUpdate(LocalDateTime.now());
        inventoryRepository.save(inventory);

        // 5. Ghi nhận giao dịch xuất kho
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setInventory(inventory);
        transaction.setTransactionType(InventoryTransaction.TransactionType.OUT);
        transaction.setQuantity(request.getQuantity());
        transaction.setBeforeQuantity(beforeQuantity);
        transaction.setAfterQuantity(afterQuantity);
        transaction.setUser(getCurrentUserOrNull());
        inventoryTransactionRepository.save(transaction);

        // 6. Ghi nhận xuất vật tư (dùng ReliefDistribution tạm thay mission_supplies)
        ReliefDistribution distribution = new ReliefDistribution();
        distribution.setMission(mission);
        distribution.setInventory(inventory);
        distribution.setQuantityDistributed(request.getQuantity());
        distribution.setDistributedAt(LocalDateTime.now());
        distribution.setReturned(false);
        reliefDistributionRepository.save(distribution);

        return mapToResponse(mission);
    }

    // =====================================================================
    // Feature 4: Combined assignment — team, vehicle, supplies in one shot
    // =====================================================================
    @Override
    @Transactional
    public MissionDetailResponse assignMissionWithResources(Integer missionId, AssignMissionWithResourcesRequest request) {
        if (request == null || request.getTeam() == null) {
            throw new BadRequestException("Thông tin phân công đội cứu hộ là bắt buộc");
        }

        // 1) Assign rescue team (always required)
        MissionDetailResponse response = assignMission(missionId, request.getTeam());

        // 2) Optionally assign vehicle
        if (request.getVehicle() != null) {
            response = assignVehicleToMission(missionId, request.getVehicle());
        }

        // 3) Optionally assign multiple supplies
        if (request.getSupplies() != null) {
            for (AssignSuppliesRequest suppliesRequest : request.getSupplies()) {
                if (suppliesRequest != null) {
                    response = assignSuppliesToMission(missionId, suppliesRequest);
                }
            }
        }

        // All operations share the same transaction (method is @Transactional).
        return response;
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

    /**
     * Hoàn vật tư đã xuất về kho khi mission FAILED hoặc CANCELLED (chưa sử dụng).
     * Dùng ReliefDistribution, chỉ xử lý bản ghi chưa returned.
     */
    private void returnSuppliesForMission(Mission mission) {
        List<ReliefDistribution> distributions = reliefDistributionRepository.findByMission_IdAndReturnedFalse(mission.getId());
        for (ReliefDistribution rd : distributions) {
            Inventory inv = rd.getInventory();
            if (inv != null) {
                int beforeQuantity = inv.getQuantity();
                int qty = rd.getQuantityDistributed();
                int afterQuantity = beforeQuantity + qty;
                inv.setQuantity(afterQuantity);
                inv.setLastUpdate(LocalDateTime.now());
                inventoryRepository.save(inv);

                InventoryTransaction transaction = new InventoryTransaction();
                transaction.setInventory(inv);
                transaction.setTransactionType(InventoryTransaction.TransactionType.IN);
                transaction.setQuantity(qty);
                transaction.setBeforeQuantity(beforeQuantity);
                transaction.setAfterQuantity(afterQuantity);
                transaction.setUser(getCurrentUserOrNull());
                inventoryTransactionRepository.save(transaction);

                rd.setReturned(true);
                reliefDistributionRepository.save(rd);
            }
        }
    }

    private User getCurrentUserOrNull() {
        try {
            return getCurrentUser();
        } catch (Exception e) {
            return null;
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
    /**
     * Cập nhật trạng thái tất cả MissionAssignment của mission khi mission kết thúc
     * (COMPLETED / FAILED / CANCELLED). Assignment DECLINED giữ nguyên, còn lại map theo status mission.
     */
    private void updateAssignmentsForMission(Mission mission, Mission.MissionStatus status) {
        List<MissionAssignment> assignments = missionAssignmentRepository.findByMission_Id(mission.getId());
        MissionAssignment.AssignmentStatus targetStatus = mapMissionStatusToAssignmentStatus(status);
        if (targetStatus == null) {
            return;
        }
        for (MissionAssignment assignment : assignments) {
            if (assignment.getStatus() == MissionAssignment.AssignmentStatus.DECLINED) {
                continue;
            }
            assignment.setStatus(targetStatus);
            missionAssignmentRepository.save(assignment);
        }
    }

    private static MissionAssignment.AssignmentStatus mapMissionStatusToAssignmentStatus(Mission.MissionStatus status) {
        switch (status) {
            case COMPLETED:
                return MissionAssignment.AssignmentStatus.COMPLETED;
            case FAILED:
                return MissionAssignment.AssignmentStatus.FAILED;
            case CANCELLED:
                return MissionAssignment.AssignmentStatus.CANCELLED;
            default:
                return null;
        }
    }
}
