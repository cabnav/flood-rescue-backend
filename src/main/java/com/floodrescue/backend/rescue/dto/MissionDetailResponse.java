package com.floodrescue.backend.rescue.dto;

import com.floodrescue.backend.rescue.model.Mission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionDetailResponse {
    private Integer id;
    private Integer requestId;
    private Mission.MissionType missionType;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
}
