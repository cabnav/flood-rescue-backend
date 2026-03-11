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
public class MissionDetailResponse {
    private Integer id;
    private Integer requestId;
    private Mission.MissionType missionType;
    private Mission.MissionStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private List<VehicleInfo> vehicles;
    private List<SupplyInfo> supplies;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleInfo {
        private Integer id;
        private String type;
        private String model;
        private String licensePlate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplyInfo {
        private Integer inventoryId;
        private String itemName;
        private Integer quantity;
    }
}
