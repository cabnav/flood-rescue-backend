package com.floodrescue.backend.rescue.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignVehicleRequest {
    @NotNull(message = "vehicleId không được để trống")
    private Integer vehicleId;
}
