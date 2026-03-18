package com.floodrescue.backend.rescue.dto;

import com.floodrescue.backend.manager.model.Item;
import com.floodrescue.backend.manager.model.Vehicle;
import com.floodrescue.backend.manager.model.VehicleType;
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
        private Integer missionVehicleId;
        private Integer vehicleId;
        private Integer vehicleTypeId;
        private String model;
        private String licensePlate;
        private Integer capacityPerson;
        private Vehicle.VehicleStatus status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplyInfo {
        private Integer missionSupplyId;
        private Integer inventoryId;
        private Integer itemId;
        private String itemName;
        private Item.ItemType itemType;
        private Integer quantity;
        private Integer warehouseId;
    }
}
