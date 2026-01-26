package com.floodrescue.backend.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVehicleRequest {
    private Integer depotId;
    private String type;
    private String model;
    private String licensePlate;
    private Integer capacityPerson;
    private String status;
}
