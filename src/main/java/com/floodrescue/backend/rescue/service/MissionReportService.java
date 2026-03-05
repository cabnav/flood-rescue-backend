package com.floodrescue.backend.rescue.service;

import com.floodrescue.backend.rescue.dto.MissionReportRequest;
import com.floodrescue.backend.rescue.dto.MissionReportResponse;

public interface MissionReportService {
    MissionReportResponse createReport(Integer missionId, MissionReportRequest request);
}

