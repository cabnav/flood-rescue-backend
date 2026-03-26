package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.common.exception.UnauthorizedAccessException;
import com.floodrescue.backend.manager.model.MissionSupply;
import com.floodrescue.backend.manager.model.MissionVehicle;
import com.floodrescue.backend.manager.model.Vehicle;
import com.floodrescue.backend.rescue.dto.MissionDetailResponse;
import com.floodrescue.backend.rescue.dto.MissionReportDetailResponse;
import com.floodrescue.backend.rescue.dto.MissionReportRequest;
import com.floodrescue.backend.rescue.dto.MissionReportResponse;
import com.floodrescue.backend.rescue.model.Mission;
import com.floodrescue.backend.rescue.model.MissionAssignment;
import com.floodrescue.backend.rescue.model.Report;
import com.floodrescue.backend.rescue.model.TeamMember;
import com.floodrescue.backend.rescue.repository.MissionAssignmentRepository;
import com.floodrescue.backend.rescue.repository.MissionRepository;
import com.floodrescue.backend.rescue.repository.ReportRepository;
import com.floodrescue.backend.rescue.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionReportServiceImpl implements MissionReportService {

    private final MissionRepository missionRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final com.floodrescue.backend.manager.repository.MissionVehicleRepository missionVehicleRepository;
    private final com.floodrescue.backend.manager.repository.VehicleRepository vehicleRepository;
    private final com.floodrescue.backend.manager.repository.MissionSupplyRepository missionSupplyRepository;
    private final MissionAssignmentRepository missionAssignmentRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    @Transactional
    @SuppressWarnings("null")
    public MissionReportResponse createReport(Integer missionId, MissionReportRequest request) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhiệm vụ với id: " + missionId));

        if (mission.getStatus() != Mission.MissionStatus.IN_PROGRESS) {
            throw new BadRequestException("Chỉ có thể báo cáo cho nhiệm vụ đang ở trạng thái IN_PROGRESS");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("Người dùng chưa được xác thực");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email: " + email));

        Report report = new Report();
        report.setMission(mission);
        report.setUser(user);
        report.setPeopleRescued(request.getPeopleRescued());
        report.setSummary(request.getSummary());
        report.setObstacles(request.getObstacles());
        report.setCreatedAt(LocalDateTime.now());

        Report saved = reportRepository.save(report);

        // Tự động cập nhật trạng thái nhiệm vụ thành COMPLETED
        mission.setStatus(Mission.MissionStatus.COMPLETED);
        mission.setEndTime(LocalDateTime.now());
        missionRepository.save(mission);

        // Tự động giải phóng phương tiện sau khi nhiệm vụ hoàn thành
        missionVehicleRepository.findByMissionId(mission.getId()).forEach(mv -> {
            com.floodrescue.backend.manager.model.Vehicle vehicle = mv.getVehicle();
            if (vehicle != null
                    && vehicle.getStatus() == com.floodrescue.backend.manager.model.Vehicle.VehicleStatus.IN_USE) {
                vehicle.setStatus(com.floodrescue.backend.manager.model.Vehicle.VehicleStatus.AVAILABLE);
                vehicleRepository.save(vehicle);
            }
        });

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MissionReportDetailResponse> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MissionReportDetailResponse> getReportsForCurrentTeam() {
        User currentUser = getCurrentUser();
        TeamMember member = teamMemberRepository.findFirstByUser_Id(currentUser.getId())
                .orElseThrow(() -> new UnauthorizedAccessException("Người dùng không thuộc bất kỳ đội cứu hộ nào."));

        List<Integer> missionIds = missionAssignmentRepository.findByRescueTeam_Id(member.getRescueTeam().getId())
                .stream()
                .map(MissionAssignment::getMission)
                .filter(Objects::nonNull)
                .map(Mission::getId)
                .distinct()
                .collect(Collectors.toList());

        if (missionIds.isEmpty()) {
            return Collections.emptyList();
        }

        return reportRepository.findByMission_IdInOrderByCreatedAtDesc(missionIds)
                .stream()
                .map(this::mapToDetailResponse)
                .collect(Collectors.toList());
    }

    private MissionReportResponse mapToResponse(Report report) {
        User reporter = report.getUser();
        return new MissionReportResponse(
                report.getId(),
                report.getMission().getId(),
                reporter != null ? reporter.getId() : null,
                reporter != null ? reporter.getFullName() : null,
                report.getPeopleRescued(),
                report.getSummary(),
                report.getObstacles(),
                report.getCreatedAt()
        );
    }

    private MissionReportDetailResponse mapToDetailResponse(Report report) {
        if (report == null) {
            return null;
        }
        Mission mission = report.getMission();
        User reporter = report.getUser();
        Integer missionId = mission != null ? mission.getId() : null;

        return new MissionReportDetailResponse(
                report.getId(),
                missionId,
                mission != null && mission.getRequest() != null ? mission.getRequest().getId() : null,
                mission != null ? mission.getMissionType() : null,
                mission != null ? mission.getStatus() : null,
                mission != null ? mission.getStartTime() : null,
                mission != null ? mission.getEndTime() : null,
                mission != null ? mission.getCreatedAt() : null,
                reporter != null ? reporter.getId() : null,
                reporter != null ? reporter.getFullName() : null,
                report.getPeopleRescued(),
                report.getSummary(),
                report.getObstacles(),
                report.getCreatedAt(),
                mapMissionVehicles(missionId),
                mapMissionSupplies(missionId)
        );
    }

    private List<MissionDetailResponse.VehicleInfo> mapMissionVehicles(Integer missionId) {
        if (missionId == null) {
            return Collections.emptyList();
        }
        return missionVehicleRepository.findByMissionId(missionId).stream()
                .map(this::mapVehicleInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private MissionDetailResponse.VehicleInfo mapVehicleInfo(MissionVehicle mv) {
        if (mv == null || mv.getVehicle() == null) {
            return null;
        }
        return new MissionDetailResponse.VehicleInfo(
                mv.getId(),
                mv.getVehicle().getVehicleId(),
                mv.getVehicle().getVehicleType().getId(),
                mv.getVehicle().getModel(),
                mv.getVehicle().getLicensePlate(),
                mv.getVehicle().getCapacityPerson(),
                mv.getVehicle().getStatus()
        );
    }

    private List<MissionDetailResponse.SupplyInfo> mapMissionSupplies(Integer missionId) {
        if (missionId == null) {
            return Collections.emptyList();
        }
        return missionSupplyRepository.findByMissionId(missionId).stream()
                .map(this::mapSupplyInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private MissionDetailResponse.SupplyInfo mapSupplyInfo(MissionSupply ms) {
        if (ms == null || ms.getInventory() == null || ms.getInventory().getItem() == null) {
            return null;
        }
        return new MissionDetailResponse.SupplyInfo(
                ms.getId(),
                ms.getInventory().getId(),
                ms.getInventory().getItem().getId(),
                ms.getInventory().getItem().getName(),
                ms.getInventory().getItem().getItemType(),
                ms.getQuantity(),
                ms.getInventory().getWarehouse() != null ? ms.getInventory().getWarehouse().getId() : null
        );
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("Người dùng chưa được xác thực");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email: " + email));
    }
}
