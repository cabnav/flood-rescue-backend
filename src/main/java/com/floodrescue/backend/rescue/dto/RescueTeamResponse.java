package com.floodrescue.backend.rescue.dto;

import com.floodrescue.backend.rescue.model.RescueTeam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RescueTeamResponse {
    private Integer id;
    private String name;
    private RescueTeam.TeamStatus status;
    private Integer quantity;
    private Integer warehouseId;
}

