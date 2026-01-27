package com.floodrescue.backend.manager.dto;

import com.floodrescue.backend.manager.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDetailResponse {
    private Integer vehicleId;
    private Integer depotId;
    private String type;
    private String model;
    private String licensePlate;
    private Integer capacityPerson;
    private Vehicle.VehicleStatus status;
}
