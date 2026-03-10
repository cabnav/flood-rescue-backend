package com.floodrescue.backend.rescue.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignMissionWithResourcesRequest {

    @Valid
    @NotNull(message = "Thông tin đội cứu hộ là bắt buộc")
    private AssignMissionRequest team;

    @Valid
    private AssignVehicleRequest vehicle;

    @Valid
    private List<AssignSuppliesRequest> supplies;
}

