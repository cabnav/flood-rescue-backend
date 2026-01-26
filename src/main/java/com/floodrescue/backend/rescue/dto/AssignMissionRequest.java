package com.floodrescue.backend.rescue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignMissionRequest {
    private Integer rescueTeamId;
    private String missionRole;
    private String notes;
}
