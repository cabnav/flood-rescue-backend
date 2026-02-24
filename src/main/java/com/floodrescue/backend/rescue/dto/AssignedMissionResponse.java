package com.floodrescue.backend.rescue.dto;

import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.rescue.model.MissionAssignment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignedMissionResponse {
    private Integer assignmentId;
    private MissionAssignment.AssignmentStatus status;
    private MissionDetailResponse mission;
    private RequestInfo request;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestInfo {
        private Integer id;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private Request.Priority priority;
    }
}

