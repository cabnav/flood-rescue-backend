package com.floodrescue.backend.manager.dto;

import com.floodrescue.backend.manager.model.Vehicle;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequest {

    private Integer depotId;

    @NotBlank(message = "Vehicle type is required")
    private String type;

    private String model;

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    @NotNull(message = "Capacity (persons) is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacityPerson;

    @NotNull(message = "Status is required")
    private Vehicle.VehicleStatus status;
}

