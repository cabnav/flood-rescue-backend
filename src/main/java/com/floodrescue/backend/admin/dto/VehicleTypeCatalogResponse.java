package com.floodrescue.backend.admin.dto;

import com.floodrescue.backend.manager.model.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleTypeCatalogResponse {
    private Integer id;
    private String name;
    private String status;

    public static VehicleTypeCatalogResponse from(VehicleType vehicleType) {
        return new VehicleTypeCatalogResponse(
                vehicleType.getId(),
                vehicleType.getName(),
                vehicleType.getStatus()
        );
    }
}
