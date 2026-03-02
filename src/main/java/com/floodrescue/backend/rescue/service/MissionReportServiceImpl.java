package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.common.exception.BadRequestException;
import com.floodrescue.backend.common.exception.ResourceNotFoundException;
import com.floodrescue.backend.rescue.dto.MissionReportRequest;
import com.floodrescue.backend.rescue.dto.MissionReportResponse;
import com.floodrescue.backend.rescue.model.Mission;
import com.floodrescue.backend.rescue.model.Report;
import com.floodrescue.backend.rescue.repository.MissionRepository;
import com.floodrescue.backend.rescue.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MissionReportServiceImpl implements MissionReportService {

    private final MissionRepository missionRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
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

        return mapToResponse(saved);
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
}

