package com.floodrescue.backend.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleTypeCatalogRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String status = "ACTIVE";
}
