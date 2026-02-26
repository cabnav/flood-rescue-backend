package com.floodrescue.backend.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReliefDistributionResponse {
    private Integer id;
    private Integer missionId;
    private Integer inventoryId;
    private String itemName;
    private String itemType;
    private Integer quantityDistributed;
    private String householdIdentifier;
    private Boolean isConfirmed;
    private Integer recordedById;
    private String recordedByName;
    private LocalDateTime distributedAt;
}

