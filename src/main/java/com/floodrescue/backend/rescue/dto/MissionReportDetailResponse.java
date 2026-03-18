package com.floodrescue.backend.rescue.dto;

import com.floodrescue.backend.rescue.model.Mission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionReportDetailResponse {
    private Integer reportId;
    private Integer missionId;
    private Integer requestId;
    private Mission.MissionType missionType;
    private Mission.MissionStatus missionStatus;
    private LocalDateTime missionStartTime;
    private LocalDateTime missionEndTime;
    private LocalDateTime missionCreatedAt;
    private Integer reporterId;
    private String reporterName;
    private Integer peopleRescued;
    private String summary;
    private String obstacles;
    private LocalDateTime reportedAt;
    private List<MissionDetailResponse.VehicleInfo> vehicles;
    private List<MissionDetailResponse.SupplyInfo> supplies;
}

