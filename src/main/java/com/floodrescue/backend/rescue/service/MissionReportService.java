package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.rescue.dto.MissionReportDetailResponse;
import com.floodrescue.backend.rescue.dto.MissionReportRequest;
import com.floodrescue.backend.rescue.dto.MissionReportResponse;

import java.util.List;

public interface MissionReportService {
    MissionReportResponse createReport(Integer missionId, MissionReportRequest request);

    List<MissionReportDetailResponse> getAllReports();

    List<MissionReportDetailResponse> getReportsForCurrentTeam();
}
