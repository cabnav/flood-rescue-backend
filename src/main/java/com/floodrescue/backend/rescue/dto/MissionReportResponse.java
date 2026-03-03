package com.floodrescue.backend.rescue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionReportResponse {
    private Integer id;
    private Integer missionId;
    private Integer reporterId;
    private String reporterName;
    private Integer peopleRescued;
    private String summary;
    private String obstacles;
    private LocalDateTime reportedAt;
}

